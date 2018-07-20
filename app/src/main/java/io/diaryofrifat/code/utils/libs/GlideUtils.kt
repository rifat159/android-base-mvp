package io.diaryofrifat.code.utils.libs

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import io.diaryofrifat.code.RifBaseApplication
import java.util.concurrent.ExecutionException

// TODO: Introduce more methods
class GlideUtils private constructor() {
    companion object {
        /**
         * This method loads image into ImageView using glide library but does not cache or crop.
         *
         * @param imageView ImageView where to load the image
         * @param source image location
         */
        fun normal(imageView: ImageView, source: Any) {
            default(imageView, source, false, ImageCropType.NONE, null)
        }

        /**
         * This method loads image into ImageView using glide library but does not cache or crop.
         * Provides callback for resource state.
         *
         * @param imageView ImageView where to load the image
         * @param source image location
         * @param requestListener listens to the state
         */
        fun normal(imageView: ImageView, source: Any, requestListener: RequestListener<Bitmap>) {
            default(imageView, source, false, ImageCropType.NONE, requestListener)
        }

        /**
         * This method loads image into ImageView using glide library but does not cache.
         * Provides callback for resource state and can crop the image too.
         *
         * @param imageView ImageView where to load the image
         * @param source image location
         * @param cropType how the image will be cropped while showing
         * @param requestListener listens to the state
         */
        fun skipCache(imageView: ImageView, source: Any, cropType: ImageCropType,
                      requestListener: RequestListener<Bitmap>?) {
            default(imageView, source, false, cropType, requestListener)
        }

        /**
         * This method shows image using glide library
         *
         * @param imageView ImageView where to load the image
         * @param source image location
         * @param shouldCache should cache the resource or not
         * @param cropType how the image will be cropped while showing
         * @param requestListener listens to the state
         */
        fun default(imageView: ImageView, source: Any, shouldCache: Boolean,
                    cropType: ImageCropType, requestListener: RequestListener<Bitmap>?) {

            val requestOptions = getRequestOptionsWithCaching(shouldCache)

            when (cropType) {
                ImageCropType.CIRCLE -> {
                    requestOptions.circleCrop()
                }

                ImageCropType.SQUARE -> {
                    requestOptions.centerCrop()
                }

                ImageCropType.ORIGINAL_RATIO -> {
                    requestOptions.fitCenter()
                }

                ImageCropType.CENTER_INSIDE -> {
                    requestOptions.centerInside()
                }

                ImageCropType.NONE -> {

                }
            }

            custom(imageView, source, requestOptions, requestListener)
        }

        /**
         * This method shows image using glide library taking custom options
         *
         * @param imageView ImageView where to load the image
         * @param source image location
         * @param requestOptions options going to be applied while showing image
         * @param requestListener listens to the state
         */
        fun custom(imageView: ImageView, source: Any, requestOptions: RequestOptions,
                   requestListener: RequestListener<Bitmap>?) {
            Glide.with(RifBaseApplication.getBaseApplicationContext())
                    .asBitmap()
                    .load(source)
                    .apply(requestOptions)
                    .listener(requestListener)
                    .into(imageView)
        }

        /**
         * This method returns the bitmap of an image from any source
         *
         * @param source image Uri, url or drawable
         * @param width required width
         * @param height required height
         * */
        // TODO: Test this method
        fun getBitmap(source: Any, width: Int, height: Int): Bitmap? {
            try {
                return Glide.with(RifBaseApplication.getBaseApplicationContext())
                        .asBitmap()
                        .load(source)
                        .apply(RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .submit(width, height)
                        .get()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * This method clears the image caches on the disk
         *
         * @param context current activity context
         * */
        // TODO: Test this method
        fun clearCache(context: Context) {
            Glide.get(context).clearDiskCache()
        }

        private fun getRequestOptionsWithCaching(shouldCache: Boolean): RequestOptions {
            return RequestOptions()
                    .skipMemoryCache(!shouldCache)
                    .diskCacheStrategy(if (shouldCache) {
                        DiskCacheStrategy.ALL
                    } else {
                        DiskCacheStrategy.NONE
                    })
        }
    }

    enum class ImageCropType {
        CIRCLE, SQUARE, ORIGINAL_RATIO, NONE, CENTER_INSIDE
    }
}