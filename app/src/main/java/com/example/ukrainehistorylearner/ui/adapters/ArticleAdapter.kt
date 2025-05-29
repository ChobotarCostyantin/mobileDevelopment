package com.example.ukrainehistorylearner.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.model.HistoricalArticle

class ArticleAdapter(
    private val articles: List<HistoricalArticle>,
    private val context: Context
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textPeriod: TextView = itemView.findViewById(R.id.textPeriod)
        val textAuthor: TextView = itemView.findViewById(R.id.textAuthor)
        val textWordCount: TextView = itemView.findViewById(R.id.textWordCount)
        val textReadTime: TextView = itemView.findViewById(R.id.textReadTime)
        val textTags: TextView = itemView.findViewById(R.id.textTags)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.textTitle.text = article.title
        holder.textPeriod.text = "${context.getString(R.string.period)}: ${article.period.getYearRange(context)}"
        holder.textAuthor.text = "${context.getString(R.string.article_author)}: ${article.author}"
        holder.textWordCount.text = "${context.getString(R.string.article_word_count)}: ${article.wordCount}"
        holder.textReadTime.text = "${context.getString(R.string.article_read_time)}: ~${article.getReadTime()}"
        holder.textTags.text = "${context.getString(R.string.article_tags)}: ${article.tags.joinToString(", ")}"
    }

    override fun getItemCount(): Int = articles.size
}
