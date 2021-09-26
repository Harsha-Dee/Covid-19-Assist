package com.example.covid.news

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.covid.R

class NewsActivity : AppCompatActivity(), clickListener{

    lateinit var mAdapter:NewsAdapter

    var news_url = "https://newsapi.org/v2/top-headlines?country=in&apiKey=0bfd101369b949c4923b33baae521307&category=health"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        fetchData()

    }

    fun fetchData() {
        val queue = Volley.newRequestQueue(this)
        val url = news_url
        val getRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<NewsDataClass>()
                for(i in 0 until  newsJsonArray.length()){
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = NewsDataClass(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,
                    "Something went wrong, please try again later", Toast.LENGTH_LONG)
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }
        queue.add(getRequest)
    }

    override fun onItemsClicked(items: NewsDataClass) {
        val builder =  CustomTabsIntent.Builder();
        val customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(items.url));
    }


}

