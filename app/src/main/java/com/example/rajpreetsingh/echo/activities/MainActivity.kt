package com.example.rajpreetsingh.echo.activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.rajpreetsingh.echo.R
import com.example.rajpreetsingh.echo.activities.MainActivity.Statified.drawerLayout

import com.example.rajpreetsingh.echo.adapaters.navigation_drawer_adapter
import com.example.rajpreetsingh.echo.fragments.MainScreenFragment
import com.example.rajpreetsingh.echo.fragments.SongPlayingFragment

class MainActivity : AppCompatActivity() {
    object Statified {
        var drawerLayout: DrawerLayout? = null
        var notification: NotificationManager? = null


    }
    var tracknotificationBuilder: Notification? = null

    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()

    var images_for_navdrawer = intArrayOf(R.drawable.navigation_allsongs, R.drawable.navigation_favorites, R.drawable.navigation_settings, R.drawable.navigation_aboutus)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)

        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About Us")

        val toggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawerLayout?.setDrawerListener(toggle)
        toggle.syncState()


        val mainScreenFragment = MainScreenFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.details_fragment, mainScreenFragment, "MainScreenFragment")
                .commit()

        // Setup our Recycler view in navigation drawer
        // We have written recycler view code in activity_main.xml
        // <RecyclerView> is typecasting
        val navigation_adapter = navigation_drawer_adapter(navigationDrawerIconsList, images_for_navdrawer, this)
        navigation_adapter.notifyDataSetChanged()
        val navigation_recycler_view = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        // Layout manager is responsible for measuring and positioning itemView within recycler view
        //By changing layout manager we can implement standard list, uniform grid, or a staggerd grid, horizontal scrolling
        // LinearLayoutManager is basic one
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        // We add default animation
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter = navigation_adapter
        navigation_recycler_view.setHasFixedSize(true)




        val intent = Intent(this@MainActivity,MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this@MainActivity,(System.currentTimeMillis().toInt() as Int),intent,0)
        tracknotificationBuilder = Notification.Builder(this)
                .setContentTitle("A Track is playing in Background")
                .setSmallIcon(R.drawable.echo_logo)
                .setContentIntent(pIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()

        Statified.notification = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    }

    override fun onStart() {
        super.onStart()

        try{
            Statified.notification?.cancel(1978)


        }catch(e:Exception){
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try{
            if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                Statified.notification?.notify(1978, tracknotificationBuilder)
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try{
            Statified.notification?.cancel(1978)


        }catch(e:Exception){
            e.printStackTrace()
        }
    }
}


