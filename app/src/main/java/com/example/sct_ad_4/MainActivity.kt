package com.example.sct_ad_4

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sct_ad_4.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.io.OutputStream
import android.widget.Toast

/**
 * MainActivity
 *
 * A simple QR Code Scanner & Generator app.
 *
 * Features:
 *  - Scan a QR code using the device camera (via ZXing Android Embedded)
 *  - Generate a QR code bitmap from user-entered text (via ZXing Core)
 *  - Copy the result text to the clipboard
 *  - Share the result text using the system share sheet
 *  - Save the generated QR code image to the device gallery
 *  - Clear the generated QR / result
 *  - Runtime camera permission handling
 *
 * Uses:
 *  - ViewBinding for view access
 *  - Single Activity architecture (no MVVM, no Fragments)
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding instance
    private lateinit var binding: ActivityMainBinding

    // Holds the bitmap of the most recently generated QR code (for saving to gallery)
    private var generatedQrBitmap: Bitmap? = null

    /**
     * Launcher that starts ZXing's scan activity and receives the scan result.
     * Registered using the modern Activity Result API.
     */
    private val qrScanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            // User cancelled the scan
            showToast(getString(R.string.msg_scan_cancelled))
        } else {
            // Display the scanned content
            binding.tvResult.text = result.contents
            showToast(getString(R.string.msg_scan_success))
        }
    }

    /**
     * Launcher that requests the CAMERA runtime permission.
     * If granted, immediately starts the QR scanner.
     */
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchScanner()
        } else {
            showToast(getString(R.string.msg_camera_permission_denied))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtonListeners()
    }

    /**
     * Wires up all button click listeners.
     */
    private fun setupButtonListeners() {
        binding.btnGenerate.setOnClickListener {
            generateQrCode()
        }

        binding.btnScan.setOnClickListener {
            checkCameraPermissionAndScan()
        }

        binding.btnCopy.setOnClickListener {
            copyResultToClipboard()
        }

        binding.btnShare.setOnClickListener {
            shareResultText()
        }

        binding.btnClear.setOnClickListener {
            clearAll()
        }

        binding.btnSaveQr.setOnClickListener {
            saveQrToGallery()
        }
    }

    // ------------------------------------------------------------------
    // QR GENERATION
    // ------------------------------------------------------------------

    /**
     * Generates a QR code bitmap from the text entered by the user and
     * displays it in the ImageView. Includes input validation and
     * error handling for invalid/oversized input.
     */
    private fun generateQrCode() {
        val inputText = binding.editTextInput.text.toString().trim()

        // Input validation
        if (inputText.isEmpty()) {
            binding.inputLayout.error = getString(R.string.error_empty_input)
            return
        } else {
            binding.inputLayout.error = null
        }

        try {
            val qrWriter = QRCodeWriter()
            val bitMatrix = qrWriter.encode(
                inputText,
                BarcodeFormat.QR_CODE,
                QR_CODE_SIZE,
                QR_CODE_SIZE
            )

            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)

            generatedQrBitmap = bitmap
            binding.ivQrCode.setImageBitmap(bitmap)
            binding.tvResult.text = inputText

            binding.ivQrCode.visibility = android.view.View.VISIBLE
            binding.btnSaveQr.visibility = android.view.View.VISIBLE

            showToast(getString(R.string.msg_qr_generated))
        } catch (e: WriterException) {
            // Error handling for QR generation failures
            showToast(getString(R.string.error_qr_generation_failed))
            e.printStackTrace()
        } catch (e: Exception) {
            showToast(getString(R.string.error_unknown))
            e.printStackTrace()
        }
    }

    // ------------------------------------------------------------------
    // QR SCANNING
    // ------------------------------------------------------------------

    /**
     * Checks whether the CAMERA permission has already been granted.
     * If yes, launches the scanner directly. If no, requests the
     * permission at runtime.
     */
    private fun checkCameraPermissionAndScan() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchScanner()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    /**
     * Configures and launches the ZXing scan activity.
     */
    private fun launchScanner() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt(getString(R.string.scan_prompt))
            setCameraId(0) // Use the rear camera
            setBeepEnabled(true)
            setBarcodeImageEnabled(false)
            setOrientationLocked(false) // Allows landscape support while scanning
        }
        qrScanLauncher.launch(options)
    }

    // ------------------------------------------------------------------
    // CLIPBOARD & SHARE
    // ------------------------------------------------------------------

    /**
     * Copies the current result text to the system clipboard.
     */
    private fun copyResultToClipboard() {
        val resultText = binding.tvResult.text.toString()

        if (resultText.isBlank()) {
            showToast(getString(R.string.error_nothing_to_copy))
            return
        }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(CLIPBOARD_LABEL, resultText)
        clipboard.setPrimaryClip(clip)

        showToast(getString(R.string.msg_copied_to_clipboard))
    }

    /**
     * Shares the current result text using the system share sheet.
     */
    private fun shareResultText() {
        val resultText = binding.tvResult.text.toString()

        if (resultText.isBlank()) {
            showToast(getString(R.string.error_nothing_to_share))
            return
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, resultText)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)))
    }

    // ------------------------------------------------------------------
    // SAVE TO GALLERY
    // ------------------------------------------------------------------

    /**
     * Saves the currently generated QR code bitmap to the device gallery
     * using MediaStore (works without extra runtime permission on API 29+).
     */
    private fun saveQrToGallery() {
        val bitmap = generatedQrBitmap
        if (bitmap == null) {
            showToast(getString(R.string.error_no_qr_to_save))
            return
        }

        try {
            val filename = "QR_${System.currentTimeMillis()}.png"
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/SCT_AD_4"
                    )
                }
            }

            val resolver = contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (uri != null) {
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                outputStream?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                showToast(getString(R.string.msg_qr_saved))
            } else {
                showToast(getString(R.string.error_save_failed))
            }
        } catch (e: Exception) {
            showToast(getString(R.string.error_save_failed))
            e.printStackTrace()
        }
    }

    // ------------------------------------------------------------------
    // CLEAR
    // ------------------------------------------------------------------

    /**
     * Clears the input field, result text, and generated QR image.
     */
    private fun clearAll() {
        binding.editTextInput.text?.clear()
        binding.inputLayout.error = null
        binding.tvResult.text = ""
        binding.ivQrCode.setImageDrawable(null)
        binding.ivQrCode.visibility = android.view.View.GONE
        binding.btnSaveQr.visibility = android.view.View.GONE
        generatedQrBitmap = null

        showToast(getString(R.string.msg_cleared))
    }

    // ------------------------------------------------------------------
    // UTILITY
    // ------------------------------------------------------------------

    /**
     * Shows a short Toast message.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val QR_CODE_SIZE = 800
        private const val CLIPBOARD_LABEL = "QR_RESULT"
    }
}
