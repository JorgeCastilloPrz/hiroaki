package me.jorgecastillo.hiroaki

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_article.view.*
import me.jorgecastillo.hiroaki.NewsAdapter.ViewHolder
import me.jorgecastillo.hiroaki.model.Article
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.ArrayList

class NewsAdapter(var articles: List<Article> = ArrayList()) :
    RecyclerView.Adapter<ViewHolder>() {

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
      itemView.picture.load(article.urlToImage)
      itemView.title.text = article.title

      val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(article.publishedAt)
      itemView.publishedAt.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
      itemView.description.text = article.description
    }
  }
}
