package com.example.ukrainehistorylearner

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ukrainehistorylearner.data.datastore.SettingsDataStore
import com.example.ukrainehistorylearner.data.repository.ArticlesRepository
import com.example.ukrainehistorylearner.data.repository.ArticlesRepositoryImpl
import com.example.ukrainehistorylearner.ui.adapters.ArticleAdapter
import com.example.ukrainehistorylearner.utils.ArticleDataBase
import com.example.ukrainehistorylearner.utils.LocaleHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ArticlesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter

    private val repository: ArticlesRepository by lazy {
        val database = ArticleDataBase.getDatabase(application)
        ArticlesRepositoryImpl(database.articleEntryDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articles)

        setupBackButton()

        recyclerView = findViewById(R.id.recyclerViewArticles)
        recyclerView.layoutManager = LinearLayoutManager(this)

//        val articles = listOf(
//            HistoricalArticle(
//                title = "Київська Русь",
//                period = HistoricalPeriod.KYIV_RUS,
//                author = "Нестор Літописець",
//                wordCount = 900,
//                tags = listOf("давня історія", "релігія")
//            ),
//            HistoricalArticle(
//                title = "Проголошення незалежності",
//                period = HistoricalPeriod.INDEPENDENCE,
//                author = "Леонід Кравчук",
//                wordCount = 1500,
//                tags = listOf("1991", "державність")
//            )
//        )
//
//
//        adapter = ArticleAdapter(articles, this)
//        recyclerView.adapter = adapter

        loadArticles()
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

    private fun loadArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.refreshArticles()
            val articles = repository.articles.value

            withContext(Dispatchers.Main) {
                adapter = ArticleAdapter(articles, this@ArticlesActivity)
                recyclerView.adapter = adapter
            }
        }
    }
}
