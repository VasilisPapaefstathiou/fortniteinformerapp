package com.vasilis.papaefs.fninformer


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

class AboutItem(var title: String?, var subTitle: String?)

class AboutListViewAdapter(private val context: Context?, private val aboutItemList: List<AboutItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return aboutItemList.size
    }

    override fun getItem(i: Int): Any {
        return aboutItemList[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
        val v = View.inflate(context, R.layout.about_list_item, null)
        val title = v.findViewById<View>(R.id.tv_about_title) as TextView
        val subTitle = v.findViewById<View>(R.id.tv_about_subtitle) as TextView

        title.text = aboutItemList[i].title
        subTitle.text = aboutItemList[i].subTitle
        v.tag = aboutItemList[i].title

        return v
    }
}

class AboutFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.about_fragment, container, false)

        val aboutListView = view!!.findViewById<View>(R.id.lv_about) as ListView
        val aboutItemList: ArrayList<AboutItem> = ArrayList()

        aboutItemList.add(AboutItem("Developer", "Just a student who develops apps for fun :)"))
        //  TODO
        aboutItemList.add(AboutItem("Version", "1.0"))
        aboutItemList.add(AboutItem("News", "Taken from the official Epic Games website"))
        aboutItemList.add(AboutItem("Item Shop", "Taken from  (Tap to visit)"))
        aboutItemList.add(AboutItem("Copyright", "Epic Games, Inc is not affiliated with this app. All Fortnite material used is owned by Epic Games, Inc"))

        val adapter = AboutListViewAdapter(context!!, aboutItemList)
        aboutListView.adapter = adapter

        aboutListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            if (view.tag == "Item Shop") {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("")
                startActivity(intent)
            }
        }


        return view
    }

}
