import 'dart:typed_data';

class DeviceVideo {
  final String videoName;
  final String path;
  final int size;
  final int duration; //originally in micro seconds
  final DateTime
      creationDate; //date of creation in [yyyy MM dd] or [yyyyMMdd'T'HHmmss] on some devices
  final int width;
  final int height;
  final Uint8List thumbnailBytes;

  DeviceVideo(
      {this.videoName,
      this.path,
      this.size,
      this.duration,
      this.creationDate,
      this.width,
      this.height,
      this.thumbnailBytes});

  factory DeviceVideo.fromMap(Map<dynamic, dynamic> json) {
    json = Map<String, dynamic>.from(json);
    return DeviceVideo(
        videoName: json["name"],
        path: json["filePath"],
        size: json["size"],
        duration: json["duration"],
        creationDate: DateTime.tryParse(json["dateCreated"]),
        width: json["videoWidth"],
        height: json["videoHeight"],
        thumbnailBytes: Uint8List.fromList(List<int>.from(json["thumbNail"])));
  }
}
