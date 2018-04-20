package ua.batyuk.dmytro.androidmaterialdesignapp

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.Placeholder
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var placeholder: Placeholder
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        constraintLayout = findViewById(R.id.root)
        placeholder = findViewById(R.id.placeholder)
        setOnClickListener(findViewById<View>(R.id.button_1))
        setOnClickListener(findViewById<View>(R.id.button_2))
        setOnClickListener(findViewById<View>(R.id.button_3))
        setOnClickListener(findViewById<View>(R.id.button_4))
    }

    private fun setOnClickListener(view: View) {
        view.setOnClickListener {
            //To make changes on placeholder view visible(snoozed)
            TransitionManager.beginDelayedTransition(constraintLayout)

            placeholder.setContentId(it.id)
        }
    }
}
