import 'dart:io';
import 'package:device_media/device_media.dart';
import 'package:device_media_example/views/videos/controls_overlay.dart';
import 'package:flutter/material.dart';
import 'package:video_player/video_player.dart';

class VideoView extends StatefulWidget {
  final DeviceVideo video;

  const VideoView({Key key, this.video}) : super(key: key);
  @override
  _VideoViewState createState() => _VideoViewState();
}

class _VideoViewState extends State<VideoView> {
  VideoPlayerController _controller;

  DeviceVideo get video => widget.video;

  @override
  void initState() {
    super.initState();
    _controller = VideoPlayerController.file(File(video.path));
    _controller.addListener(() {
      setState(() {});
    });
    _controller.setLooping(true);
    _controller.initialize().then((_) => setState(() {}));
    _controller.play();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Container(
        child: AspectRatio(
          aspectRatio:
              (video.width / video.height) ?? _controller.value.aspectRatio,
          child: Stack(
            alignment: Alignment.bottomCenter,
            children: <Widget>[
              VideoPlayer(_controller),
              ClosedCaption(text: _controller.value.caption.text),
              ControlsOverlay(controller: _controller),
              VideoProgressIndicator(_controller, allowScrubbing: true),
            ],
          ),
        ),
      ),
    );
  }
}
