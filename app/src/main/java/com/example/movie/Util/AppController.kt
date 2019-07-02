package com.example.movie.Util

import android.app.Application
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

class AppController : Application() {

    private var mRequestQueue: RequestQueue? = null
    private var mImageLoader: ImageLoader? = null
    private var mInstance: AppController? = null



    fun getRequestQueue(): RequestQueue? {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(applicationContext)
            }

            return mRequestQueue

    }

    fun getImageLoader(): ImageLoader? {
            getRequestQueue()
            if (mImageLoader == null) {
                mImageLoader = ImageLoader(this.mRequestQueue, LruBitmapCache() as ImageLoader.ImageCache)
            }
            return this.mImageLoader

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        // set the default tag if tag is empty
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        getRequestQueue()!!.add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        getRequestQueue()!!.add(req)
    }

    fun cancelPendingRequests(tag: Any) {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }

    companion object {
        fun getInstance(): AppController? {
            return instance
        }

        val TAG = AppController::class.java.simpleName

        @get:Synchronized
        private var instance: AppController? = null
            private set

    }

}
