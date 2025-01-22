package jp.shsit.sustinaboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler()
        val timer = Timer(true)

        timer.schedule(
            object: TimerTask() {
                override fun run() {
                    handler.post {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)

                        finish()
                    }
                }
            }, 3000
        )
    }
}