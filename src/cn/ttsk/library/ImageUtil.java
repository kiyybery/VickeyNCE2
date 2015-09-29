package cn.ttsk.library;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.*;
import java.math.BigDecimal;

import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;

/**
 * @author Nick create at 2011-3-17
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class ImageUtil {

    private static String TAG = ImageUtil.class.getSimpleName();


    public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            bitmap = android.media.ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        } catch (IllegalArgumentException ex) {
// Assume this is a corrupt video file
        } catch (RuntimeException ex) {
// Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
// Ignore failures while cleaning up.
            }
        }

        return bitmap;
    }

    //图片先缩放尺寸，再居中裁剪
    public static Bitmap scaleAndCutThumbnail(String filename, int resizeWidth, int resizeHeight) {
        Bitmap bitmap = null;
        Bitmap resBitmap = null;
        try {
            resBitmap = ImageUtil.extractPicture(filename);
            if (resBitmap != null) {
                bitmap = ImageUtil.cutImage(resBitmap, resizeWidth, resizeHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resBitmap != null && !resBitmap.equals(bitmap) && !resBitmap.isRecycled())
                resBitmap.recycle();
        }
        return bitmap;
    }

    public static Bitmap scaleThumbnail(String filename, int resizeWidth, int resizeHeight) throws Exception {
        Bitmap bmp = null;
        InputStream input = null;
        try {
            input = new FileInputStream(filename);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opts);

            if (opts.outHeight > resizeHeight || opts.outWidth > resizeWidth) {
                float scaleHeight = ((float) opts.outHeight) / (float) resizeHeight;
                float scaleWidth = ((float) opts.outWidth) / (float) resizeWidth;

                int initialSize = (int) scaleHeight;
                if (scaleWidth > scaleHeight) {
                    initialSize = (int) scaleWidth;
                }

                int roundedSize = 1;
                if (initialSize <= 8) {
                    roundedSize = 1;
                    while (roundedSize < initialSize) {
                        roundedSize <<= 1;
                    }
                } else {
                    roundedSize = (initialSize + 7) / 8 * 8;
                }
                IOUtils.closeQuietly(input);
                input = new FileInputStream(filename);

                opts.inSampleSize = roundedSize;
                opts.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeStream(input, null, opts);
            }
        } finally {
            IOUtils.closeQuietly(input);
        }
        return bmp;
    }

    //裁剪或者缩放宽高到统一尺寸
    public static Bitmap cutImage(Bitmap bitmap, int resizeWidth, int resizeHeight) {
        if(bitmap == null)
            return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == resizeWidth && height == resizeHeight) {
            return bitmap;
        } else {

            float scaleHeight = ((float) height) / (float) resizeHeight;
            float scaleWidth = ((float) width) / (float) resizeWidth;

            float scale = scaleHeight > scaleWidth ? scaleWidth : scaleHeight;

            int newWidth = (int) (resizeWidth * scale);
            int newHeight = (int) (resizeHeight * scale);

            if (width == newWidth && height == newHeight) {
            	return bitmap;
            }
            Log.d("cutImage", "width:" + width + " height:" + height + " newWidth:" + newWidth + " newHeight:" + newHeight + " scale:" + scale);

            int x = (width - newWidth) / 2;
            int y = (height - newHeight) / 2;
            return Bitmap.createBitmap(bitmap, x, y, newWidth, newHeight);
        }
    }

    public static Bitmap extractThumbnail(String filename, int sideLength) throws IOException {
        Bitmap bmp = null;
        InputStream input = null;
        try {
            input = new FileInputStream(filename);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(input, null, opts);

            int max = Math.max(opts.outWidth, opts.outHeight);

            int initialSize = max / sideLength;
            int roundedSize = 1;
            if (initialSize <= 8) {
                roundedSize = 1;
                while (roundedSize < initialSize) {
                    roundedSize <<= 1;
                }
            } else {
                roundedSize = (initialSize + 7) / 8 * 8;
            }

            IOUtils.closeQuietly(input);
            input = new FileInputStream(filename);

            opts.inSampleSize = roundedSize;
            opts.inJustDecodeBounds = false;

            bmp = BitmapFactory.decodeStream(input, null, opts);
//		} catch (Exception ex) {
//			Log.e(TAG, ex.toString());
        } finally {
            IOUtils.closeQuietly(input);
        }

        return bmp;
    }

    public static BitmapDrawable resizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        FileOutputStream outputStream = null;
        try {
            File imageFile = new File(path);
            if (imageFile.exists()) {
                imageFile.delete();
            }
            if (imageFile.createNewFile()) {
                outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static Bitmap extractPicture(String filename) {
        if (filename == null) {
            return null;
        }

        Bitmap bmp = null;
        InputStream input = null;


        try {
            input = new FileInputStream(filename);

            BitmapFactory.Options opts = new BitmapFactory.Options();

            opts.inJustDecodeBounds = false;

            bmp = BitmapFactory.decodeStream(input, null, opts);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            IOUtils.closeQuietly(input);
        }

        return bmp;
    }

    /**
     * 获取bitmap尺寸缩放解决OOM问题
     *
     * @throws java.io.IOException
     */
    public static Bitmap convertBitmap(String file, float maxSize) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            if(width_tmp > maxSize || height_tmp > maxSize){
            	BigDecimal bigDecimal;
                if(width_tmp >= height_tmp) {
                	bigDecimal = new BigDecimal(width_tmp/maxSize);
                } else {
                	bigDecimal = new BigDecimal(height_tmp/maxSize);
                }
                scale = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            }

            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = scale;
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis, null, op);
            fis.close();

        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        if(bitmap == null)
            return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }


    //生成圆角图片
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, final float roundPx) {
        if(bitmap == null)
            return null;
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, src, rect, paint);
            paint.setAntiAlias(true);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(16);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }
    
    public static Bitmap addShadowBitmap(Bitmap bitmap, Bitmap shadow) {
    	final float roundX = (shadow.getWidth() - bitmap.getWidth()) /2;
    	final float roundY = (shadow.getHeight() - bitmap.getHeight()) /2;
    	final float innerRoundPx = 10 * NCE2.screenWidthScale;
		int colorFull = 0x00000000;
		int colorBorder = 0xff000000;
    	Bitmap innerBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
    	Canvas innerCanvas = new Canvas(innerBitmap);
    	innerCanvas.drawARGB(0, 0, 0, 0);
    	Paint innerPaint = new Paint();
    	Rect innerRect = new Rect(0, 0, innerBitmap.getWidth(), innerBitmap.getHeight());
    	innerPaint.setColor(colorBorder);
    	innerCanvas.drawRoundRect(new RectF(innerRect), innerRoundPx, innerRoundPx, innerPaint);
    	innerPaint = new Paint();
		innerPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		innerCanvas.drawBitmap(bitmap, innerRect, innerRect, innerPaint);
    	
    	Bitmap output = Bitmap.createBitmap(shadow.getWidth(), shadow.getHeight(), Config.ARGB_8888);
		Canvas outerCanvas = new Canvas(output);
		outerCanvas.drawColor(colorFull);
		outerCanvas.drawBitmap(shadow, 0, 0, null);
		outerCanvas.drawBitmap(innerBitmap, roundX, roundY, null);

		return output;   
	}

}
