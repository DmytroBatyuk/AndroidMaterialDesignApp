package ua.batyuk.dmytro.androidmaterialdesignapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


private const val COUNTER_ARG = "counter"
class TestNavigationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val counter = arguments?.getInt(COUNTER_ARG) ?: 0
        val view = inflater.inflate(R.layout.fragment_test_navigation, container, false)
        view.findViewById<TextView>(R.id.textView).setText("Fragment #$counter")

        view.findViewById<Button>(R.id.prevButton).setOnClickListener {
            (activity as TestNavigationActivity).previous()
        }

        view.findViewById<Button>(R.id.nextButton).setOnClickListener {
            (activity as TestNavigationActivity).next()
        }

        view.findViewById<Button>(R.id.nextButtonResetTop).setOnClickListener {
            (activity as TestNavigationActivity).nextClearTop()
        }

        return view
    }

    companion object {
        fun createFragment(counter: Int): TestNavigationFragment {
            return TestNavigationFragment().apply {
                arguments = Bundle().apply {
                    putInt(COUNTER_ARG, counter)
                }
            }
        }
    }
}