import 'package:flutter/material.dart';
import 'package:flutter_star_micronics/enums.dart';
import 'package:flutter_star_micronics/flutter_star_micronics.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  void onDiscoveryTCP() async {
    try {
      final d = await FlutterStarMicronics.onDiscovery(
          type: StarMicronicsDiscoveryType.TCP);
      print("onDiscoveryTCP $d");
    } catch (e) {
      print(e.toString());
    }
  }

  void onShowLogs() {}

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter Star Micronics'),
          actions: [
            IconButton(icon: Icon(Icons.bug_report), onPressed: onShowLogs)
          ],
        ),
        body: Container(
          padding: const EdgeInsets.all(20),
          child: Center(
            child: Column(children: [
              FlatButton(
                  color: Colors.orange,
                  child: Text('Discovery TCP'),
                  onPressed: onDiscoveryTCP)
            ]),
          ),
        ),
      ),
    );
  }
}
