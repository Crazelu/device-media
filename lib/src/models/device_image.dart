class DeviceImage {
  final String imagePath;
  final String fileName;

  DeviceImage({this.imagePath, this.fileName});

  factory DeviceImage.fromMap(Map<dynamic, dynamic> json) {
    json = Map<String, dynamic>.from(json);
    return DeviceImage(
      imagePath: json["imagePath"],
      fileName: json["fileName"],
    );
  }
}
