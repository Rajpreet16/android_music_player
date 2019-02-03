package com.example.rajpreetsingh.echo.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Parcel
import android.os.Parcelable
import com.example.rajpreetsingh.echo.DataBase.FavoritesDataBase.Staticated.COLUMN_ID
import com.example.rajpreetsingh.echo.DataBase.FavoritesDataBase.Staticated.COLUMN_SONG_ARTIST
import com.example.rajpreetsingh.echo.DataBase.FavoritesDataBase.Staticated.COLUMN_SONG_PATH
import com.example.rajpreetsingh.echo.DataBase.FavoritesDataBase.Staticated.COLUMN_SONG_TITLE
import com.example.rajpreetsingh.echo.DataBase.FavoritesDataBase.Staticated.DB_NAME
import com.example.rajpreetsingh.echo.DataBase.FavoritesDataBase.Staticated.DB_VERSION

import com.example.rajpreetsingh.echo.DataBase.FavoritesDataBase.Staticated.TABLE_NAME
import com.example.rajpreetsingh.echo.Songs


class FavoritesDataBase : SQLiteOpenHelper {

    var _songList = ArrayList<Songs>()

    object Staticated{
        val DB_NAME = "FavoriteDatabase"
        val TABLE_NAME = "FavoriteTable"
        val COLUMN_ID = "SongID"
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val COLUMN_SONG_PATH = "SongPath"
        val DB_VERSION = 1
    }

    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {

        sqliteDatabase?.execSQL("CREATE TABLE " + TABLE_NAME + "( " + COLUMN_ID +
                " INTEGER," + COLUMN_SONG_ARTIST + " STRING," + COLUMN_SONG_TITLE + " STRING,"
                + COLUMN_SONG_PATH + " STRING);")
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?,
                version: Int) : super(context, name, factory, version) {}

    constructor(context: Context?) : super(context, DB_NAME,null, DB_VERSION) {}


    fun storeAsFavorite(id: Int?, artist: String?, songTitle: String?, path: String?) {

        val db = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(COLUMN_ID, id)
        contentValues.put(COLUMN_SONG_ARTIST, artist)
        contentValues.put(COLUMN_SONG_TITLE, songTitle)
        contentValues.put(COLUMN_SONG_PATH, path)

        db.insert(TABLE_NAME, null, contentValues)

        db.close()
    }


    fun queryDBList(): ArrayList<Songs>? {

        try {


            val db = this.readableDatabase
            val query_params = "SELECT * FROM " + FavoritesDataBase.Staticated.TABLE_NAME
            val cSor = db.rawQuery(query_params, null)

            if (cSor.moveToFirst()) {

                do {
                    var _id = cSor.getInt(cSor.getColumnIndexOrThrow(FavoritesDataBase.Staticated.COLUMN_ID))
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(FavoritesDataBase.Staticated.COLUMN_SONG_ARTIST))
                    var _title = cSor.getString(cSor.getColumnIndexOrThrow(FavoritesDataBase.Staticated.COLUMN_SONG_TITLE))
                    var _path = cSor.getString(cSor.getColumnIndexOrThrow(FavoritesDataBase.Staticated.COLUMN_SONG_PATH))

                    _songList.add(Songs(_id.toLong(), _title, _artist, _path, 0))

                } while (cSor.moveToNext())


            } else {
                return null
            }

        } catch (e: Exception)
        {
            e.printStackTrace()
        }

        return _songList
    }


    fun checkifIdExist(_id: Int):Boolean{

        var storeId = -1090
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + FavoritesDataBase.Staticated.TABLE_NAME + " WHERE SongID = '$_id'"
        val cSor = db.rawQuery(query_params, null)


        if(cSor.moveToFirst()){
            do{
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(FavoritesDataBase.Staticated.COLUMN_ID))
            }
            while(cSor.moveToNext())
        }
        else{
            return false
        }
        return storeId != -1090

    }

    fun deleteFavorite(_id: Int){
        val db = this.writableDatabase
        db.delete(FavoritesDataBase.Staticated.TABLE_NAME, FavoritesDataBase.Staticated.COLUMN_ID + '=' + _id,null)
        db.close()
    }

    fun check(): Int{
        var counter = 0
        val db = this.readableDatabase
        val query_params = " SELECT * FROM "+ FavoritesDataBase.Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if(cSor.moveToFirst()){

            do{
                counter = counter + 1
            }
            while(cSor.moveToNext())
        }
        else{
            return 0
        }
        return counter
    }

}