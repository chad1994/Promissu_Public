package com.simsimhan.promissu.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;

import com.simsimhan.promissu.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import timber.log.Timber;


public class StringUtil {
    private static final String TAG = "StringUtil";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static String addPaddingIfSingleDigit(int time) {
        if (time >= 0) {
            if (time < 10) {
                return "0" + time;
            } else {
                return "" + time;
            }
        } else {
            if (time < -10) {
                return "-0" + (time * -1);
            } else {
                return "" + time;
            }
        }
    }

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

    @Nullable
    public static String getHashKey(Context context) {
        final String TAG = "KeyHash";
        String keyHash = null;

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = new String(Base64.encode(md.digest(), 0));
                Timber.d(keyHash);
            }
        } catch (Exception e) {
            Timber.e(e.toString());
        }

        if (keyHash != null) {
            return keyHash;
        } else {
            return null;
        }
    }
}
