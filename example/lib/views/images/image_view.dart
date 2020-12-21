import 'package:device_media/device_media.dart';
import "package:flutter/material.dart";
import "dart:io";

class ImageView extends StatefulWidget {
  final int pageIndex;
  final List<DeviceImage> images;

  const ImageView({Key key, this.pageIndex, this.images}) : super(key: key);

  @override
  _ImageViewState createState() => _ImageViewState();
}

class _ImageViewState extends State<ImageView> {
  PageController _pageController;

  @override
  void initState() {
    _pageController = PageController(initialPage: widget.pageIndex);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    return PageView(
      controller: _pageController,
      children: [
        for(var image in widget.images)
          Container(
            height: size.height,
            width: size.width,
            decoration: BoxDecoration(image:
            DecorationImage(
                image: FileImage(File(image.imagePath)),
                fit: BoxFit.contain),
            ),
          )
      ],
    );
  }
}
