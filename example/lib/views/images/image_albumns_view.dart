import 'dart:io';

import 'package:device_media/device_media.dart';
import 'package:device_media_example/views/images/images_folder_view.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class ImageAlbumnsView extends StatelessWidget {
  final List<ImageFolder> imageFolders;

  const ImageAlbumnsView({Key key, this.imageFolders}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Center(
      child: imageFolders == null
          ? CircularProgressIndicator()
          : GridView.count(childAspectRatio: .58, crossAxisCount: 3, children: [
              for (var folder in imageFolders)
                Wrap(
                  children: [
                    GestureDetector(
                      onTap: () {
                        Navigator.of(context).push(CupertinoPageRoute(
                            builder: ((context) => ImagesFolderView(
                                  folderName: folder.folderName,
                                  images: folder.images,
                                ))));
                      },
                      child: Container(
                          padding:
                              EdgeInsets.symmetric(vertical: 10, horizontal: 5),
                          height: 230,
                          width: 230,
                          child: Column(
                            children: [
                              Container(
                                height: 150,
                                width: 150,
                                decoration: BoxDecoration(
                                  color: Colors.grey[200],
                                  borderRadius: BorderRadius.circular(20),
                                  image: DecorationImage(
                                      image: FileImage(
                                          File(folder.images[0].imagePath)),
                                      fit: BoxFit.cover),
                                ),
                              ),
                              SizedBox(height: 10),
                              Container(
                                  width: 150,
                                  child: Text(folder.folderName,
                                      textAlign: TextAlign.center,
                                      overflow: TextOverflow.ellipsis,
                                      maxLines: 1,
                                      style: TextStyle(
                                          fontWeight: FontWeight.w600,
                                          fontSize: 14))),
                              SizedBox(height: 3),
                              Container(
                                  width: 150,
                                  child: Text('${folder.images.length}',
                                      textAlign: TextAlign.center,
                                      overflow: TextOverflow.ellipsis,
                                      maxLines: 1,
                                      style: TextStyle(
                                          fontSize: 12, color: Colors.black54)))
                            ],
                          )),
                    )
                  ],
                ),
            ]),
    );
  }
}
