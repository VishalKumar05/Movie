package com.example.movie

import android.os.Bundle

class AnalyticsLogEvent {

    fun eventLog(event:String, msg: String):Bundle{
        val bundle = Bundle()
        bundle.putString(event, msg)
        return bundle
    }

}