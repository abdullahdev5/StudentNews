package com.android.studentnews.main.events

import android.Manifest
import com.android.studentnews.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.util.fastJoinToString
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.imageLoader
import coil.request.ImageRequest
import com.android.studentnews.MainActivity
import com.android.studentnews.NotificationRelated
import com.android.studentnews.core.domain.common.formatDateToDay
import com.android.studentnews.core.domain.common.formatDateToMonthName
import com.android.studentnews.core.domain.common.formatDateToYear
import com.android.studentnews.core.domain.common.formatTimeToString
import com.android.studentnews.main.MyBroadcastReceiver
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnews.main.news.NOTIFICATION_ID
import com.android.studentnews.core.domain.common.getUrlOfImageNotVideo
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import kotlin.random.Random


const val TITLE = "title"
const val DESCRIPTION = "description"
const val ADDRESS = "address"
const val EVENT_ID = "eventId"
const val STARTING_DATE = "startingDate"
const val STARTING_TIME_HOUR = "startingTimeHour"
const val STARTING_TIME_MINUTES = "startingTimeMinutes"
const val STARTING_TIME_STATUS = "startingTimeStatus"
const val ENDING_DATE = "endingDate"
const val ENDING_TIME_HOUR = "endingTimeHour"
const val ENDING_TIME_MINUTES = "endingTimeMinutes"
const val ENDING_TIME_STATUS = "endingTimeStatus"
const val URL_LIST = "urlList"
const val BOOKINGS = "bookings"
const val IS_AVAILABLE = "isAvailable"


const val SAVED_EVENT_ACTION = "com.android.studentnews.SAVED_EVENT_ACTION"
const val EVENTS_URI = "https://www.events.com/"
const val EVENTS_REGISTRATION_URI = "https://www.events_registration.com/"


class EventsWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters,
    private val eventsRepository: EventsRepository,
    private val notificationManager: NotificationManagerCompat,
) : CoroutineWorker(context, workerParameters) {


    override suspend fun doWork(): Result {

        return try {
            val event = eventsRepository.getEventsUpdates()
            val currentUserName = eventsRepository.getCurrentUserName()

            showNotification(event, currentUserName)

            Result.success()

        } catch (e: Exception) {
            Result.failure()
        }
    }


    private fun showNotification(event: EventsModel?, currentUserName: String) {

        val imageRequest = ImageRequest.Builder(context)
            .data(getUrlOfImageNotVideo(event?.urlList ?: emptyList()))
            .allowHardware(false)
            .target { drawable ->
                val bitmap = drawable.toBitmap()

                val notificationId = Random.nextInt()
                val clickedIntentRequestId = Random.nextInt()
                val savedIntentRequestId = Random.nextInt()
                val registrationIntentRequestId = Random.nextInt()

                val flag = PendingIntent.FLAG_MUTABLE
                val savedFlag =
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT

                val serializedUrlList = event?.urlList?.fastJoinToString(",") {
                    "${it.url};${it.contentType};${it.sizeBytes};${it.lastPathSegment}"
                } ?: ""

                val serializedBookingList = event?.bookings?.fastJoinToString(",") {
                    "${it.userId};${it.userName};${it.userDegree};${it.userPhoneNumber};${it.userCity};${it.userAddress};${it.userProfilePic};${it.userProfilePicBgColor}"
                } ?: ""


                val clickedIntent = Intent(
                    Intent.ACTION_VIEW,
                    "$EVENTS_URI/eventId=${event?.eventId ?: ""}".toUri(),
                    context,
                    MainActivity::class.java,
                )

                val savedClickedIntent = Intent(
                    context,
                    MyBroadcastReceiver::class.java
                ).apply {
                    action = SAVED_EVENT_ACTION
                    putExtra(TITLE, event?.title ?: "")
                    putExtra(DESCRIPTION, event?.description ?: "")
                    putExtra(ADDRESS, event?.address ?: "")
                    putExtra(EVENT_ID, event?.eventId ?: "")
                    putExtra(STARTING_DATE, event?.startingDate ?: 0L)
                    putExtra(STARTING_TIME_HOUR, event?.startingTimeHour ?: 0)
                    putExtra(STARTING_TIME_MINUTES, event?.startingTimeMinutes ?: 0)
                    putExtra(STARTING_TIME_STATUS, event?.startingTimeStatus ?: "")
                    putExtra(ENDING_DATE, event?.endingDate ?: 0L)
                    putExtra(ENDING_TIME_HOUR, event?.endingTimeHour ?: 0)
                    putExtra(ENDING_TIME_MINUTES, event?.endingTimeMinutes ?: 0)
                    putExtra(ENDING_TIME_STATUS, event?.endingTimeStatus ?: "")
                    putExtra(URL_LIST, serializedUrlList)
                    putExtra(BOOKINGS, serializedBookingList)
                    putExtra(IS_AVAILABLE, event?.isAvailable ?: true)
                    putExtra(NOTIFICATION_ID, notificationId)
                }


                val registrationIntent = Intent(
                    Intent.ACTION_VIEW,
                    ("$EVENTS_REGISTRATION_URI/eventId=${event?.eventId ?: ""}" +
                            "/isComeForRegistration=${true}" + "/notificationId=$notificationId"
                            ).toUri(),
                    context,
                    MainActivity::class.java,
                )


                val clickedPendingIntent = PendingIntent.getActivity(
                    context,
                    clickedIntentRequestId,
                    clickedIntent,
                    flag
                )

                val savedPendingIntent = PendingIntent.getBroadcast(
                    context,
                    savedIntentRequestId,
                    savedClickedIntent,
                    savedFlag
                )

                val registrationPendingIntent = PendingIntent.getActivity(
                    context,
                    registrationIntentRequestId,
                    registrationIntent,
                    flag
                )


                // Starting
                val startingDay = formatDateToDay(event?.startingDate ?: 0L)
                val startingMonthName = formatDateToMonthName(event?.startingDate ?: 0L)
                val startingYear = formatDateToYear(event?.startingDate ?: 0L)
                val startingTime = "${
                    formatTimeToString(
                        event?.startingTimeHour ?: 0,
                        event?.startingTimeMinutes ?: 0
                    ).dropLast(2)
                } ${event?.startingTimeStatus}"

                // Ending
                val endingDay = formatDateToDay(event?.endingDate ?: 0L)
                val endingMonthName = formatDateToMonthName(event?.endingDate ?: 0L)
                val endingYear = formatDateToYear(event?.endingDate ?: 0L)
                val endingTime = "${
                    formatTimeToString(
                        event?.endingTimeHour ?: 0,
                        event?.endingTimeMinutes ?: 0
                    ).dropLast(2)
                } ${event?.endingTimeStatus}"
                val isAvailable =
                    if ((event?.isAvailable ?: true)) "Available" else "UnAvailable"


                val notification =
                    NotificationCompat.Builder(context, NotificationRelated.EVENTS_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Hey $currentUserName, New Event is Here. Register?")
                        .setContentText(event?.title ?: "")
                        .setLargeIcon(bitmap)
                        .setContentIntent(clickedPendingIntent)
                        .setAutoCancel(true)
                        .setStyle(
                            NotificationCompat.InboxStyle()
                                .addLine("Starting Date: $startingDay, $startingMonthName, $startingYear")
                                .addLine("Starting Time: $startingTime")
                                .addLine("Ending Date: $endingDay, $endingMonthName, $endingYear")
                                .addLine("Ending Time: $endingTime")
                                .addLine("Status: $isAvailable")
                        )
                        .addAction(
                            NotificationCompat.Action(
                                null,
                                HtmlCompat.fromHtml(
                                    "<font color=\"" + ContextCompat.getColor(
                                        context,
                                        R.color.green
                                    )
                                            + "\">" + "Save" + "</font>",
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                ),
                                savedPendingIntent
                            )
                        )
                        .addAction(
                            NotificationCompat.Action(
                                null,
                                HtmlCompat.fromHtml(
                                    "<font color=\"" + ContextCompat.getColor(
                                        context,
                                        R.color.green
                                    )
                                            + "\">" + "Register" + "</font>",
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                ),
                                registrationPendingIntent,
                            )
                        )
                        .build()

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                notificationManager.notify(
                    notificationId,
                    notification
                )
            }
            .build()
        context.imageLoader.enqueue(imageRequest)

    }

}