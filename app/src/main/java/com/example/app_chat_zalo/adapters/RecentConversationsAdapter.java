package com.example.app_chat_zalo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Base64;

public class RecentConversationsAdapter {

    private Bitmap getConversionImage(String encodeImage){
        byte[] bytes = Base64.getDecoder().decode(encodeImage);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
