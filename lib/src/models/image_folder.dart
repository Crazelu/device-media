import 'package:device_media/src/models/device_image.dart';

class ImageFolder{
  final String folderName;
  final String folderPath;
  final List<DeviceImage> images;

  ImageFolder({this.folderName, this.images, this.folderPath});

  factory ImageFolder.fromMap(Map<dynamic, dynamic> json){
    json = Map<String, dynamic>.from(json);
    return ImageFolder(
        folderPath: json["folderPath"],
        folderName: json["folderName"] ?? '',
        images: List<DeviceImage>.from(json["images"].map((e) => DeviceImage.fromMap(e)))
    );
  }
}