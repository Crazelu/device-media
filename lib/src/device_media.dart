import 'dart:async';
import 'package:flutter/services.dart';
import 'models/image_folder.dart';

class DeviceMedia {
  static const MethodChannel _channel =
  const MethodChannel('device_media');

  ///requests permission to read storage
  static Future<bool> requestPermission() async{
    final bool status = await _channel.invokeMethod('requestPermission');
    return status;
  }

  ///returns image directories and the images they contain
  ///supported image media extensions: [png, jpg, jpeg, gif]
  static Future<List<ImageFolder>> getFoldersImages() async{
    final List<dynamic> result = await _channel.invokeMethod('getFoldersImages');
    final List<ImageFolder> imageFolders = List<ImageFolder>.from(result.map((e) => ImageFolder.fromMap(e)));
    return imageFolders;

  }
}
