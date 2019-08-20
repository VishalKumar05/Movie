package com.example.movie

import android.app.SearchManager
import android.content.Context

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonArrayRequest

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList
import com.android.volley.toolbox.Volley
import com.example.movie.Adapter.MoviesAdapter
import com.example.movie.Model.Movie
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.example.movie.Constants.StringConstant
import com.example.movie.Util.Data
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), MoviesAdapter.ItemClickListener, GoogleApiClient.OnConnectionFailedListener {

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val TAG = MainActivity::class.java.simpleName
    private val url:String = "https://api.androidhive.info/json/movies.json"
    val movieData = ArrayList<Movie>()
    //val movieData:MutableList<Movie>? = null
    lateinit var mAdapter: MoviesAdapter
    private var searchView:SearchView? = null
    //var requestQueue = RequestQueue()
    private lateinit var mAuth : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var mAuthListener : FirebaseAuth.AuthStateListener? = null
    private lateinit var googleApiClient:GoogleApiClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val analyticsLogEvent = AnalyticsLogEvent()
    private val data = Data()

    interface ClickListener {
        fun onClick(view: View, position: Int)
        fun onLongClick(view: View?, position: Int)
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(this.mAuthListener!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // setSupportActionBar(toolbar)
        // getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        mAuthListener = FirebaseAuth.AuthStateListener(object : FirebaseAuth.AuthStateListener, (FirebaseAuth) -> Unit {
            override fun invoke(p1: FirebaseAuth) {
            }

            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user = firebaseAuth.getCurrentUser()
                if (user == null){
                    val intent = Intent(this@MainActivity,Login::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        })

        progress_bar.visibility = View.VISIBLE
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_view!!.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator() as RecyclerView.ItemAnimator?
        mAdapter = MoviesAdapter(this, movieData, this)
        recycler_view.adapter = mAdapter

        recycler_view!!.addOnItemTouchListener(RecyclerTouchListener(this@MainActivity, recycler_view!!, object : ClickListener {

            override fun onClick(view: View, position: Int) {
                //val intent: Intent?
                /*when(position){
                    0 -> {
                        intent = Intent(this@MainActivity,PlayerActivity::class.java)
                        //intent.putExtra("movieName",movieData[position].getMovieName())
                        startActivity(intent)
                    }
                    else -> {
                        intent = Intent(this@MainActivity,PlayerActivity::class.java)
                        //intent.putExtra("movieName",movieData[position].getMovieName())
                        startActivity(intent)
                    }
                }*/

                for(i in 0..movieData.size){
                    val intent = Intent(this@MainActivity,PlayerActivity::class.java)
                    //intent.putExtra("movieData",data.videoList.get(position))
                    var b = Bundle()
                    b.putString("movieData",data.videoList.get(position))
                    intent.putExtras(b)
                    startActivity(intent)
                }
            }
            override fun onLongClick(view: View?, position: Int) {
            }
        }))
        prepareMovieData()
    }

    private fun prepareMovieData() {
        movieData.clear()
        val requestQueue = Volley.newRequestQueue(this)
        val movieReq = JsonArrayRequest(url,
            Response.Listener { response ->
                progress_bar.visibility = View.GONE
                for (i in 0..response.length()){
                    try {
                        val obj: JSONObject = response.getJSONObject(i)
                        val movie = Movie()

                        movie.setThumbnailUrl(obj.getString("image"))
                        movie.setMovieName(obj.getString("title"))
                        movie.setMovieRating((obj.get("rating") as Number).toDouble())

                        if (movieData != null) {
                            movieData.add(movie)
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                mAdapter.notifyDataSetChanged()
                /*val bundle = Bundle()
                bundle.putString("movie_data", movieData.toString())*/
                firebaseAnalytics.logEvent(StringConstant.event_category, analyticsLogEvent.eventLog("movie_data","Movie data loaded"))
            },
            Response.ErrorListener { error ->
                VolleyLog.d("Volley","Error: $error")
                progress_bar.visibility = View.GONE
                firebaseAnalytics.logEvent(StringConstant.event_category, analyticsLogEvent.eventLog("volley_error","Error loading data"))
                Toast.makeText(this,"Error: $error",Toast.LENGTH_SHORT).show()
            }
        )
        //MySingleton.getInstance(this).addToRequestQueue(movieReq)
        requestQueue.add(movieReq)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // Associate search_layout configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView!!.maxWidth = Integer.MAX_VALUE

        // listening to search query text change
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                mAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                mAdapter.filter.filter(query)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> true

            R.id.action_logout -> {
                Toast.makeText(this,"Logout",Toast.LENGTH_SHORT).show()
                showSignoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSignoutDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
            .setCancelable(false)
            .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                    dialog, id -> run {
                            mAuth.signOut()
                            googleSignInClient.signOut().addOnCompleteListener(this) {}
                            gotoLoginActivity()
                        }
            }).setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("Sign Out?")
        alert.show()
    }

    private fun gotoLoginActivity() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        Log.d("1","BackPressed")
        if (!searchView!!.isIconified){
            searchView!!.isIconified = true
            return
        }
        super.onBackPressed()
    }

    override fun onItemClicked(movie: Movie) {
        Toast.makeText(this@MainActivity, "Selected", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    internal class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: ClickListener?) : androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }
                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child))
                    }
                }
            })
        }

        override fun onInterceptTouchEvent(rv: androidx.recyclerview.widget.RecyclerView, e: MotionEvent): Boolean {
            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child))
            }
            return false
        }
        override fun onTouchEvent(rv: androidx.recyclerview.widget.RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }
    }

}
