package com.udacity

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var fileName: TextView
    private lateinit var status: TextView
    private lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        fileName = findViewById(R.id.FileNameTextView)
        status = findViewById(R.id.statusTextView)
        btn = findViewById(R.id.okButton)

        fileName.text = intent.getStringExtra("FILENAME")
        status.text = intent.getStringExtra("STATUS")

        if (status.text == "Success"){
            status.setTextColor(Color.GREEN)
        }else if(status.text == "Fail"){
            status.setTextColor(Color.RED)
        }

        btn.setOnClickListener {
            finish()
        }
    }

}
