package com.android.studentnews

import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.events.data.repository.EventsRepositoryImpl
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnews.main.events.ui.viewModels.EventsDetailViewModel
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.inject
import kotlin.test.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest : KoinTest {

    //    private lateinit var eventsRepository: EventsRepository
    private val eventsDetailViewModel: EventsDetailViewModel by inject()
    private lateinit var mockkSavedEventsDocRef: DocumentReference
    private lateinit var mockkEventsColRef: CollectionReference
    private lateinit var mockkFirestore: FirebaseFirestore
    private lateinit var mockkAuth: FirebaseAuth
    private lateinit var mockkUser: FirebaseUser


    private val testUserId = "MLvN2rURj5SmAetAGwB3qbVm3352"
    private val testEventId = "25xOTyM10UqIsR0Ht8Xc"


    val testModule = module {
        single { mockk<FirebaseFirestore>(relaxed = true) }
        single { mockk<FirebaseAuth>(relaxed = true) }
        single { mockk<WorkManager>(relaxed = true) }
        singleOf(::EventsRepositoryImpl) { bind<EventsRepository>() }
        single { mockk<NotificationManagerCompat>() }
        single { mockk<FirebaseUser>() }
        viewModelOf(::EventsDetailViewModel)
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
//        androidLogger()
        modules(testModule)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        mockkFirestore = get()
        mockkAuth = get()
        mockkUser = get()
        mockkSavedEventsDocRef = mockk()
        mockkEventsColRef = mockk()

        Dispatchers.setMain(testDispatcher)

        println("Print Line: on setup()")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test onEventSave`(): Unit = runTest(testDispatcher) {

        every { mockkAuth.currentUser } returns mockkUser
        every { mockkUser.uid } returns testUserId

        every {
            mockkFirestore
                .collection(FirestoreNodes.EVENTS_COL)
        } returns mockkEventsColRef

        every {
            mockkFirestore
                .collection(FirestoreNodes.USERS_COL)
                .document(any())
                .collection(FirestoreNodes.SAVED_EVENTS)
                .document(any())
        } returns mockkSavedEventsDocRef

        val mockkEventDocumentSnapshot = mockk<DocumentSnapshot>()
        val mockkEventModel = mockk<EventsModel>()

        val mockkTaskBefore = mockk<Task<DocumentSnapshot>>()

        every {
            mockkEventsColRef
                .document(any())
                .get()
        } returns mockkTaskBefore

        val mockkTask = mockk<Task<Void>>()

        // For Success
        every {
            mockkSavedEventsDocRef.set(any())
        } returns mockkTask

        every { mockkTask.isComplete } returns true
        every { mockkTask.isCanceled } returns false
        every {
            mockkTask
                .addOnSuccessListener(any())
        } answers {
            (firstArg<OnSuccessListener<Void>>()).onSuccess(null)
            mockk<Task<Void>>()
        }


        // For Failure
        every {
            mockkSavedEventsDocRef.set(any())
        } returns mockkTask

        every {
            mockkTask
                .addOnFailureListener(any())
        } answers {
            (firstArg<OnFailureListener>()).onFailure(mockk())
            mockk<Task<Void>>()
        }

//        val spyViewModel = spyk(eventsDetailViewModel, recordPrivateCalls = true)


        var resultStatus = ""

        println("Print Line: on test onEventSave()")
//
        resultStatus = eventsDetailViewModel.onEventSave(testEventId)
        println("Print Line: Result: $resultStatus")

//        coVerify(exactly = 1) {
//            eventsDetailViewModel.onEventSave(testEventId)
//        }
//        confirmVerified(eventsDetailViewModel)

        verify(exactly = 1) {
            mockkUser.uid
        }

        verify(exactly = 1) {
            mockkFirestore
                .collection(FirestoreNodes.EVENTS_COL)
        }

        verify(exactly = 1) {
            mockkFirestore
                .collection(FirestoreNodes.USERS_COL)
                .document(any())
                .collection(FirestoreNodes.SAVED_EVENTS)
                .document(any())
        }

        verify(exactly = 1) {
            mockkEventsColRef
                .document(any())
                .get()
        }

//        verify(exactly = 1) {
//            mockkDocumentReference
//        }
//
//        verify(exactly = 1) {
//            mockkTask
//        }

        assertTrue(resultStatus == Status.SUCCESS)

//        launch {
//            // Create an instance of the function being tested
//            val flow = eventsRepository.onEventSave(testEventId)
//
//            // Collect the emitted values from the flow
//            val emittedStates = mutableListOf<EventsState<String>>()
//            flow.collect { emittedStates.add(it) }
//
//            // Check that the flow emitted the correct states
//            assertTrue(emittedStates.contains(EventsState.Loading)) // Should first emit Loading
//            assertTrue(emittedStates.contains(EventsState.Success("Event Saved Successfully"))) // Should emit Success after set() completes
//        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun finish() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        stopKoin()
    }
}

inline fun <reified T> mockkTask(result: T?, exception: Exception? = null): Task<T> {
    val task: Task<T> = mockk(relaxed = true)
    every { task.isComplete } returns true
    every { task.exception } returns exception
    every { task.isCanceled } returns false
    val relaxedT: T = mockk(relaxed = true)
    every { task.result } returns result
    return task
}