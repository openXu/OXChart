package com.openxu.chart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BeiZhenbo on 2017.03.10.
 */

public class DownUserImage {

    private static byte[] data;
    private static Bitmap bm = null;
    private static Map<String, Bitmap> bms = new HashMap<>();
    public static Bitmap Base64ToBitmap(String str) {
        bm = bms.get(str);
        if (bm == null) {
            if (!TextUtils.isEmpty(str)) {
                try {
                    if (data != null) {
                        data = null;
                    }
                    data = Base64.decode(str, Base64.DEFAULT);
                    try {
                        bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                    }
                    bms.put(str, bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }
    /*
     * bitmap转base64
     * */
    private static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        Log.e("gadggsddg", "转换为数组："+bytes.length);
        Bitmap btmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Log.e("gadggsddg", "转换："+btmap);
        return btmap;
    }
}
