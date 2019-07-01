package com.vasilis.papaefs.fninformer

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.lang.ref.WeakReference
import java.util.*


/*
* TODO
* save favourite skins on phone
* remember searches
* reminder when favorite skin goes on sale
* upcoming commpetitions
* make error messages (fix network connection)
* notification
*
* support stuff
* load less news at a time
* create new images for android play store
* check out dagger
* */

class SearchItem : Fragment() {

    internal lateinit var view: View

    // JSON
    internal var imageLink = ""
    internal lateinit var input: String
    internal lateinit var tempInput: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_search_item, container, false)

        tempInput = ""
        val searchBar = view.findViewById<View>(R.id.et_searchItem) as EditText
        val searchButton = view.findViewById<View>(R.id.btn_searchItem) as Button

        searchButton.setOnClickListener {
            input = searchBar.text.toString().replace(" ", "+")

            // do nothing if there is no input or the user if just spamming the search button
            if (input == "" || input == tempInput) {
                // do nothing
            } else if (networkState == false) {
                Toast.makeText(activity, "Internet Connection Required", Toast.LENGTH_SHORT).show()
            } else {
                tempInput = input
                Toast.makeText(activity, "Loading...", Toast.LENGTH_SHORT).show()
                val task = SearchItemAsyncTask(activity!!, input)
                task.execute()
            }
        }

        return view
    }

    private inner class SearchItemAsyncTask(activity: Activity, private val input: String) : AsyncTask<Void?, Void?, Void?>() {

        private val weakReference: WeakReference<Activity>

        init {
            this.weakReference = WeakReference(activity)
        }

        override fun doInBackground(vararg voids: Void?): Void? {
            try{
                val weakActivity = weakReference.get()
                val request = object : JsonObjectRequest(Request.Method.GET, "" + input, null,
                        Response.Listener { response ->
                            try {
                                // Get arrays of gallery links
                                val jsonArrayFeatured = response.getJSONArray("data")
                                imageLink = jsonArrayFeatured.getJSONObject(0).getJSONObject("images").getString("gallery")

                                // if gallery link doesn't exist, get the icon
                                if (imageLink == "false")
                                    imageLink = jsonArrayFeatured.getJSONObject(0).getJSONObject("images").getString("icon")

                                // load image into image view
                                val galleryImage = view.findViewById<View>(R.id.iv_searchItem) as ImageView
                                val requestOptions = RequestOptions()
                                        .centerCrop()
                                        .placeholder(null)
                                        .error(null)
                                Glide.with(weakActivity!!)
                                        .load(imageLink)
                                        .apply(requestOptions)
                                        .into(galleryImage)

                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(weakActivity, "Item not found!", Toast.LENGTH_SHORT).show()
                            }
                        }, Response.ErrorListener { error -> error.printStackTrace() }) {
                    // Custom authorization header
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("x-api-key", "") // REMOVED KEY IN ORDER TO PUBLISH ON GITHUB
                        return headers
                    }
                }
                Volley.newRequestQueue(weakActivity).add(request)
            } catch (e: Exception){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            val activity = weakReference.get()
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

    override fun onDetach() {
        input = ""
        tempInput = ""
        super.onDetach()
    }
}
