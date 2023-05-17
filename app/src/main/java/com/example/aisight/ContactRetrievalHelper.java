package com.example.aisight;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactRetrievalHelper {
    private static final String TAG = "ContactRetrievalHelper";

    public static List<String> getContactsByCustomLabel(Context context, String customLabel) {
        //
        List<String> phoneNumbers = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        // Retrieve the contacts associated with the custom label
        Cursor cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Phone.LABEL + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, customLabel},
                null
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNumbers.add(phoneNumber);
                }
            } finally {
                cursor.close();
            }
        }

        return phoneNumbers;
    }

}

