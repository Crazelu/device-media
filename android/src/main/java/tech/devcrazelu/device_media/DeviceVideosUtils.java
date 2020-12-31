package tech.devcrazelu.device_media;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DeviceVideosUtils {

    private static String TAG = "DeviceMediaPlugin";

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
            Log.d(TAG, e.toString());
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<HashMap<String, Object>> getVideos (Context context){


        ArrayList<HashMap<String, Object>> videoList = new ArrayList<>();

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
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.VideoColumns.DATA
                };

                String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";

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
                    int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);

                    while (cursor.moveToNext()) {
                        HashMap<String, Object> videoData = new HashMap<>();
                        // Get values of columns for a given video.
                        long id = cursor.getLong(idColumn);
                        String name = cursor.getString(nameColumn);
                        int size = cursor.getInt(sizeColumn);

                        String path  = cursor.getString(pathColumn);

                        // Stores column values and the thumb nail (in base 64) in a local object
                        // that represents the video file.

                        Bitmap thumbNail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
                        videoData.put("name", name);
                        videoData.put("size", size);
                        videoData.put("filePath", path);
                        videoData.put("thumbNail", bitmapToBase64(thumbNail));
                        ArrayList<String> metaData = getMetaData(path);
                        if (metaData.size() == 4) {
                            videoData.put("duration", Integer.parseInt(metaData.get(0)));
                            videoData.put("dateCreated", metaData.get(1));
                            videoData.put("videoWidth", Integer.parseInt(metaData.get(2)));
                            videoData.put("videoHeight", Integer.parseInt(metaData.get(3)));
                        }
                        videoList.add(videoData);
                    }


                }
                return videoList;
            }catch(Exception e){
                Log.d(TAG, e.toString());
                return videoList;
            }
        }
        return videoList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public  ArrayList<String> getMetaData(String path) {
        File  file = new File(path);
        ArrayList<String> result = new ArrayList<>();
        if (file.exists()) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(file.getAbsolutePath());
                result.add(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                result.add( retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
                result.add(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                result.add(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                retriever.release();

                return result;
            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e.toString());
                return result;
            }
        } else {
            Log.e(TAG, "File doesn´t exist");
        }
        return result;
    }

    private byte[] bitmapToBase64(Bitmap bitmap){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}
