package ua.batyuk.dmytro.androidmaterialdesignapp

import android.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class TestNavigationActivity : AppCompatActivity() {

    var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_test)

        next()
    }

    fun previous() {
        onBackPressed()
    }

    fun next() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, TestNavigationFragment.createFragment(counter++))
            .addToBackStack(null)
            .commit()
    }

    fun nextClearTop() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//    for (fragment in supportFragmentManager.fragments) {
//        supportFragmentManager.popBackStack()
//        while (supportFragmentManager.popBackStackImmediate()) {
            Log.e("DIMA", "fragment popped immediate")
//        }
        next()
    }
}
