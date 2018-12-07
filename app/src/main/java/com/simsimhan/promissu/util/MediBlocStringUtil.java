package com.simsimhan.promissu.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;

import com.simsimhan.promissu.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MediBlocStringUtil {
    private static final String TAG = "MediBlocStringUtil";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static boolean isStringEmpty(String source) {
        return source == null || source.isEmpty();
    }

    public static Bitmap decodeEncodedStringToBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String encodeFileToBase64Binary(Uri fileUri) {
        try {
            Bitmap bm = BitmapFactory.decodeFile(fileUri.getPath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isInAddressFormat(String addressCandidate) {
        if (addressCandidate != null) {
            Pattern addressFormat = Pattern.compile("^(0x)?[0-9a-f]{40}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = addressFormat.matcher(addressCandidate);
            return matcher.find();
        } else {
            return false;
        }
    }

    public static Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static boolean isAllInSmallCaps(String addressCandidate) {
        if (addressCandidate != null) {
            Pattern addressFormat = Pattern.compile("^(0x)?[0-9a-f]{40}$");
            Matcher matcher = addressFormat.matcher(addressCandidate);
            return matcher.find();
        } else {
            return false;
        }
    }

    public static boolean isAllInCaps(String addressCandidate) {
        if (addressCandidate != null) {
            Pattern addressFormat = Pattern.compile("^(0x)?[0-9A-F]{40}$");
            Matcher matcher = addressFormat.matcher(addressCandidate);
            return matcher.find();
        } else {
            return false;
        }
    }
}
