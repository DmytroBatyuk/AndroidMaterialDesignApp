/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.example.android.fingerprintdialog

import android.annotation.TargetApi
import android.app.DialogFragment
import android.content.res.ColorStateList
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ua.batyuk.dmytro.androidmaterialdesignapp.R

@TargetApi(Build.VERSION_CODES.M)
class FingerprintAuthenticationDialogFragment : DialogFragment() {

    private lateinit var fingerprintImage: ImageView
    private lateinit var descriptionText: TextView
    private lateinit var errorText: TextView

    private lateinit var cryptoObject: FingerprintManager.CryptoObject

    private val fingerprintHelperCallback = object : FingerprintHelper.Callback {
        override fun onAuthenticated() {
            view.run {
                errorText.visibility = View.INVISIBLE
                val color = ContextCompat.getColor(context, android.R.color.holo_green_light)
                fingerprintImage.imageTintList = ColorStateList.valueOf(color)
                errorText.setTextColor(color)
                errorText.text = "Success"
                errorText.visibility = View.VISIBLE
            }

            view.postDelayed({
                dismiss()
            }, 1000)
        }

        override fun onAuthenticatedFailed(description: String) {
            onError(description)

            view.postDelayed(dismissShowErrorRunnable, 500)
        }
        
        private val dismissShowErrorRunnable = Runnable {
            errorText.visibility = View.INVISIBLE
            val color = ContextCompat.getColor(context, android.R.color.darker_gray)
            fingerprintImage.imageTintList = ColorStateList.valueOf(color)

        }

        override fun onError(description: String) {
            view.removeCallbacks(dismissShowErrorRunnable)
            view.run {
                errorText.visibility = View.INVISIBLE
                val color = ContextCompat.getColor(context, android.R.color.holo_red_dark)
                fingerprintImage.imageTintList = ColorStateList.valueOf(color)
                errorText.setTextColor(color)
                errorText.text = description
                errorText.visibility = View.VISIBLE
            }
        }

    }

    private lateinit var fingerprintHelper: FingerprintHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        retainInstance = true
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog.setTitle("Authorize With Fingerprint")
        return inflater.inflate(R.layout.fingerprint_dialog_container, container, false)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.cancel_button).setOnClickListener { dismiss() }
        fingerprintImage = view.findViewById(R.id.fingerprintImage)
        descriptionText = view.findViewById(R.id.descriptionText)
        errorText = view.findViewById(R.id.errorText)

        fingerprintHelper = FingerprintHelper(
            activity.getSystemService(FingerprintManager::class.java),
            fingerprintHelperCallback
        )
    }

    override fun onResume() {
        super.onResume()
        fingerprintHelper.startListening(cryptoObject)
    }

    override fun onPause() {
        super.onPause()
        fingerprintHelper.stopListening()
    }

    fun setCryptoObject(cryptoObject: FingerprintManager.CryptoObject) {
        this.cryptoObject = cryptoObject
    }

    companion object {
        public val TAG = FingerprintAuthenticationDialogFragment::class.java.simpleName
    }
}
