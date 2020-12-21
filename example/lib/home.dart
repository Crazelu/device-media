import 'package:device_media_example/views/images/images_folder_view.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:device_media/device_media.dart';

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  List<ImageFolder> _imageFolders;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    List<ImageFolder> imageFolders;
    // Platform messages may fail, so we use a try/catch PlatformException.

    try {
      await DeviceMedia.requestPermission();
      imageFolders =  await DeviceMedia.getFoldersImages();
    } on PlatformException {
      setState(() {
        _imageFolders =imageFolders?? [];
      });
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _imageFolders = imageFolders;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Flutter Media'),
      ),
      bottomNavigationBar: BottomNavigationBar(items: [
        BottomNavigationBarItem(icon: Icon(Icons.photo_camera_back),
            label: 'Images'),
        BottomNavigationBarItem(icon: Icon(Icons.video_collection_rounded),
            label: 'Videos'),
        BottomNavigationBarItem(icon: Icon(Icons.audiotrack_rounded),
            label: 'Music'),
      ],),
      body: Center(
        child:_imageFolders == null? CircularProgressIndicator() :GridView.count(
            childAspectRatio: .58,
            crossAxisCount: 3,
            children:[
                for(var folder in _imageFolders)
                  Wrap(
                    children: [
                      GestureDetector(
                        onTap: (){
                          Navigator.of(context).push(CupertinoPageRoute(builder: ((context)=>
                              ImagesFolderView(folderName: folder.folderName,images: folder.images,))));
                        },
                        child: Container(
                            padding: EdgeInsets.symmetric(vertical: 10, horizontal: 5),
                            height:230,
                            width:230,
                            child: Column(
                              children: [
                              Container(
                                height:150,
                                width:150,
                                decoration: BoxDecoration(
                                  color: Colors.grey[200],
                                  borderRadius: BorderRadius.circular(20),
                                  image: DecorationImage(
                                      image: FileImage(File(folder.images[0].imagePath)),
                                      fit: BoxFit.cover),
                                ),
                              ),
                              SizedBox(height:10),
                              Container(
                                  width:150,
                                  child:Text(
                                      folder.folderName,
                                      textAlign: TextAlign.center,
                                      overflow: TextOverflow.ellipsis,
                                      maxLines: 1,
                                      style:TextStyle(fontWeight:FontWeight.w600, fontSize:14))),
                                SizedBox(height:3),
                                Container(
                                    width:150,
                                    child:Text(
                                        '${folder.images.length}',
                                        textAlign: TextAlign.center,
                                        overflow: TextOverflow.ellipsis,
                                        maxLines: 1,
                                        style:TextStyle(fontSize:12, color:Colors.black54)))
                            ],)),
                      )],),

            ]),
      ),
    );
  }
}
