package com.example.rajpreetsingh.echo.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.example.rajpreetsingh.echo.R

class Splash_Screen : AppCompatActivity() {


    fun display_splash_screen(){
        Handler().postDelayed({
            var startAct = Intent(this@Splash_Screen, MainActivity::class.java)
            startActivity(startAct)
            this.finish()
        },1000)
    }


    var permissionString = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.PROCESS_OUTGOING_CALLS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash__screen)

        if(!hasPermission(this@Splash_Screen, *permissionString)){
            // WE have to ask for permission

            ActivityCompat.requestPermissions(this@Splash_Screen,permissionString,131)

        }else{
            display_splash_screen()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            131 ->{
                if(grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED){

                    display_splash_screen()

                }else{
                    Toast.makeText(this@Splash_Screen, "Please grant all the permissions", Toast.LENGTH_SHORT).show()
                    this.finish()
                }
                return
            }
            else->{
                Toast.makeText(this@Splash_Screen, "Something went wrong", Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }
        }
    }


    fun hasPermission(context: Context, vararg permissions: String): Boolean{
        var hasAllPermission = true
        for(permission in permissions){
            var re = context.checkCallingOrSelfPermission(permission)
            if(re != PackageManager.PERMISSION_GRANTED){
                hasAllPermission = false
            }
        }
        return hasAllPermission
    }

}

private fun Handler.postDelayed(function: () -> Unit) {

}
