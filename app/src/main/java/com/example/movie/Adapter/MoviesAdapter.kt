package com.example.movie.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.android.volley.toolbox.ImageLoader
import com.example.movie.Model.Movie
import com.example.movie.Util.MySingleton
import com.example.movie.R
import kotlinx.android.synthetic.main.single_card.view.*

class MoviesAdapter(private val context:Context, private val dataList:MutableList<Movie>, private val listener: ItemClickListener) : RecyclerView.Adapter<MoviesAdapter.MyViewHolder>(),Filterable {

    var imageLoader: ImageLoader? = null
    private var searchQueryList:MutableList<Movie>? = null

    init {
        this.searchQueryList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.single_card,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie: Movie = dataList[position]

        //Log.d("Adapter","Id: ${movie.getThumbnailUrl()}")
        if (imageLoader == null){
            imageLoader = MySingleton.getInstance(context).imageLoader
        }
        holder.view.iv_icon.setImageUrl(movie.getThumbnailUrl(),imageLoader)
        holder.view.tv_title.text = movie.getMovieName()
        holder.view.tv_rating.text = movie.getMovieRating().toString()
    }

    inner class MyViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        init {
          view.setOnClickListener(View.OnClickListener {
              listener.onItemClicked(searchQueryList!![adapterPosition])
          })
        }
    }

    //For Search
    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    searchQueryList = dataList
                } else {
                    val filteredList = ArrayList<Movie>()
                    for (row in dataList) {
                        if (row.getMovieName()!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    searchQueryList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = searchQueryList
                Log.d("Query","Filter: ${filterResults.values}")
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                searchQueryList = results?.values as MutableList<Movie>?
                notifyDataSetChanged()
            }

        }
    }


    interface ItemClickListener {
        fun onItemClicked(movie: Movie)
    }

}




