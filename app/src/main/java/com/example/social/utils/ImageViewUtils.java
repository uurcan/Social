package com.example.social.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.util.Objects;

public class ImageViewUtils {

    private Activity context;

    private Drawable imageViewDrawable;
    private int finalImageWidth;
    private int finalImageHeight;
    private boolean requireResizingOfBitmap;

    private void cacheResizedImage(ImageView imageView) {
        imageViewDrawable = imageView.getDrawable();
        int imageRealWidth = imageViewDrawable.getIntrinsicWidth()*3;
        int imageRealHeight = imageViewDrawable.getIntrinsicHeight()*3;

        Point screenDimensions = getScreenDimensions(context);
        final int screenWidth = screenDimensions.x;
        final int screenHeight = screenDimensions.y;

        while (imageRealWidth > screenWidth || imageRealHeight > screenHeight) {
            imageRealWidth *= 0.9;
            imageRealHeight *= 0.9;
        }

        finalImageWidth = imageRealWidth;
        finalImageHeight = imageRealHeight;

        requireResizingOfBitmap = true;

    }

    public static void enablePopUpOnClick(final Activity context, final ImageView imageView) {
        new ImageViewUtils().internalEnablePopUpOnClick(context, imageView);
    }

    private void internalEnablePopUpOnClick(final Activity context, final ImageView imageView) {

        this.context = context;


        if (imageViewDrawable != imageView.getDrawable()) {
            ImageViewUtils.this.cacheResizedImage(imageView);
        }

        ImageView poppedImageView = new ImageView(context);

        if (requireResizingOfBitmap) {
            Bitmap bitmap = ImageViewUtils.this.drawableToBitmap(imageViewDrawable);
            BitmapDrawable resizedBitmapDrawable = new BitmapDrawable(
                    context.getResources(),
                    Bitmap.createScaledBitmap(bitmap, finalImageWidth, finalImageHeight, false));
            poppedImageView.setBackgroundDrawable(resizedBitmapDrawable);
        } else {
            poppedImageView.setBackgroundDrawable(imageViewDrawable);
        }

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(poppedImageView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(null);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        }


    @SuppressWarnings("deprecation")
    private Point getScreenDimensions(Activity context) {
        // Get screen size
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            display.getRealSize(size);
        }
        else if (currentApiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
            display.getSize(size);
        } else{
            size.x = display.getWidth();
            size.y = display.getHeight();
        }
        return size;
    }

    private Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
