package com.vasilis.papaefs.fninformer

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter
import com.twitter.sdk.android.tweetui.UserTimeline
import java.lang.ref.WeakReference

/**
 * Created by papaefs on 17/03/18.
 */


class TwitterFragment : Fragment() {

    private var twitterConfig: TwitterConfig? = null
    private var userTimeline: UserTimeline? = null
    private lateinit var adapter: TweetTimelineRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    private var isLoaded = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.twitter_fragment, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.rv_twitterTimeline)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        if (PassClass.twitterConfig == null) {
            val twitterAsyncTask = TwitterAsyncTask(activity!!)
            twitterAsyncTask.execute()
        } else {
            twitterConfig = PassClass.twitterConfig
            userTimeline = PassClass.userTimeline
            adapter = TweetTimelineRecyclerViewAdapter.Builder(activity)
                    .setTimeline(userTimeline)
                    .setViewStyle(R.style.tw__TweetLightStyle)
                    .build()
            Twitter.initialize(twitterConfig)
            recyclerView.adapter = adapter
        }

        if (networkState == false) {
            Toast.makeText(context, "Internet connection required!", Toast.LENGTH_SHORT).show()
        }

        return view
    }


    private inner class TwitterAsyncTask(activity: Activity) : AsyncTask<Void?, Void?, Void?>() {
        private val weakReference: WeakReference<Activity>

        init {
            this.weakReference = WeakReference(activity)
        }

        override fun doInBackground(vararg voids: Void?): Void? {
            try {
                val activity = weakReference.get()
                // Twitter configuration for debug and use of API keys
                twitterConfig = TwitterConfig.Builder(activity)
                        .logger(DefaultLogger(Log.DEBUG))
                        .twitterAuthConfig(TwitterAuthConfig("", "")) // REMOVED KEYS IN ORDER TO PUBLISH ON GITHUB
                        .debug(true)
                        .build()
                Twitter.initialize(twitterConfig)

                // Select which user to show the timeline of
                userTimeline = UserTimeline.Builder()
                        .screenName("FortniteGame")
                        .maxItemsPerRequest(5)
                        .build()

                // Initialize Twitter adapter for RecyclerView
                adapter = TweetTimelineRecyclerViewAdapter.Builder(activity)
                        .setTimeline(userTimeline)
                        .setViewStyle(R.style.tw__TweetLightStyle)
                        .build()
            } catch (e: Exception) {
                isLoaded = false
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            val activity = weakReference.get()
            if (isLoaded) {
                recyclerView.adapter = adapter
                PassClass.twitterConfig = twitterConfig
                PassClass.userTimeline = userTimeline
            } else {
                Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
            if (activity == null || activity.isFinishing)
                return

        }

    }

    private val networkState: Boolean
        get() {
            val connectivityManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED) {
                true
            } else {
                false
            }
        }
}
