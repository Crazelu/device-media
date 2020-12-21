import 'dart:typed_data';

class DeviceImage{
  final String imagePath;
  final String fileName;
  final Uint8List imageBytes;

  DeviceImage({this.imagePath, this.fileName, this.imageBytes});

  factory DeviceImage.fromMap(Map<dynamic, dynamic> json){
    json = Map<String, dynamic>.from(json);
    return DeviceImage(
      imagePath: json["imagePath"],
      fileName: json["fileName"],
      imageBytes: json["imageBytes"],
    );
  }
}