package com.vasilis.papaefs.fninformer.News

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.vasilis.papaefs.fninformer.R


class NewsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val webView = findViewById<WebView>(R.id.wv_news)
        val progressBar = findViewById<ProgressBar>(R.id.pb_news)

        // remove header/footer from page then display page
        // hide progressbar
        val client = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:(function() { " +
                        "document.getElementById('egh').style.display='none'; })()");
                view.loadUrl("javascript:(function() { " +
                        "document.getElementById('egf').style.display='none'; })()");
                progressBar.visibility = View.GONE
                view.visibility = View.VISIBLE
            }
        }

        // webview options
        webView.webViewClient = client
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(intent.extras.getString("url"))
        webView.visibility = View.INVISIBLE

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}