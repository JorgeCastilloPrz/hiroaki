package com.jorgecastillo.hiroaki

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jorgecastillo.hiroaki.model.Article
import kotlinx.android.synthetic.main.item_article.view.*
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(var articles: List<Article> = ArrayList()) :
        RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(articles[pos])
    }

    override fun getItemCount() = articles.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(article: Article) {
            with(article) {
                itemView.picture.load(article.urlToImage)
                itemView.title.text = article.title

                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(article.publishedAt)
                itemView.publishedAt.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
                itemView.description.text = article.description
            }
        }
    }
}
