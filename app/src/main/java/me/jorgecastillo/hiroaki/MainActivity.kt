package me.jorgecastillo.hiroaki

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.loading
import kotlinx.android.synthetic.main.activity_main.newsList
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.jorgecastillo.hiroaki.data.datasource.MoshiNewsNetworkDataSource

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
            val articles = MoshiNewsNetworkDataSource(getApp().newsService())
                    .getNews()
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
