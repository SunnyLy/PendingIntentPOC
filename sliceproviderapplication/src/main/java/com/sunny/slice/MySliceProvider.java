package com.sunny.slice;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;

public class MySliceProvider extends SliceProvider {
    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */
    @Override
    public boolean onCreateSliceProvider() {
        return true;
    }

    /**
     * Converts URL to content URI (i.e. content://com.sunny.slice...)
     */
    @Override
    @NonNull
    public Uri onMapIntentToUri(@Nullable Intent intent) {
        // Note: implementing this is only required if you plan on catching URL requests.
        // This is an example solution.
        Uri.Builder uriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT);
        if (intent == null) return uriBuilder.build();
        Uri data = intent.getData();
        if (data != null && data.getPath() != null) {
            String path = data.getPath().replace("/hello", "");
            uriBuilder = uriBuilder.path(path);
        }
        Context context = getContext();
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.getPackageName());
        }
        return uriBuilder.build();
    }

    /**
     * Construct the Slice and bind data if available.
     */
    public Slice onBindSlice(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        SliceAction activityAction = createActivityAction();
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
        // Create parent ListBuilder.
        if ("/hello".equals(sliceUri.getPath())) {
            listBuilder.addRow(new ListBuilder.RowBuilder()
                    .setTitle("Hello World")
                    .setPrimaryAction(activityAction)
            );
        } else {
            listBuilder.addRow(new ListBuilder.RowBuilder()
                    .setTitle("URI not recognized")
                    .setPrimaryAction(activityAction)
            );
        }
        return listBuilder.build();
    }

    private SliceAction createActivityAction() {
        if (getContext() == null) {

            return null;
        }
        return SliceAction.create(
                PendingIntent.getActivity(
                        getContext(), 0, new Intent(getContext(), AttackerActivity.class), 0
                ),
                IconCompat.createWithResource(getContext(), R.drawable.ic_launcher_foreground),
                ListBuilder.ICON_IMAGE,
                "Open App"
        );
    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    @Override
    public void onSlicePinned(Uri sliceUri) {
        // When data is received, call context.contentResolver.notifyChange(sliceUri, null) to
        // trigger MySliceProvider#onBindSlice(Uri) again.
    }

    /**
     * Unsubscribe from data source if necessary.
     */
    @Override
    public void onSliceUnpinned(Uri sliceUri) {
        // Remove any observers if necessary to avoid memory leaks.
    }
}