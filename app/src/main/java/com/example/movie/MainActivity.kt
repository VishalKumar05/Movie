package com.example.movie

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
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


class MainActivity : AppCompatActivity(), MoviesAdapter.ItemClickListener {

    private val TAG = MainActivity::class.java.simpleName
    private val url:String = "https://api.androidhive.info/json/movies.json"
    val movieData = ArrayList<Movie>()
    //val movieData:MutableList<Movie>? = null
    lateinit var adapter: MoviesAdapter
    private var searchView:SearchView? = null
    //var requestQueue = RequestQueue()

    interface ClickListener {
        fun onClick(view: View, position: Int)

        fun onLongClick(view: View?, position: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        /*setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);*/

        progress_bar.visibility = View.VISIBLE
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view!!.itemAnimator = DefaultItemAnimator()
        adapter = MoviesAdapter(this, movieData, this)
        recycler_view.adapter = adapter

        recycler_view!!.addOnItemTouchListener(RecyclerTouchListener(this@MainActivity, recycler_view!!, object : ClickListener {

            override fun onClick(view: View, position: Int) {
                //Toast.makeText(this@MainActivity, this!!.movieData[position]?.getMovieName(), Toast.LENGTH_SHORT).show()

                var intent: Intent?

                when(position){
                    0 -> {
                        intent = Intent(this@MainActivity,MovieDetail::class.java)
                        //intent.putExtra("movieName",movieData[position].getMovieName())
                        startActivity(intent)
                    }

                    else -> {
                        intent = Intent(this@MainActivity,MovieDetail::class.java)
                        //intent.putExtra("movieName",movieData[position].getMovieName())
                        startActivity(intent)
                    }

                }
            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))

        prepareMovieData()
    }

    private fun prepareMovieData() {
        movieData?.clear()
        val requestQueue = Volley.newRequestQueue(this)
        var movieReq = JsonArrayRequest(url,
            Response.Listener { response ->
                progress_bar.visibility = View.GONE
                for (i in 0..response.length()){
                    try {
                        var obj: JSONObject = response.getJSONObject(i)
                        var movie = Movie()

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

                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                VolleyLog.d("Volley","Error: $error")
                progress_bar.visibility = View.GONE
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
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                adapter.filter.filter(query)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        Log.d("1","BackPressed")
        if (!searchView!!.isIconified){
            searchView!!.setIconified(true);
            return;
        }
        super.onBackPressed()
    }

    override fun onItemClicked(movie: Movie) {
        Toast.makeText(this@MainActivity, "Selected", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    internal class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: ClickListener?) : RecyclerView.OnItemTouchListener {

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

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child))
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }

}
