package com.example.fundo.common

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fundo.R
import com.example.fundo.data.room.DateTypeConverters
import com.example.fundo.ui.note.NoteActivity

class NotifyWorker(private val context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val id = inputData.getLong("id", 0)
        val title = inputData.getString("title")
        val content = inputData.getString("content")
        val archived = inputData.getBoolean("archived", false)
        val reminder = DateTypeConverters().toDateTime(inputData.getString("reminder"))

        val intent = Intent(context, NoteActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        intent.putExtra("archived", archived)
        intent.putExtra("reminder", reminder)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_splash)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)

        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "Reminder Notify Channel";
        const val NOTIFICATION_ID = 1;
    }
}