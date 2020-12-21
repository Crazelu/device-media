class DeviceVideo{
  final String videoName;
  final String path;
  final int size;
  final int duration; //originally in micro seconds
  final String creationDate; //date of creation in [yyyy MM dd] or [yyyyMMdd'T'HHmmss] on some devices

  DeviceVideo({this.videoName, this.path, this.size, this.duration, this.creationDate});

  factory DeviceVideo.fromMap(Map<dynamic, dynamic> json){
    json = Map<String, dynamic>.from(json);
    return DeviceVideo(
      videoName: json["name"],
      path: json["filePath"],
      size: json["size"],
      duration: json["duration"],
      creationDate: json["dateCreated"],
    );
  }
}