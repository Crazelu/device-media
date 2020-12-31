import 'package:device_media/device_media.dart';
import 'package:device_media_example/views/videos/video_view.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class VideoListView extends StatelessWidget {
  final List<DeviceVideo> videos;

  const VideoListView({Key key, this.videos}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    final Size size = MediaQuery.of(context).size;
    return videos == null
        ? Center(child: CircularProgressIndicator())
        : Container(
            height: size.height,
            width: size.width,
            child: ListView(
              children: [
                for (var video in videos)
                  GestureDetector(
                      onTap: () {
                        Navigator.of(context).push(CupertinoPageRoute(
                            builder: ((context) => VideoView(
                                  path: video.path,
                                ))));
                      },
                      child: Text(video.videoName))
              ],
            ),
          );
  }
}
