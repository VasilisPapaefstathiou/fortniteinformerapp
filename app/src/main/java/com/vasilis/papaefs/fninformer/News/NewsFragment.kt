package com.vasilis.papaefs.fninformer.News

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vasilis.papaefs.fninformer.PassClass
import com.vasilis.papaefs.fninformer.R
import org.jsoup.Jsoup
import java.lang.ref.WeakReference

data class NewsItem(val thumbnail: String, val date: String, val description: String, val link: String)

class NewsAdapter(private val context: Context, private val newsList: java.util.ArrayList<NewsItem>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.date.text = newsList[position].date
        holder.description.text = newsList[position].description

        val requestOptions = RequestOptions()
                .centerCrop()
                .placeholder(null)
                .error(null)
        Glide.with(context)
                .load(newsList[position].thumbnail)
                .apply(requestOptions)
                .into(holder.thumbnail)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, NewsActivity::class.java)
            intent.putExtra("url", newsList[position].link)
            ContextCompat.startActivity(context, intent, null)
        }
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumbnail: ImageView
        var date: TextView
        var description: TextView

        init {
            thumbnail = itemView.findViewById<View>(R.id.iv_news) as ImageView
            date = itemView.findViewById<View>(R.id.tv_news_date) as TextView
            description = itemView.findViewById<View>(R.id.tv_news_description) as TextView
        }
    }

}

class NewsFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var newsList: ArrayList<NewsItem>
    private lateinit var progressBar: ProgressBar

    // scraped lists
    private lateinit var links: ArrayList<String>
    private lateinit var dates: ArrayList<String>
    private lateinit var titles: ArrayList<String>
    private lateinit var thumbnails: ArrayList<String>

    private var exceptionHappened = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_news, container, false)

        // initialize variables
        progressBar = view.findViewById<ProgressBar>(R.id.pb_rc_news)
        newsList = ArrayList()
        links = ArrayList()
        dates = ArrayList()
        titles = ArrayList()
        thumbnails = ArrayList()
        recyclerView = view.findViewById<RecyclerView>(R.id.rc_news)


        if (PassClass.newsList == null) {
            val jsoupAsyncTask = Async(activity!!)
            jsoupAsyncTask.execute()
        } else {
            progressBar.visibility = View.GONE
            recyclerView.layoutManager = GridLayoutManager(activity, 1)
            adapter = NewsAdapter(activity!!, PassClass.newsList!!)
            recyclerView.adapter = adapter
        }

        return view
    }


    private inner class Async internal constructor(activity: Activity) : AsyncTask<Void?, Void?, Void?>() {

        private val weakReference: WeakReference<Activity>

        init {
            this.weakReference = WeakReference(activity)
        }

        override fun doInBackground(vararg voids: Void?): Void? {

            try {
                val document = Jsoup.connect("https://www.epicgames.com/fortnite/en-US/news").get()

                // get all needed elements from the website
                val featuredElements = document.select("div.top-featured-activity")
                val titleElements = document.select("h1.title")
                val dateElements = document.select("h4.date")
                val linkElements = document.select("div.row")[0].getElementsByTag("a")
                val thumbnailElements = document.select("div.row")[0].getElementsByTag("img")

                // featured
                links.add("https://www.epicgames.com" + featuredElements[0].getElementsByTag("a").attr("href").toString()) // featured
                thumbnails.add("(https).+jpg".toRegex().find(featuredElements[0].getElementsByTag("div").attr("style"))!!.value) // featured
                dates.add(featuredElements[0].getElementsByTag("p").text()) // featured
                titles.add(featuredElements[0].getElementsByTag("h2").text()) // featured

                // add news
                for (i in 0..linkElements.size - 1) {
                    links.add("https://www.epicgames.com" + linkElements[i].attr("href"))
                    thumbnails.add(thumbnailElements[i].attr("src"))
                    dates.add(dateElements[i].text())
                    titles.add(titleElements[i].text())
                    newsList.add(NewsItem(thumbnails[i], dates[i], titles[i], links[i]))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                exceptionHappened = true
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?) {

            val activity = weakReference.get()

            progressBar.visibility = View.GONE

            if (exceptionHappened == false) {
                recyclerView.layoutManager = GridLayoutManager(activity, 1)
                adapter = NewsAdapter(activity!!, newsList)
                recyclerView.adapter = adapter
                PassClass.newsList = newsList
            } else {
                Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }

            if (activity == null || activity.isFinishing)
                return

        }

    }

}