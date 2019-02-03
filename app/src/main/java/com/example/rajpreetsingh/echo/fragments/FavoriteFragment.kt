package com.example.rajpreetsingh.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.rajpreetsingh.echo.DataBase.FavoritesDataBase
import com.example.rajpreetsingh.echo.R
import com.example.rajpreetsingh.echo.Songs
import com.example.rajpreetsingh.echo.adapaters.FavoriteAdapter
import com.example.rajpreetsingh.echo.currenSongHelper


class FavoriteFragment : Fragment() {

    var myActivity: Activity? = null

    var noFavorites: TextView? = null

    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null

    var favoriteContent : FavoritesDataBase? = null

    var trackPosition : Int = 0

    var refresh: ArrayList<Songs>? = null
    var getListFromDatabase: ArrayList<Songs>? = null



    object Statified {
        var mediaPlayer :MediaPlayer?= null
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        noFavorites = view?.findViewById(R.id.noFav)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenFavBarScreen)
        songTitle = view.findViewById(R.id.songTitle)
        playPauseButton = view?.findViewById(R.id.FavplayPauseButton)
        recyclerView = view.findViewById(R.id.favRecycler)
        return view
    }



    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        favoriteContent  = FavoritesDataBase(myActivity)

        display_fav_bysearch()
        bottomBarSetup()

    }


    override fun onResume() {
        super.onResume()
    }


    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
    }



    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()

        var contentResolver = myActivity?.contentResolver


        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        var songCursor = contentResolver?.query(songUri, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                /*Adding the fetched songs to the arraylist*/
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData,
                        currentDate))
            }
        }
        /*Returning the arraylist of songs*/
        return arrayList
    }


    fun bottomBarClickHandler(){

        nowPlayingBottomBar?.setOnClickListener({

            Statified.mediaPlayer =  SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()

            var args = Bundle()

            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("SongID", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.position!!)

            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)

            args.putString("FavBottomBar","success")
            songPlayingFragment.arguments = args

            fragmentManager!!.beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()


            playPauseButton?.setOnClickListener({

                if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    /*If the song was already playing, we then pause it and save the it's
                   position
                    * and then change the button to play button*/
                    SongPlayingFragment.Statified.mediaPlayer?.pause()
                    trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition
                            as Int
                    playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                } else {
                    /*If the music was already paused and we then click on the button
                    * it plays the song from the same position where it was paused
                    * and change the button to pause button*/
                    SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                    SongPlayingFragment.Statified.mediaPlayer?.start()
                    playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
                }


            })

        })

    }


    fun bottomBarSetup(){

        try {

            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener ({
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            })

            if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                nowPlayingBottomBar?.visibility = View.VISIBLE

            }
            else{
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }










    fun display_fav_bysearch(){

        if(favoriteContent?.check() as Int > 0){

            refresh = ArrayList<Songs>()
            getListFromDatabase = favoriteContent?.queryDBList()
            var fetchListFromDevice = getSongsFromPhone()

            if(fetchListFromDevice != null){
                for(i in 0..fetchListFromDevice?.size as Int- 1){
                    for(j in 0..getListFromDatabase?.size as Int- 1) {
                        if ((getListFromDatabase?.get(j)?.songId === fetchListFromDevice?.get(i)?.songId)) {
                            refresh?.add((getListFromDatabase as ArrayList<Songs>)[j])
                        }
                    }

                }
            }else{



            }

            if (refresh == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            } else {
                var favoriteAdapter = FavoriteAdapter(refresh as ArrayList<Songs>,
                        myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator
                recyclerView?.adapter = favoriteAdapter
                recyclerView?.setHasFixedSize(true)
            }
        }
        else{
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
        }
    }


}