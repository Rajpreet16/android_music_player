package com.example.rajpreetsingh.echo.adapaters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.rajpreetsingh.echo.R
import com.example.rajpreetsingh.echo.Songs
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment

class FavoriteAdapter(_songDetails: ArrayList<Songs>, _context: Context) :
        RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null

    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)

        holder.trackTitle?.text = songObject?.songTitle
        holder.trackArtist?.text = songObject?.artist

        holder.contentHolder?.setOnClickListener({

            val songPlayingFragment = SongPlayingFragment()

            var args = Bundle()

            args.putString("songArtist", songObject?.artist)
            args.putString("songTitle", songObject?.songTitle)
            args.putString("path", songObject?.songData)
            args.putInt("SongID", songObject?.songId?.toInt() as Int)
            args.putInt("songPosition", position)
            /*Here the complete array list is sent*/
            args.putParcelableArrayList("songData", songDetails)
            /*Using this we pass the arguments to the song playing fragment*/
            songPlayingFragment.arguments = args
            /*Now after placing the song details inside the bundle, we inflate the song
           playing fragment*/
            (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragmentFav")
                    .commit()
        })
    }
    /*This has the same implementation which we did for the navigation drawer adapter*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        /*If the array list for the songs is null i.e. there are no songs in your device
        * then we return 0 and no songs are displayed*/
        if (songDetails == null) {
            return 0
        }
        /*Else we return the total size of the song details which will be the total number
       of song details*/
        else {
            return (songDetails as ArrayList<Songs>).size
        }
    }
    /*Every view holder class we create will serve the same purpose as it did when we
   created it for the navigation drawer*/
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /*Declaring the widgets and the layout used*/
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null
        /*Constructor initialisation for the variables*/
        init {
            trackTitle = view.findViewById(R.id.TrackTitle) as TextView
            trackArtist = view.findViewById(R.id.TrackArtist) as TextView
            contentHolder = view.findViewById(R.id.contentRow) as RelativeLayout
        }
    }
}