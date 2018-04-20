package ua.batyuk.dmytro.androidmaterialdesignapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.Group
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button_1)?.setOnClickListener {
            val group = findViewById<Group>(R.id.group_1)
            group.visibility = if (group.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        findViewById<View>(R.id.button_2)?.setOnClickListener {
            val group = findViewById<Group>(R.id.group_2)
            group.visibility = if (group.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }
}
