package com.example.movie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.movie_detail.*
import com.google.android.exoplayer2.ExoPlayer


class PlayerActivity : AppCompatActivity() {

    private val TAG:String = PlayerActivity::class.java.simpleName
    private var player:SimpleExoPlayer? = null
    private var currentWindow:Int = 0
    private var playbackPosition:Int = 0
    private var playWhenReady:Boolean = true
    private var componentListener = ComponentListener()
    lateinit var mVideoDataUrl:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_detail)

        val i = getIntent()
       // mVideoData = i.getStringExtra("movieData")
        mVideoDataUrl = i.getStringExtra("movieData")
        Log.d("data","Data: ${mVideoDataUrl}")
    }

    fun initializePlayer(){
        if (player == null) {
            val defaultRenderersFactory = DefaultRenderersFactory(this)
            val defaultTrackSelector = DefaultTrackSelector()
            val defaultLoadControl = DefaultLoadControl()
            player = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, defaultTrackSelector, defaultLoadControl)
            //val uri: Uri = Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
            val uri: Uri = Uri.parse(mVideoDataUrl)
            val mediaSource: MediaSource = buildMediaSource(uri)
            player_view.player = player
            player?.prepare(mediaSource, true, false)
            player?.addListener(componentListener)
            player?.playWhenReady = playWhenReady
            player?.seekTo(currentWindow, playbackPosition.toLong())
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        Log.d("Url","url: $uri")
        return ExtractorMediaSource.Factory(
            DefaultHttpDataSourceFactory("exoplayer-codelab")
        ).createMediaSource(uri)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            Log.d("State","onStart()")
            initializePlayer()
            player?.playWhenReady = true
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            Log.d("State","onResume()")
            initializePlayer()
        }
    }

    private fun hideSystemUi() {
        player_view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        )
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            Log.d("State","onPause()")
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            Log.d("State","onStop()")
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player?.currentPosition!!.toInt()
            currentWindow = player?.currentWindowIndex!!
            playWhenReady = player?.getPlayWhenReady()!!
            player?.removeListener(componentListener)
            player?.release()
            player = null
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("data","onBackPressed()")
        //player?.release()
        //player = null
        val intent = Intent(this@PlayerActivity,MainActivity::class.java)
        startActivity(intent)
        releasePlayer()

    }

    private inner class ComponentListener : Player.DefaultEventListener() {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateString: String
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> stateString = "ExoPlayer.STATE_IDLE -"
                ExoPlayer.STATE_BUFFERING -> stateString = "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> stateString = "ExoPlayer.STATE_READY -"
                ExoPlayer.STATE_ENDED -> stateString = "ExoPlayer.STATE_ENDED -"
                else -> stateString = "UNKNOWN_STATE -"
            }
            Log.d(TAG, "changed state to " + stateString+ " playWhenReady: " + playWhenReady)
        }
    }


}