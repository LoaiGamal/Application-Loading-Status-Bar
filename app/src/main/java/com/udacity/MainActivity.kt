package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var status: String
    private lateinit var name: String

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var download: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            when (checkedRadioGroupSelected()) {
                -1 -> Toast.makeText(
                    this@MainActivity,
                    "No item selected to download",
                    Toast.LENGTH_SHORT
                ).show()

                glide.id -> {
                    custom_button.buttonState = ButtonState.Loading
                    name = getString(R.string.glide)
                    download(GLIDE_URL, "Glide")
                }

                loadApp.id -> {
                    custom_button.buttonState = ButtonState.Loading
                    name = getString(R.string.load_app)
                    download(LOADAPP_URL, "App")
                }

                retrofit.id -> {
                    custom_button.buttonState = ButtonState.Loading
                    name = getString(R.string.retrofit)
                    download(RETROFIT_URL, "Retrofit")
                }
            }
        }


//        test.setOnClickListener {
//            notificationManager = ContextCompat.getSystemService(
//                this@MainActivity,
//                NotificationManager::class.java
//            ) as NotificationManager
//            notificationManager.sendNotification(getString(R.string.notification_description), this@MainActivity)
//        }

//        test.setOnClickListener {
//            Log.i("Loai", checkedRadioGroupSelected().toString())
//            if(checkedRadioGroupSelected() == glide.id)
//                Toast.makeText(
//                    this@MainActivity,
//                    "No item selected to download",
//                    Toast.LENGTH_SHORT
//                ).show()
//        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                // Reset download state
                custom_button.buttonState = ButtonState.Completed

                val query = DownloadManager.Query().setFilterById(id)
                val downloadManager =
                    context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val statusValue =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    status = if (statusValue == DownloadManager.STATUS_SUCCESSFUL) {
                        "Success"
                    } else {
                        "Fail"
                    }
                }
            }

            val detailIntent = Intent(applicationContext, DetailActivity::class.java)
            detailIntent.putExtra("FILENAME", name)
            detailIntent.putExtra("STATUS", status)

            pendingIntent = PendingIntent.getActivity(
                applicationContext,
                NOTIFICATION_ID,
                detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            notificationManager = ContextCompat.getSystemService(
                this@MainActivity,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.sendNotification(
                getString(R.string.notification_description),
                this@MainActivity,
                pendingIntent
            )
        }

    }

    private fun download(url: String, title: String) {

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
//                .setDescription(getString(R.string.app_description))
                .setDescription(title)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        download = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            download.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
    }

    private fun createChannel(channelID: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                applicationContext.getString(R.string.notification_description)
            notificationChannel.lightColor = Color.BLUE

            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun checkedRadioGroupSelected(): Int {
        return radioGroup.checkedRadioButtonId
    }

}
