package id.nns.nichat.ui.image

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.ContextMenu
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import id.nns.nichat.databinding.ActivityImageBinding
import java.util.*

class ImageActivity : AppCompatActivity() {

    companion object {
        const val OPEN_IMAGE = "open_image"
        private const val DEFAULT_URL = "https://firebasestorage.googleapis.com/v0/b/nichat-78f00.appspot.com/o/profile.png?alt=media&token=e79b0d36-3787-4e13-9ada-d19f57bd3f67"
    }

    private lateinit var binding: ActivityImageBinding
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this, "Please wait!", Toast.LENGTH_SHORT).show()

        val imageUrl = intent.getStringExtra(OPEN_IMAGE) ?: DEFAULT_URL
        webView = binding.imageWeb.apply {
            settings.apply {
                setSupportZoom(true)
                javaScriptEnabled = true
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                webViewClient = WebViewClient()
            }
            loadUrl(imageUrl)
        }
        registerForContextMenu(webView)
    }

    @Suppress("DEPRECATION")
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val webViewHitTestResult = webView.hitTestResult

        if (webViewHitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                webViewHitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

            menu?.setHeaderTitle("Download Image?")
            menu?.add(0, 1, 0, "Yes")
                ?.setOnMenuItemClickListener {
                    val imageUrl = webViewHitTestResult.extra

                    if (URLUtil.isValidUrl(imageUrl)) {
                        val request = DownloadManager.Request(Uri.parse(imageUrl)).apply {
                            setDescription("Downloading ...")
                            allowScanningByMediaScanner()
                            setNotificationVisibility(
                                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                            )
                            setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                "Nichat/${UUID.randomUUID()}.png"
                            )
                        }

                        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        downloadManager.enqueue(request)

                        Toast.makeText(
                            this@ImageActivity,
                            "Downloading ...",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ImageActivity,
                            "Ooops, something went wrong.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    false
                }
        }
    }

}