package tech.devcrazelu.device_media;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DeviceVideosUtils {

    /**
     *
     * @param context
     * @return true if permission to read external storage is granted
     * and for API 29 and above, if ACCESS_MEDIA_LOCATION is also granted
     * else, false
     */
    public boolean doesAppHavePermission(Context context){
        try{
            String storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;

            int storagePermissionStatus = context.checkCallingOrSelfPermission(storagePermission);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String mediaLocationPermission = Manifest.permission.ACCESS_MEDIA_LOCATION;
                int mediaLocationPermissionStatus = context.checkCallingOrSelfPermission(mediaLocationPermission);
                return storagePermissionStatus == PackageManager.PERMISSION_GRANTED
                        && mediaLocationPermissionStatus == PackageManager.PERMISSION_GRANTED;
            }
            return storagePermissionStatus == PackageManager.PERMISSION_GRANTED;
        }catch(Exception e){
            Log.d("DeviceMediaPlugin", e.toString());
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<HashMap<String, Object>> getVideos (Context context){


        ArrayList<HashMap<String, Object>> videoList = new ArrayList<HashMap<String, Object>>();

        if(doesAppHavePermission(context)){
            try{
                Uri collection;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else {
                    collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }

                String[] projection = new String[] {
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.SIZE
                };

                String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DSC";

                try (Cursor cursor = context.getContentResolver().query(
                        collection,
                        projection,
                        null,
                        null,
                        sortOrder
                )) {
                    // Cache column indices.
                    assert cursor != null;
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    int nameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

                    while (cursor.moveToNext()) {
                        HashMap<String, Object> videoData= new HashMap<>();
                        // Get values of columns for a given video.
                        long id = cursor.getLong(idColumn);
                        String name = cursor.getString(nameColumn);
                        int size = cursor.getInt(sizeColumn);

                        Uri contentUri = ContentUris.withAppendedId(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        videoData.put("name",name);
                        videoData.put("size",size);
                        videoData.put("filePath",contentUri.getPath());
                        videoData = updateMetaData(contentUri, videoData);
                        videoList.add(videoData);
                    }
                }
                return videoList;
            }catch(Exception e){
                Log.d("DeviceMediaPlugin", e.toString());
                return videoList;
            }
        }
        return videoList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public HashMap<String, Object> updateMetaData(Uri uri, HashMap<String, Object> videoData) {
        File  file = new File(Objects.requireNonNull(uri.getPath()));

        if (file.exists()) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(file.getAbsolutePath());
                videoData.put("duration", retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                videoData.put("dateCreated", retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
                return videoData;
            } catch (Exception e) {
                Log.e("DeviceMediaPlugin", "Exception : " + e.toString());
                return videoData;
            }
        } else {
            Log.e("DeviceMediaPlugin", "File doesn´t exist.");
        }
        return videoData;
    }
}
