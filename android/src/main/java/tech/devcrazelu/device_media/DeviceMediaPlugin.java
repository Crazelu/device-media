package tech.devcrazelu.device_media;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** DeviceMediaPlugin */
public class DeviceMediaPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity activity;
  private Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "device_media");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
  }


  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull final Result rawResult) {
    final Result result = new MethodResultWrapper(rawResult);
    switch (call.method) {
      case "requestPermission":
          final boolean isImage = call.argument("isImage");
          new PermissionTask(context, result, activity, isImage).execute();
        break;

      case "getFoldersImages":
          new GetImagesFoldersTask(context, result).execute();
         
        break;
      case "getVideos":
          new VideosTask(context, result).execute();
        break;


      default:
        result.notImplemented();
    }
  }

    private static class GetImagesFoldersTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Result result;
        
        public  GetImagesFoldersTask(Context context,Result result){
            this.context = context;
            this.result = result;
        }

        protected Void doInBackground(Void... params) {
            DeviceImagesUtils imagesUtils = new DeviceImagesUtils();
            ArrayList<HashMap<String, Object>> folderImages = imagesUtils.getFoldersImages(context);


            if (folderImages == null) {
                result.error("DeviceMediaPlugin", "Some error occurred", "Check logs");
            }

           else if (folderImages.isEmpty()) {
                result.error("DeviceMediaPlugin", "Permission required", "Call requestPermission first");
            } else {
                result.success(folderImages);
            }
            return null;
            }
        }



    private static class PermissionTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Result result;
        private Activity activity;
        private boolean isImage;

        public  PermissionTask(Context context,Result result,Activity activity, boolean isImage){
            this.context = context;
            this.result = result;
            this.activity = activity;
            this.isImage = isImage;
        }

        protected Void doInBackground(Void... params) {
            if(isImage){
                DeviceImagesUtils imagesUtils = new DeviceImagesUtils();
                if (!imagesUtils.doesAppHavePermission(context)) {

                    ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                }
            }
            else{
                //app is trying to get ACCESS_MEDIA_LOCATION permission for videos too
                //if API level >= 29
                DeviceVideosUtils videosUtils = new DeviceVideosUtils();
                if (!videosUtils.doesAppHavePermission(context)) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, 4);
                    }
                }
            }
            result.success(true);

            return null;
        }
    }


    private static class VideosTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Result result;

        public  VideosTask(Context context,Result result){
            this.context = context;
            this.result = result;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected Void doInBackground(Void... params) {
            DeviceVideosUtils videosUtils = new DeviceVideosUtils();
            ArrayList<HashMap<String, Object>> videosList =  videosUtils.getVideos(context);
            if (videosList == null) {
                result.error("DeviceMediaPlugin", "Some error occurred", "Check logs");
            }

            if (Objects.requireNonNull(videosList).isEmpty()) {
                result.error("DeviceMediaPlugin", "Permission required", "Call requestPermission first");
            } else {
                result.success(videosList);
            }
            return null;
        }
    }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onDetachedFromActivity() {
    //TODO("Not yet implemented")
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // TODO("Not yet implemented")
  }
}
