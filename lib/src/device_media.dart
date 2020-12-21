import 'dart:async';
import 'package:device_media/src/models/device_video.dart';
import 'package:flutter/services.dart';
import 'models/image_folder.dart';

class DeviceMedia {
  static const MethodChannel _channel =
  const MethodChannel('device_media');

  ///requests permission to read storage
  ///[isImage]
  ///set to true if permission is for images
  ///set to false if permission is for device videos
  ///defaults to true
  static Future<bool> requestPermission({isImage:true}) async{
    final bool status = await _channel.invokeMethod('requestPermission', <String, bool>{
      "isImage": isImage,
    });
    return status;
  }

  ///returns image directories and the images they contain
  ///supported image media extensions: [png, jpg, jpeg, gif]
  static Future<List<ImageFolder>> getFoldersImages() async{
    final List<dynamic> result = await _channel.invokeMethod('getFoldersImages');
    final List<ImageFolder> imageFolders = List<ImageFolder>.from(result.map((e) => ImageFolder.fromMap(e)));
    return imageFolders;

  }

  ///returns videos on device with metadata
  static Future<List<DeviceVideo>> getVideos() async{
    final List<dynamic> result = await _channel.invokeMethod('getVideos');
    print(result);
    final List<DeviceVideo> videos = List<DeviceVideo>.from(result.map((e) => DeviceVideo.fromMap(e)));
    return videos;

  }
}
