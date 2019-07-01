package com.vasilis.papaefs.fninformer

import android.support.v4.app.Fragment
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.tweetui.UserTimeline
import com.vasilis.papaefs.fninformer.News.NewsItem
import com.vasilis.papaefs.fninformer.Shop.ShopItem
import java.util.*

/**
 * Created by papaefs on 22/03/18.
 */

class PassClass : Fragment() {
    companion object {
        var shopItemList: ArrayList<ShopItem>? = null
        var newsList: ArrayList<NewsItem>? = null
        var twitterConfig: TwitterConfig? = null
        var userTimeline: UserTimeline? = null
    }

}
