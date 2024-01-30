package fr.mathysg.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var ballView: BallView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ballView = BallView(this)

        setContentView(ballView)
    }
}