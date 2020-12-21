package tech.devcrazelu.device_media;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;

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
          new PermissionTask(context, result, activity).execute();
        break;

      case "getFoldersImages":
          new GetImagesFoldersTask(context, result).execute();
         
        break;
      case "getAllImages":

        break;


      default:
        result.notImplemented();
    }
  }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
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

            if (folderImages.isEmpty()) {
                result.error("DeviceMediaPlugin", "Permission required", "Call requestPermission first");
            } else {
                result.success(folderImages);
            }
            return null;
            }
        }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private static class PermissionTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Result result;
        private Activity activity;

        public  PermissionTask(Context context,Result result,Activity activity){
            this.context = context;
            this.result = result;
            this.activity = activity;
        }

        protected Void doInBackground(Void... params) {
            DeviceImagesUtils imagesUtils = new DeviceImagesUtils();
            if (!imagesUtils.doesAppHavePermission(context)) {

                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            }
            result.success(true);
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
