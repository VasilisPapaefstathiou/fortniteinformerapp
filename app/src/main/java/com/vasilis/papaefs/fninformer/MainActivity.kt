package com.vasilis.papaefs.fninformer

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.vasilis.papaefs.fninformer.News.NewsFragment
import com.vasilis.papaefs.fninformer.Shop.ShopFragment

class MainActivity : AppCompatActivity() {

    private var drawerLayout: DrawerLayout? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var navigationView: NavigationView? = null

    internal lateinit var twitterFragment: Fragment
    internal lateinit var shopFragment: Fragment
    internal lateinit var transaction: FragmentTransaction
    internal lateinit var aboutFragment: Fragment
    internal lateinit var supportFragmnet: Fragment
    internal lateinit var newsFragment: Fragment
    internal lateinit var searchItemFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeVariables()
    }

    private fun initializeVariables() {

        drawerLayout = findViewById<View>(R.id.dl_drawer) as DrawerLayout?
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        navigationView = findViewById<View>(R.id.nv_main) as NavigationView?

        drawerLayout!!.addDrawerListener(drawerToggle!!)
        drawerToggle!!.syncState()

        // Create instances of all the fragments
        try {
            twitterFragment = TwitterFragment::class.java.newInstance()
            shopFragment = ShopFragment::class.java.newInstance()
            aboutFragment = AboutFragment::class.java.newInstance()
            supportFragmnet = SupportFragment::class.java.newInstance()
            newsFragment = NewsFragment::class.java.newInstance()
            searchItemFragment = SearchItem::class.java.newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setDrawerContent(navigationView)

        title = "Twitter"
        navigationView!!.setCheckedItem(R.id.mi_twitter)
        transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_fragment, twitterFragment)
        transaction.commit()

    }


    // Navigation view item click listener
    private fun setDrawerContent(navigationView: NavigationView?) {
        navigationView!!.setNavigationItemSelectedListener { item ->

            drawerLayout!!.closeDrawers()

            // Set fragment selected on item click
            when (item.itemId) {
                R.id.mi_twitter -> replaceFragment(twitterFragment)
                R.id.mi_shop -> replaceFragment(shopFragment)
                R.id.mi_about -> replaceFragment(aboutFragment)
                R.id.mi_news -> replaceFragment(newsFragment)
                R.id.mi_support -> replaceFragment(supportFragmnet)
                R.id.mi_searchItem -> replaceFragment(searchItemFragment)
                else -> {
                }
            }

            item.isChecked = true
            title = item.title
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_fragment, fragment)
        transaction.commit()
    }

    // Opens navigation drawer with button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (drawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

}
