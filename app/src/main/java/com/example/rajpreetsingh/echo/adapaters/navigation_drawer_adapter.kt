package com.example.rajpreetsingh.echo.adapaters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.rajpreetsingh.echo.R
import com.example.rajpreetsingh.echo.activities.MainActivity
import com.example.rajpreetsingh.echo.fragments.AboutUsFragment
import com.example.rajpreetsingh.echo.fragments.FavoriteFragment
import com.example.rajpreetsingh.echo.fragments.MainScreenFragment
import com.example.rajpreetsingh.echo.fragments.SettingFragment
import kotlinx.android.synthetic.main.activity_main.view.*


// THIS IS ADAPTER FILE. WE CREATE CLASS.
// fOR THAT WE WRITE CLASS AND THEN THE FILE NAME
// THIS ADAPTER CLASS WILL EXTEND THE ADAPTER CLASS.
// RECYCLER VIEW IS DEFAULT ADAPTER CLASS
// iT TAKES A VIEW HOLDER OBJECT
// A VIEW HOLDER IS BASICALLY A CLASS THAT TAKES TOTAL CONTROL OF THE ELEMENTS INSIDE THE ADAPTER
// INSIDE <> WE CREATE VIEW HOLDER CLASS
// NavViewHolder IS INNER CLASS OF navigation_drawer_adapter CLASS OR WE CAN SAY THE VIEW HOLDER CLASS
// NavViewHolder EXTENDS VIEW HOLDER CLASS
// IT CONTAINS A OBJECT OF ITEMVIEW TYPE


// WE LINK THE XML FILE LAYOUT WE CREATED
class navigation_drawer_adapter(_contentList: ArrayList<String>, _getImages: IntArray, _context: Context):
        RecyclerView.Adapter<navigation_drawer_adapter.NavViewHolder>(){

    var contentList: ArrayList<String>? = null
    var getImages: IntArray? = null
    var mContext: Context? = null

    /*This is the constructor initialisation of the parameters. This converts the data passed from the parameters as the local params, which are used in this class*/
    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.mContext = _context
    }

    // LayoutInflater IS USED TO MANIPULATE ANDROID SCREEN USING PREDIFINED LAYOUTS, BU WE USE OURS
    // WE PASS CONTEXT, IT COULD BE FETCHED BY ViewGroup OBJECT WHICH IS PARENT
    // WE INFLATE TO USE OUR XML LAYOUT
    // THIS WILL RETURN US A OBJECT OF TYPE VIEW WHICH WE PASS IN NavViewHolder


    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {

        holder?.icon_GET?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.text_GET?.setText(contentList?.get(position))
        holder?.contentHolder?.setOnClickListener({

            if(position == 0){
                val main_screen_fragment = MainScreenFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, main_screen_fragment)
                        .commit()
            }
            else if(position == 1){
                val fav_screen_fragment = FavoriteFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, fav_screen_fragment)
                        .commit()
            }
            else if (position == 2){
                val setting_screen_fragment = SettingFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, setting_screen_fragment)
                        .commit()
            }
            else{
                val about_us_screen_fragment = AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, about_us_screen_fragment)
                        .commit()
            }
            MainActivity.Statified.drawerLayout?.closeDrawers()

        })
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        var itemView = LayoutInflater.from(parent?.context)
                // FIRST PARAM IS TO PASS OUR XML LAYOUT FILE
                //  SECOND IS VIEW GROUP OBJECT WHICH IS PARENT
                // THIRD PARAM IS OPTIONAL
                .inflate(R.layout.row_custom_nagivation_drawer,parent,false)
        val returnThis = NavViewHolder(itemView)
        return returnThis
    }

    override fun getItemCount(): Int {
        return ((contentList as ArrayList).size)
    }



    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        var icon_GET: ImageView?=null
        var text_GET: TextView?=null
        var contentHolder: RelativeLayout?=null

        init{
            icon_GET = itemView?.findViewById(R.id.icon_navdrawer)
            text_GET = itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }
    }
}