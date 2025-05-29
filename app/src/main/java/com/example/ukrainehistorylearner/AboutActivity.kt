package com.example.ukrainehistorylearner

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.ukrainehistorylearner.data.datastore.SettingsDataStore
import com.example.ukrainehistorylearner.utils.LocaleHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setupBackButton()
    }

    override fun attachBaseContext(newBase: Context?) {
        val langCode = runBlocking {
            SettingsDataStore(newBase!!).languageFlow.first()
        }
        val context = LocaleHelper.wrapContext(newBase!!, langCode)
        super.attachBaseContext(context)
    }

    private fun setupBackButton() {
        val backButton = findViewById<ImageButton>(R.id.btn_back)
        backButton.setOnClickListener {
            finish() // Закриває активність і повертає до попереднього екрана
        }
    }
}