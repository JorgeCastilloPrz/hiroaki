package com.jorgecastillo.hiroaki

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jorgecastillo.hiroaki.di.provideNewsService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupList()
    }

    private fun setupList() {
        newsList.setHasFixedSize(true)
        newsList.layoutManager = LinearLayoutManager(this)
        adapter = NewsAdapter()
        newsList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        launch(UI) {
            loading.visibility = View.VISIBLE
            val articles = NewsNetworkDataSource(provideNewsService()).getNews()
            adapter.articles = articles
            adapter.notifyDataSetChanged()
            loading.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> true
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
