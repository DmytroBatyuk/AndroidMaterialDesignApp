package ua.batyuk.dmytro.androidmaterialdesignapp

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.SharedPreferences
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.*
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatCheckBox
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.android.fingerprintdialog.FingerprintAuthenticationDialogFragment
import java.security.KeyStore
import java.security.KeyStoreException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private val ANDROID_KEY_STORE = "AndroidKeyStore"
private val KEY_NAME = "testName"
private val TAG = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var button: Button

    private var failedFingerprintInitialization = false
    private var failedCreateKey = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val checkBox: AppCompatCheckBox = findViewById(R.id.checkBox)
        checkBox.isChecked = isFingerprintEnabled()
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("enabled", isChecked).apply()
        }

        textView = findViewById(R.id.textView)

        button = findViewById(R.id.button)
        button.setOnClickListener {
            if (isFingerprintEnabled()) {
                showCheckFingerprintDialog()
            } else {
                AlertDialog.Builder(this)
                    .setMessage("Fingeprint is not enabled yet")
                    .setPositiveButton("OK") {
                        dialog, _ -> dialog.dismiss()
                    }
                    .show()
            }
        }

        if (isSupportFingerprint()) {
            setupKeyStoreAndKeyGenerator()
            setupCiphers()
        } else {
            showError("Your Android version doesn't support fingerprint functionality")
        }
    }

    private fun isFingerprintEnabled() = sharedPreferences.getBoolean("enabled", true)

    override fun onStart() {
        super.onStart()
        if (isSupportFingerprint() && !failedFingerprintInitialization) {
            if (checkKeyguardAndFingerprintAccessibility()) {
                createKey(KEY_NAME)
                showSuccess("click to button to check fingerprint functionality")
                failedCreateKey = false
            } else {
                failedCreateKey = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        button.isEnabled = !failedFingerprintInitialization && !failedCreateKey
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkKeyguardAndFingerprintAccessibility(): Boolean {
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        if (!keyguardManager.isKeyguardSecure) {
            // Fingerprint or lock screen hasn't been setup yet
            Log.e("DIMA", "device hasn't fingerprint or lock screen setup. Skipping")
            showError(getString(R.string.setup_lock_screen))
            return false
        }

        val fingerprintManager = getSystemService(FingerprintManager::class.java)
        if (!fingerprintManager.isHardwareDetected) {
            Log.e("DIMA", "device hasn't hardware fingerprint. Skipping")
            showError("Your device hasn't hardware fingerprint")
            return false
        }
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            // No fingerprints are registered
            Log.e("DIMA", "device hasn't enrolled fingerprint. Skipping")
            showError(getString(R.string.register_fingerprint))
            return false
        }

        return true
    }


    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyGenerator? = null
    private var cipher: Cipher? = null

    private fun setupKeyStoreAndKeyGenerator() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        } catch (e: KeyStoreException) {
            //TODO: log
            failedFingerprintInitialization = true
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        } catch (e: Exception) {
            //TODO: log
            failedFingerprintInitialization = true
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun createKey(keyName: String) {
        try {
            keyStore?.load(null)
            val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val builder = KeyGenParameterSpec.Builder(keyName, keyProperties)
                .setBlockModes(BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(ENCRYPTION_PADDING_PKCS7)

            keyGenerator?.run {
                init(builder.build())
                generateKey()
            }
        } catch (e: Exception) {
            //TODO: log error
            failedFingerprintInitialization = true
        }
    }

    private fun isSupportFingerprint(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun setupCiphers() {
        try {
            val cipherString = "$KEY_ALGORITHM_AES/$BLOCK_MODE_CBC/$ENCRYPTION_PADDING_PKCS7"
            cipher = Cipher.getInstance(cipherString)
        } catch (e: Exception) {
            //TODO: log error
            failedFingerprintInitialization = true
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun initCipher(keyName: String) {
        try {
            keyStore?.load(null)
            cipher?.init(Cipher.ENCRYPT_MODE, keyStore?.getKey(keyName, null) as SecretKey)
        } catch (e: Exception) {
            //TODO: log error
            failedFingerprintInitialization = true
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun showCheckFingerprintDialog() {
        FingerprintAuthenticationDialogFragment().apply {
            setCryptoObject(FingerprintManager.CryptoObject(cipher))
            initCipher(KEY_NAME)
        }.show(fragmentManager, FingerprintAuthenticationDialogFragment.TAG)

    }


    private fun showError(text: String) {
        updateTextView(text, android.R.color.holo_red_dark)
    }

    private fun showWarning(text: String) {
        updateTextView(text, android.R.color.holo_orange_light)
    }

    private fun showSuccess(text: String) {
        updateTextView(text, android.R.color.holo_green_dark)
    }

    private fun updateTextView(text: String, colorRes: Int) {
        textView.text = text
        textView.setTextColor(ContextCompat.getColor(this, colorRes))
    }
}
