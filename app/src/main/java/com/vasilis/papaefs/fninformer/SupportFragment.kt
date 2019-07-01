package com.vasilis.papaefs.fninformer


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

/**
 * Created by papaefs on 04/04/18.
 */
class SupportItem(var title: String?, var subTitle: String?)

class SupportListViewAdapter(private val context: Context, private val supportItemList: List<SupportItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return supportItemList.size
    }

    override fun getItem(i: Int): Any {
        return supportItemList[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
        val v = View.inflate(context, R.layout.support_list_item, null)
        val title = v.findViewById<View>(R.id.tv_support_title) as TextView
        val subTitle = v.findViewById<View>(R.id.tv_support_subtitle) as TextView

        title.text = supportItemList[i].title
        subTitle.text = supportItemList[i].subTitle

        v.tag = supportItemList[i].title
        return v
    }
}

class SupportFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.support_fragment, container, false)

        // initialize variables
        val context = context!!
        val supportListView = view.findViewById<View>(R.id.lv_support) as ListView
        val supportItemList: ArrayList<SupportItem> = ArrayList()
        val intent = Intent(Intent.ACTION_VIEW)


        supportItemList.add(SupportItem("", "Visit and check out their cool website"))
        supportItemList.add(SupportItem("Rate the App", "Give feedback through the Play Store"))

        val adapter = SupportListViewAdapter(context, supportItemList)
        supportListView.adapter = adapter

        supportListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val tag = view.tag as String
            when (tag) {
                "" -> startIntent(intent, "")
                "Rate the App" -> {
                    val uri = Uri.parse("market://details?id=" + context.packageName)
                    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                    try {
                        startActivity(goToMarket)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)))
                    }

                }
            }
        }

        return view
    }

    private fun startIntent(intent: Intent, link: String) {
        intent.data = Uri.parse(link)
        startActivity(intent)
    }


}
