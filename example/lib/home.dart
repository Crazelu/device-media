import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:device_media/device_media.dart';
import 'views/images/image_albums_view.dart';
import 'views/videos/video_list_view.dart';

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  List<ImageFolder> _imageFolders;
  List<DeviceVideo> _videos;
  PageController _pageController;
  int _selectedIndex;
  List<String> _pageNames = ["Albums", "Videos", "Music"];
  String _pageName;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    _selectedIndex = 0;
    _pageName = _pageNames[0];
    _pageController = PageController(initialPage: _selectedIndex);
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    List<ImageFolder> imageFolders;
    List<DeviceVideo> videos;
    // Platform messages may fail, so we use a try/catch PlatformException.

    try {
      await DeviceMedia.requestPermission(isImage: false);
      imageFolders = await DeviceMedia.getFoldersImages();
      videos = await DeviceMedia.getVideos();
    } on PlatformException {
      setState(() {
        _imageFolders = imageFolders ?? [];
        _videos = videos ?? [];
      });
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _imageFolders = imageFolders;
      _videos = videos;
    });
  }

  void goToPage(int index) {
    if (_selectedIndex != index) {
      setState(() {
        _selectedIndex = index;
        _pageName = _pageNames[index];
      });

      _pageController.animateToPage(index,
          duration: Duration(milliseconds: 200), curve: Curves.easeInCubic);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_pageName),
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: goToPage,
        items: [
          BottomNavigationBarItem(
              icon: Icon(Icons.photo_camera_back), label: 'Images'),
          BottomNavigationBarItem(
              icon: Icon(Icons.video_collection_rounded), label: 'Videos'),
          BottomNavigationBarItem(
              icon: Icon(Icons.audiotrack_rounded), label: 'Music'),
        ],
      ),
      body: PageView(
        controller: _pageController,
        onPageChanged: (int page) {
          setState(() {
            _selectedIndex = page;
            _pageName = _pageNames[page];
          });
        },
        children: [
          ImageAlbumnsView(imageFolders: _imageFolders),
          VideoListView(videos: _videos),
          Container()
        ],
      ),
    );
  }
}
