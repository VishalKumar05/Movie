package com.example.movie

import android.os.Bundle

class AnalyticsLogEvent {

    fun eventLog(key:String, msg: String):Bundle{
        val bundle = Bundle()
        bundle.putString(key, msg)
        return bundle
    }

}