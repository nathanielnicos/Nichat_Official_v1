package id.nns.nichat.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.canhub.cropper.CropImage

@Suppress("DEPRECATION")
class CropActivityResultContract(
    private val ctx: Context,
    private val aspectRatioX: Int?,
    private val aspectRatioY: Int?
) : ActivityResultContract<Any?, Uri?>() {

    override fun createIntent(context: Context, input: Any?): Intent {
        return if (aspectRatioNull()) {
            CropImage.activity()
                .getIntent(ctx)
        } else {
            CropImage.activity()
                .setAspectRatio(aspectRatioX ?: 1, aspectRatioY ?: 1)
                .getIntent(ctx)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return CropImage.getActivityResult(intent)?.uriContent
    }

    private fun aspectRatioNull() : Boolean =
        aspectRatioX == null && aspectRatioY == null

}