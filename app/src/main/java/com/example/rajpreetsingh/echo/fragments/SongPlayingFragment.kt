package com.example.rajpreetsingh.echo.fragments

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.cleveroad.audiovisualization.VisualizerDbmHandler
import com.example.rajpreetsingh.echo.DataBase.FavoritesDataBase
import com.example.rajpreetsingh.echo.R
import com.example.rajpreetsingh.echo.Songs
import com.example.rajpreetsingh.echo.currenSongHelper
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Staticated.MY_PREFS_LOOP
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Staticated.MY_PREFS_SHUFFLE
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Staticated.onSongComplete
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Staticated.playNext
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Staticated.processInfo
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Staticated.updateTextView
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.audioVisualization
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.currentPosition
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.currentSongHelper
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.endTimeText
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.fav
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.favoriteContent
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.fetchSongs
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.glView
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.loopImageButton
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.mediaPlayer
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.myActivity
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.nextImageButton
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.playPauseImageButton
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.previousImageButton
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.seekBar
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.shuffleImageButton
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.songArtistView
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.songTitleView
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.startTimeText
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment.Statified.updateSongTime
import kotlinx.android.synthetic.main.fragment_song_playing.*
import java.util.*
import java.util.concurrent.TimeUnit



class SongPlayingFragment : Fragment() {





    /*The different variables defined will be used for their respective purposes*/
    /*Depending on the task they do we name the variables as such so that it gets easier to
   identify the task they perform*/

    object Statified{
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null

        var fav: ImageButton? = null



        /*The current song helper is used to store the details of the current song being
       played*/
        var currentSongHelper: currenSongHelper? = null

        var glView: GLAudioVisualizationView?=null
        var audioVisualization: AudioVisualization?=null


        var favoriteContent: FavoritesDataBase? = null

//        var mSensorListner: SensorEventListener?=null
        var mSensorListener: SensorEventListener? = null
        var mSensorManager: SensorManager?= null
        var MY_PREFS_NAME = "ShakeFeature"


        var updateSongTime = object : Runnable {
            override fun run() {
                /*Retrieving the current time position of the media player*/
                val getCurrent = mediaPlayer?.currentPosition
                /*The start time is set to the current position of the song
                * The TimeUnit class changes the units to minutes and milliseconds and applied
               to the string
                * The %d:%d is used for formatting the time strings as 03:45 so that it
               appears like time*/
                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as
                                Long))))
                /*Since updating the time at each second will take a lot of processing, so we
               perform this task on the different thread using Handler*/
                Handler().postDelayed(this, 1000)
            }
        }



