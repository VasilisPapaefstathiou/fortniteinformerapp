package com.vasilis.papaefs.fninformer.Shop

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vasilis.papaefs.fninformer.PassClass
import com.vasilis.papaefs.fninformer.R
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by papaefs on 19/03/18.
 */

class ShopItem(var thumbnail: String?)

class ShopRecyclerViewAdapter(private val context: Context, private val shopItemList: java.util.ArrayList<ShopItem>) : RecyclerView.Adapter<ShopRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shop_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val requestOptions = RequestOptions()
                .centerCrop()
                .placeholder(null)
                .error(null)
        Glide.with(context)
                .load(shopItemList[position].thumbnail)
                .apply(requestOptions)
                .into(holder.thumbnail)

    }

    override fun getItemCount(): Int {
        return shopItemList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var thumbnail: ImageView

        init {
            thumbnail = itemView.findViewById<View>(R.id.iv_shopItem) as ImageView
        }
    }
}

class ShopFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var shopItemList: ArrayList<ShopItem>
    private lateinit var progressBar: ProgressBar
    private var isConnected = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.shop_fragment, container, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        recyclerView = view.findViewById<View>(R.id.rc_shop) as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        progressBar = view.findViewById(R.id.pb_shop)
        shopItemList = ArrayList()


        if (networkState) {
            if (PassClass.shopItemList == null) {
                val shopAsyncTask = ShopAsyncTask(activity!!)
                shopAsyncTask.execute()
            } else {
                shopItemList = PassClass.shopItemList!!
                progressBar.visibility = View.GONE
                adapter = ShopRecyclerViewAdapter(activity!!, shopItemList)
                recyclerView.adapter = adapter
            }

        } else {
            if (PassClass.shopItemList != null) {
                shopItemList = PassClass.shopItemList!!
                progressBar.visibility = View.GONE
                adapter = ShopRecyclerViewAdapter(activity!!, shopItemList)
                recyclerView.adapter = adapter
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Internet Connection Required", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private inner class ShopAsyncTask(activity: Activity) : AsyncTask<Void?, Void?, Void?>() {

        private val weakReference: WeakReference<Activity>

        init {
            this.weakReference = WeakReference(activity)
        }

        override fun doInBackground(vararg voids: Void?): Void? {
            try {
                val request = object : JsonObjectRequest(Request.Method.GET, "", null,
                        Response.Listener { response ->

                            val galleryLinks: ArrayList<String> = ArrayList()

                            // Get arrays of gallery links
                            val jsonArrayFeatured = response.getJSONObject("data").getJSONArray("featured")
                            val jsonArrayDaily = response.getJSONObject("data").getJSONArray("daily")

                            // Get length of array with most items
                            var maxLength = 0
                            if (jsonArrayDaily!!.length() > jsonArrayFeatured!!.length()) {
                                maxLength = jsonArrayDaily.length()
                            } else {
                                maxLength = jsonArrayFeatured.length()
                            }

                            // Loop through every array and get the skins
                            for (i in 0 until maxLength) {
                                if (jsonArrayFeatured.length() > i)
                                    galleryLinks.add(jsonArrayFeatured.getJSONObject(i).getJSONObject("images").getString("gallery"))
                                if (jsonArrayDaily.length() > i)
                                    galleryLinks.add(jsonArrayDaily.getJSONObject(i).getJSONObject("images").getString("gallery"))
                            }

                            // create and show gallery
                            for (link in galleryLinks) {
                                shopItemList.add(ShopItem(link))
                            }

                            progressBar.visibility = View.GONE
                            PassClass.shopItemList = shopItemList
                            adapter = ShopRecyclerViewAdapter(activity!!, shopItemList)
                            recyclerView.adapter = adapter

                        }, Response.ErrorListener { error -> error.printStackTrace() }) {

                    // Custom authorization header
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("x-api-key", "") // REMOVED KEY IN ORDER TO PUBLISH ON GITHUB
                        return headers
                    }
                }

                Volley.newRequestQueue(activity).add(request)

            } catch (e: Exception) {
                e.printStackTrace()
                isConnected = false
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            val activity = weakReference.get()
            if (activity == null || activity.isFinishing)
                return
            if (isConnected == false) {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
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
