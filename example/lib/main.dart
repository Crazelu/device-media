
import 'package:device_media_example/home.dart';
import 'package:flutter/material.dart';


void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        appBarTheme: AppBarTheme(
          elevation:0,
          color: Colors.white,
          iconTheme: IconThemeData(color: Colors.blue),
          textTheme: TextTheme(
            headline6: TextStyle(fontSize: 20, color:Colors.black, fontWeight: FontWeight.w500)
          )
        )
      ),
      home: Home()
    );
  }
}
