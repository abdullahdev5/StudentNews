package com.android.studentnewsadmin.main.news.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.android.studentnewsadmin.core.domain.constants.FirestoreNodes
import com.android.studentnewsadmin.core.domain.constants.StorageNodes
import com.android.studentnewsadmin.core.domain.resource.CategoryState
import com.android.studentnewsadmin.core.domain.resource.NewsState
import com.android.studentnewsadmin.main.news.data.worker.CATEGORY
import com.android.studentnewsadmin.main.news.data.worker.DESCRIPTION
import com.android.studentnewsadmin.main.news.data.worker.LINK
import com.android.studentnewsadmin.main.news.data.worker.LINK_TITLE
import com.android.studentnewsadmin.main.news.data.worker.NewsWorker
import com.android.studentnewsadmin.main.news.data.worker.NewsSuccessNotificationWorker
import com.android.studentnewsadmin.main.news.data.worker.TITLE
import com.android.studentnewsadmin.main.news.data.worker.URI_LIST
import com.android.studentnewsadmin.main.news.domain.model.CategoryModel
import com.android.studentnewsadmin.main.news.domain.model.NewsModel
import com.android.studentnewsadmin.main.news.domain.model.UrlList
import com.android.studentnewsadmin.main.news.domain.repository.NewsRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.ByteArrayOutputStream

class NewsRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val workManager: WorkManager,
) : NewsRepository {


    override val newsColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.NEWS_COL)

    override val storageRef: StorageReference?
        get() = storage.reference


    override fun onNewsAdd(
        title: String,
        description: String,
        uriList: List<Uri>,
        category: String,
        link: String,
        linkTitle: String,
        context: Context
    ): Flow<NewsState<String>> = callbackFlow {

        val newsId = newsColRef?.document()?.id

        val urlsList = mutableStateListOf<UrlList>()

        var int = mutableStateOf(0)

        val fileRef = storageRef
            ?.child("news")?.child(newsId.toString())?.child("files")

        uriList.forEach { file ->

            val inputBytes = context
                .contentResolver
                .openInputStream(file)
                .use { stream ->
                    stream?.readBytes()
                }

            var uploadTask = inputBytes?.let {
                fileRef
                    ?.child("${int.value++}")?.putBytes(it)
            }

            uploadTask
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uri = task.result.storage.downloadUrl

                        uri
                            .addOnSuccessListener { imageUri ->

                                urlsList.add(
                                    UrlList(
                                        url = imageUri.toString(),
                                        contentType = task.result.metadata?.contentType
                                            ?: "",
                                        sizeBytes = task.result.metadata?.sizeBytes ?: 0L
                                    )
                                )

                                if (urlsList.size == uriList.size) {

                                    val news = NewsModel(
                                        title = title,
                                        description = description,
                                        newsId = newsId.toString(),
                                        category = category,
                                        link = link,
                                        linkTitle = linkTitle,
                                        timestamp = Timestamp.now(),
                                        urlList = urlsList
                                    )

                                    newsColRef
                                        ?.document(newsId.toString())
                                        ?.set(news)
                                        ?.addOnSuccessListener { document ->
                                            trySend(NewsState.Success("News Added Successfully"))
                                        }
                                        ?.addOnFailureListener {
                                            trySend(NewsState.Failed(it))
                                        }

                                }
                            }

                    }
                }
                ?.addOnProgressListener {
                    val progress = 1.0 * it.bytesTransferred / it.totalByteCount
                }
                ?.addOnFailureListener {
                    trySend(NewsState.Failed(it))
                }
                ?.addOnCanceledListener {
                    uploadTask.cancel()
                }
                ?.addOnPausedListener {
                    uploadTask.pause()
                }

        }

        awaitClose {
            close()
        }
    }

    override fun getNewsList(): Flow<NewsState<List<NewsModel>>> =
        callbackFlow {

            trySend(NewsState.Loading)

            val snapshotListener = firestore
                .collection(FirestoreNodes.NEWS_COL)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null) {
                        val news = documents.map {
                            it.toObject(NewsModel::class.java)
                        }
                        trySend(NewsState.Success(news))
                    }
                }
                .addOnFailureListener { error ->
                    trySend(NewsState.Failed(error))
                }


            awaitClose {
                close()
            }
        }

    override fun onNewsDelete(newsId: String): Flow<NewsState<String>> {
        return callbackFlow {

            trySend(NewsState.Loading)

            val newsRef = storageRef
                ?.child("news")
                ?.child(newsId)
                ?.child("files")

            newsRef
                ?.listAll()
                ?.addOnSuccessListener { listResult ->
                    listResult.items.forEach { item ->
                        item
                            .delete()
                            .addOnSuccessListener {
                                val newsDocRef = newsColRef
                                    ?.document(newsId)
                                newsDocRef
                                    ?.delete()
                                    ?.addOnSuccessListener {
                                        trySend(NewsState.Success("News Deleted Successfully"))
                                    }
                                    ?.addOnFailureListener {
                                        trySend(NewsState.Failed(it))
                                    }
                            }
                    }
                }
            awaitClose {
                close()
            }
        }
    }

    override fun onCategoryAdd(
        category: String,
        imageBitmap: Bitmap
    ): Flow<CategoryState<String>> = callbackFlow {

        trySend(CategoryState.Loading)

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInByte = baos.toByteArray()

        val categoryId = firestore
            .collection(FirestoreNodes.CATEGORIES_COL)
            .document()
            .id

        val imageRef =
            storageRef?.child(StorageNodes.CATEGORY_IMAGES_COL)?.child(categoryId)

        val uploadTask = imageRef?.putBytes(imageInByte)

        uploadTask
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uri = task.result.storage.downloadUrl

                    uri
                        .addOnSuccessListener { imageUri ->
                            val category = CategoryModel(
                                name = category,
                                imageUrl = imageUri.toString(),
                                categoryId = categoryId,
                                timestamp = Timestamp.now()
                            )

                            firestore
                                .collection(FirestoreNodes.CATEGORIES_COL)
                                .document(categoryId)
                                .set(category)
                                .addOnSuccessListener {
                                    trySend(CategoryState.Success("Category Added Successfully"))
                                }

                        }

                }
            }
            ?.addOnProgressListener {
                val progress = 1.0f * it.bytesTransferred / it.totalByteCount
                trySend(CategoryState.Progress(progress))
            }
            ?.addOnFailureListener {
                trySend(CategoryState.Failed(it))
            }

        awaitClose {
            close()
        }
    }

    override fun getCategoryList(): Flow<CategoryState<List<CategoryModel>>> =
        callbackFlow {

            trySend(CategoryState.Loading)

            val snapshotListener = firestore
                .collection(FirestoreNodes.CATEGORIES_COL)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(CategoryState.Failed(error))
                    }

                    if (snapshot != null) {
                        val categoryList = snapshot.map {
                            it.toObject(CategoryModel::class.java)
                        }
                        trySend(CategoryState.Success(categoryList))
                    }
                }

            awaitClose {
                snapshotListener.remove()
            }
        }

    @SuppressLint("EnqueueWork")
    override fun startNewsAddingWorker(
        title: String,
        description: String,
        stringArray: Array<String>,
        category: String,
        link: String,
        linkTitle: String
    ) {

        val inputData = Data.Builder()
            .putString(TITLE, title)
            .putString(DESCRIPTION, description)
            .putStringArray(URI_LIST, stringArray)
            .putString(CATEGORY, category)
            .putString(LINK, link)
            .putString(LINK_TITLE, linkTitle)
            .build()

        val workRequest = OneTimeWorkRequest.Builder(
            NewsWorker::class.java,
        )
            .setInputData(inputData)
            .build()

        val notificationWorkRequest = OneTimeWorkRequest.Builder(
            NewsSuccessNotificationWorker::class.java
        ).build()

        workManager
            .beginUniqueWork(
                "news_adding_work",
                ExistingWorkPolicy.KEEP,
                workRequest
            )
            .then(notificationWorkRequest)
            .enqueue()


    }
}