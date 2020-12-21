import 'package:device_media/device_media.dart';
import 'package:flutter/cupertino.dart';
import "package:flutter/material.dart";
import 'dart:io';

import 'image_view.dart';

class ImagesFolderView extends StatelessWidget {
  final String folderName;
  final List<DeviceImage> images;

  const ImagesFolderView({Key key, this.folderName, this.images}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title:Text(folderName)),
        body:Center(
          child: GridView.count(
              childAspectRatio: .75,
              crossAxisCount: 4,
              children:[
                  for(var image in images)
                    Wrap(
                      children: [
                        GestureDetector(
                          onTap: (){
                            Navigator.of(context).push(CupertinoPageRoute(builder: ((context)=>
                                ImageView(images:images, pageIndex: images.indexOf(image),))));
                          },
                          child: Container(
                              margin: EdgeInsets.symmetric(vertical: 1, horizontal: 1),
                              height:120,
                              width:120,
                              decoration: BoxDecoration(
                                color:Colors.grey[200],
                                borderRadius: BorderRadius.circular(5),
                                image: DecorationImage(
                                  image: FileImage(File(image.imagePath)),
                                  fit: BoxFit.cover),
                              ),

                        ))],),

              ]),
        ));
  }
}