//    SHARED PREFERENCES



    }
    object Staticated{
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"







        fun onSongComplete(){
            if(currentSongHelper?.isShuffle as Boolean){
                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlaying = true
            }
            else{
                if(currentSongHelper?.isLoop as Boolean){

                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(currentPosition)

                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songArtist = nextSong?.artist
                    currentSongHelper?.position = currentPosition
                    currentSongHelper?.songId = nextSong?.songId as Long

                    updateTextView(currentSongHelper?.songTitle as String,currentSongHelper?.songArtist as String)

                    mediaPlayer?.reset()

                    try{
                        mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()

                        processInfo(mediaPlayer as MediaPlayer)

                    }
                    catch(e : Exception){
                        e.printStackTrace()
                    }

                }
                else{
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlaying = true
                }
            }


            if(favoriteContent?.checkifIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean){
                fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_on))

            }
            else{
                fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_off))
            }


        }



        fun updateTextView(SongTitle : String, SongArtist : String){
            songTitleView?.setText(SongTitle as String)
            songArtistView?.setText(SongArtist as String)

        }











        /*The playNext() function is used to play the next song*/
        fun playNext(check: String) {
            /*Let this one sit for a while, We'll explain this after the next section where we
           will be teaching to add the next and previous functionality*/
            if (check.equals("PlayNextNormal", true)) {
                currentPosition = currentPosition + 1
            }
            else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = randomPosition
            }

            if (currentPosition == fetchSongs?.size) {
                currentPosition = 0
            }


            currentSongHelper?.isLoop = false

            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songId = nextSong?.songId as Long

            updateTextView(currentSongHelper?.songTitle as String,currentSongHelper?.songArtist as String)

            mediaPlayer?.reset()


            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()

                processInfo(mediaPlayer as MediaPlayer)

            }
            catch (e: Exception) {
                e.printStackTrace()
            }



            if(favoriteContent?.checkifIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean){
                fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_on))

            }
            else{
                fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_off))
            }


        }

        fun processInfo(mediaPlayer: MediaPlayer){

            val finalTime= mediaPlayer.duration
            val startTime= mediaPlayer.currentPosition
            seekBar?.max = finalTime

            startTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))
            )
            /*Similar to above is done for the end time text*/
            endTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))
            )


            seekBar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime, 1000)


        }



    }






    var mAccelerate: Float = 0f
    var mAccelerateCurrent: Float = 0f
    var mAccelerateLast: Float = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        /*Linking views with their ids*/
        Statified.seekBar = view?.findViewById(R.id.seekBar)
        startTimeText = view?.findViewById(R.id.startTime)
        endTimeText = view?.findViewById(R.id.endTime)
        playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        nextImageButton = view?.findViewById(R.id.nextButton)
        previousImageButton = view?.findViewById(R.id.previousButton)
        loopImageButton = view?.findViewById(R.id.loopButton)
        shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        songArtistView = view?.findViewById(R.id.songArtist)
        songTitleView = view?.findViewById(R.id.songTitle)
        glView = view?.findViewById(R.id.visualizer_view)

        fav = view?.findViewById(R.id.favIcon)

        fav?.alpha = 0.8f

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioVisualization = glView as AudioVisualization
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerate = 0.0f
        mAccelerateCurrent = SensorManager.GRAVITY_EARTH
        mAccelerateLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
    }
    /*Here we handle the click event of the menu item*/
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
        /*If the id of the item click is action_redirect
        * we navigate back to the list*/
            R.id.action_redirect -> {
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }



    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }




    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }


    override fun onResume() {
        super.onResume()

        audioVisualization?.onResume()

        Statified.mSensorManager?.registerListener(Statified.mSensorListener,Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
                )
    }

    override fun onPause() {
        super.onPause()


        audioVisualization?.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioVisualization?.release()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*Initialising the params of the current song helper object*/

        favoriteContent = FavoritesDataBase(myActivity)

        currentSongHelper = currenSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isLoop = false
        currentSongHelper?.isShuffle = false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0




        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("songId")?.toLong() as Long
            /*Here we fetch the received bundle data for current position and the list of
           all songs*/
            currentPosition = arguments?.getInt("position")?.toInt() as Int
            fetchSongs = arguments?.getParcelableArrayList("songData")
            /*Now store the song details to the current song helper object so that they
           can be used later*/

            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songId = songId
            currentSongHelper?.position = currentPosition

            updateTextView(currentSongHelper?.songTitle as String,currentSongHelper?.songArtist as String)


        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if(fromFavBottomBar != null){
            Statified.mediaPlayer = FavoriteFragment.Statified.mediaPlayer
        }

        else{
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)


            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(path))
                mediaPlayer?.prepare()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

            mediaPlayer?.start()
        }



        processInfo(mediaPlayer as MediaPlayer)


        mediaPlayer?.setOnCompletionListener{
            onSongComplete()
        }


        clickHandler()


        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context,0)
        audioVisualization?.linkTo(visualizationHandler)


        var prefsForShuffle = myActivity?.getSharedPreferences(MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)

        if(isShuffleAllowed as Boolean){

            currentSongHelper?.isShuffle = true
            currentSongHelper?.isLoop = false

            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        else
        {

            currentSongHelper?.isShuffle = false
            currentSongHelper?.isLoop =  true

            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

        }

        var prefsForLoop = myActivity?.getSharedPreferences(MY_PREFS_LOOP,Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature",false)

        if(isLoopAllowed as Boolean){

            currentSongHelper?.isShuffle = false
            currentSongHelper?.isLoop = true

            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        }
        else
        {
            currentSongHelper?.isLoop =  false
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

        }


        if(favoriteContent?.checkifIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean){
            fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_on))

        }
        else{
            fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_off))
        }


    }








    /*A new click handler function is created to handle all the click functions in the song
   playing fragment*/
    fun clickHandler() {
        /*The implementation will be taught in the coming topics*/

        fav?.setOnClickListener({
            if(favoriteContent?.checkifIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean){
                fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_off))
                favoriteContent?.deleteFavorite(currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(myActivity,"Removed From Favorites", Toast.LENGTH_SHORT).show()
            }
            else{
                fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_on))
                favoriteContent?.storeAsFavorite(currentSongHelper?.songId?.toInt(), currentSongHelper?.songArtist,currentSongHelper?.songTitle,currentSongHelper?.songPath)
                Toast.makeText(myActivity,"Added To Favorites", Toast.LENGTH_SHORT).show()
            }
        })

        shuffleImageButton?.setOnClickListener({

            var editorShuffle = myActivity?.getSharedPreferences(MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if(currentSongHelper?.isShuffle as Boolean){
                currentSongHelper?.isShuffle = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
            }
            else{
                currentSongHelper?.isLoop = false
                currentSongHelper?.isShuffle = true
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)

                editorShuffle?.putBoolean("feature",true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }


        })


        nextImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if(currentSongHelper?.isShuffle as Boolean){

                playNext("PlayNextNormal")

            }
            else{
                playNext("PlayNextLikeNormalShuffle")
            }
        })


        previousImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isLoop as Boolean){
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })


        loopImageButton?.setOnClickListener({

            var editorShuffle = myActivity?.getSharedPreferences(MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if(currentSongHelper?.isLoop as Boolean){
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }
            else{
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
            }

        })


        /*Here we handle the click event on the play/pause button*/
        playPauseImageButton?.setOnClickListener({
            /*if the song is already playing and then play/pause button is tapped
            * then we pause the media player and also change the button to play button*/
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                /*If the song was not playing the, we start the music player and
               * change the image to pause icon*/
            } else {
                mediaPlayer?.start()
                currentSongHelper?.isPlaying = true
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }









    fun playPrevious(){
            currentPosition = currentPosition - 1

        if(currentPosition == -1){
            currentPosition = 0
        }

        if(currentSongHelper?.isPlaying as Boolean){
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        }
        else
        {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        currentSongHelper?.isLoop = false

        var nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songArtist = nextSong?.artist
        currentSongHelper?.songId = nextSong?.songId as Long

        updateTextView(currentSongHelper?.songTitle as String,currentSongHelper?.songArtist as String)

        mediaPlayer?.reset()

        try {
            mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaPlayer?.prepare()
            mediaPlayer?.start()

            processInfo(mediaPlayer as MediaPlayer)

        }
        catch (e: Exception) {
            e.printStackTrace()
        }


        if(favoriteContent?.checkifIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean){
            fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_on))

        }
        else{
            fav?.setImageDrawable(ContextCompat.getDrawable(myActivity!!,R.drawable.favorite_off))
        }

    }


    fun bindShakeListener(){
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

                /*We do noot need to check or work with the accuracy changes for the sensor*/
            }

            override fun onSensorChanged(event: SensorEvent?) {
                val x = event!!.values[0]
                val y = event!!.values[1]
                val z = event!!.values[2]

                mAccelerateLast = mAccelerateCurrent
                mAccelerateCurrent = Math.sqrt(((x*x + y*y+ z*z).toDouble())).toFloat() as Float
                val delta  = mAccelerateCurrent - mAccelerateLast
                mAccelerate = mAccelerate * 0.9f+ delta

                if(mAccelerate > 12 ){
                    val prefs  = Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME,Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature",false)
                    if(isAllowed as Boolean){
                    Staticated.playNext("PlayNextNormal")
                }
                }

            }

        }


    }


}