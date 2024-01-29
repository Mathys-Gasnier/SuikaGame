package fr.mathysg.testapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val msgHelloApp = findViewById<TextView>(R.id.helloMessage)

        val appName = resources.getString(R.string.app_name);

        msgHelloApp.text = resources.getString(R.string.hello_world, appName);
    }
}