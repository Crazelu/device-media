package tech.devcrazelu.device_media;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DeviceImagesUtils {

    /**
     *
     * @param context
     * @return  a list of all the directories with image media
     * and images with basic images data for individual image file
     * in each directory
     */

    public ArrayList<HashMap<String, Object>> getFoldersImages(Context context){
        try{
            ArrayList<HashMap<String, Object>> folderImages = new ArrayList<>();

            //checks if user has permission to read from external storage
            if(doesAppHavePermission(context)){
                File rootDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

                ArrayList<String> folderPathsList = getFolderPathsWithImages(rootDir);

                if (folderPathsList != null) {
                    for(String folderPath: folderPathsList){
                        HashMap<String, Object> folder = new HashMap<>();
                        folder.put("folderPath", folderPath);
                        folder.put("folderName", folderPath.substring(folderPath.lastIndexOf('/') + 1));
                        folder.put("images", getImagesInPath(folderPath));
                        folderImages.add(folder);
                    }
                }
            }else{
                Log.d("DeviceMediaPlugin", "Permission not granted. Call requestPermission first");
            }

            return folderImages;
        }catch(Exception e){
            Log.d("DeviceMediaPlugin", e.toString());
            return null;
        }

    }

    /**
     *
     * @param context
     * @return true if permission to read external storage is granted
     * else, false
     */
    public boolean doesAppHavePermission(Context context){
    try{
        String requiredPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int status = context.checkCallingOrSelfPermission(requiredPermission);
        return status == PackageManager.PERMISSION_GRANTED;
    }catch(Exception e){
        Log.d("DeviceMediaPlugin", e.toString());
        return false;
    }
    }

    /**
     *
     * @param imagePath
     * @return true if imagePath ends with any image extension
     * included for our use case else, false
     */
    private boolean isImage(String imagePath){
        try{
            return (imagePath.endsWith(".png")
                    || imagePath.endsWith(".jpg")
                    || imagePath.endsWith(".jpeg")
                    || imagePath.endsWith(".gif"));
        }catch(Exception e){
            Log.d("DeviceMediaPlugin", e.toString());
            return false;
        }
    }

    /**
     *
     * @param path
     * @return array list of a HashMap containing data,
     * such as "imagePath" & "fileName",
     * about individual images in the given directory [path]
     */
    private ArrayList<HashMap<String, Object>> getImagesInPath(String path){
        ArrayList<HashMap<String, Object>> result =  new ArrayList<>();
        try{

            String[] filenames = new String[0];
            File imagesFolder = new File(path);
            if (imagesFolder.exists()) {
                filenames = imagesFolder.list();
            }
            assert filenames != null;
            for(String fileName: filenames){
                if (isImage(fileName)){
                    HashMap<String, Object> imageData = new HashMap<>();
                    Log.d("DeviceMediaPlugin", fileName);
                    String filePath = imagesFolder.getPath() + "/" + fileName;
                    imageData.put("imagePath", filePath);
                    imageData.put("fileName", fileName);
                    result.add(imageData);
                }
            }


            return result;
        }catch(Exception e){
            Log.d("DeviceMediaPlugin", e.toString());
            return result;
        }
    }

    /**
     *
     * @param rootDir
     * Scans all the files in given directory and returns list
     * of formatted file paths where image media with extensions [png, jpg, jpeg, gif]
     * were found.
     * Image extensions list can be extended for a larger use case
     * @return array list of directories's paths with image media
     */
    private ArrayList<String> getFolderPathsWithImages(File rootDir){
        try{
            ArrayList<String> fileList = new ArrayList<>();
            File[] listFile = rootDir.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (File file : listFile) {
                    if (file.isDirectory()) {
                        ArrayList<String> temp =  getFolderPathsWithImages(file);
                        if(temp != null && !temp.isEmpty()){
                            fileList.addAll(temp);
                        }
                    }
                    else {
                        String fileName = file.getName();
                        if (isImage(fileName) && file.canRead())
                        {
                            String filePath = file.getPath();
                            String temp = filePath.substring(0, filePath.lastIndexOf('/'));

                            //checks if path is already in the list
                            //filters Android/data paths

                            if (!fileList.contains(temp) && !temp.contains("emulated/0/Android/data"))
                                fileList.add(temp);
                        }
                    }
                }
            }
            Log.d("DeviceMediaPlugin", fileList.toString());
            return fileList;
        }catch(Exception e){
            Log.d("DeviceMediaPlugin", e.toString());
            return null;
        }
    }


}
