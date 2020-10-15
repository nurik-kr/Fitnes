package ru.nurik.fitnes.data

import android.app.*
import android.content.Context
import android.content.Intent
import android.drm.DrmStore.Playback.STOP
import android.os.Build
import androidx.core.app.NotificationCompat
import ru.nurik.fitnes.R
import ru.nurik.fitnes.ui.MyForegroundService
import ru.nurik.fitnes.ui.MyForegroundService.Companion.STOP_SERVICE_ACTION
import ru.nurik.fitnes.ui.main.MainActivity
import ru.nurik.fitnes.ui.second.MainActivity2

object NotificationHelper {

    private const val CHANNEL_ID = "my_channel"
    private const val CHANNEL_NAME = "CHANNEL_NAME"
    private const val CHANNEL_DESC = "CHANNEL_DESC"

    fun createNotification(context: Context): Notification? {
        createNotificationChannel(context) //канал для уведомления для каких то андроди и выше

//        val pIntent = TaskStackBuilder.create(context)
//            .addNextIntent(Intent(context, MainActivity::class.java))
//            .addNextIntent(Intent(context, MainActivity2::class.java))
//            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val intent = Intent(context, MyForegroundService::class.java) // создание кнопки в уведом
            intent.action = STOP_SERVICE_ACTION // передаем в интент чтоб там ловить
        val pIntent = PendingIntent.getService(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID) // создание уведомления
            .setContentTitle("ForegroundService")
            .setContentText("Service ++")
            .setContentIntent(pIntent)
            .addAction(R.drawable.ic_baseline_my_location_24, "STOP TRENING", pIntent) // добавление кнопки в уведом
            .setSmallIcon(R.drawable.ic_baseline_my_location_24)

        return builder.build()
    }

    private fun createNotificationChannel(context: Context) { // канал
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.description = CHANNEL_DESC

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}