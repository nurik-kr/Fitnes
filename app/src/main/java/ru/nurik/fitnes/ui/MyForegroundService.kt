package ru.nurik.fitnes.ui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.nurik.fitnes.data.NotificationHelper

class MyForegroundService : Service() { // маин потоке ,явл бэкраунд убивается системой(уведом),не уничтожится пока мы сами его не остановим
    override fun onBind(intent: Intent?): IBinder? { // привязонный сервис
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP_SERVICE_ACTION) {
            stopForeground(true)
        } else {
            startForeground(1, NotificationHelper.createNotification(applicationContext))
            test()
        }
        return START_REDELIVER_INTENT
    }

    private fun test() {
        GlobalScope.launch(Dispatchers.IO) {
            for (i in 0..10000) {
                Log.d("______test", i.toString())
                delay(200)
            }
        }
    }

    companion object {
        const val STOP_SERVICE_ACTION = "STOP_SERVICE_ACTION"
    }
}