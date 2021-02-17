import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter_star_micronics/flutter_star_micronics.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<StarMicronicsPrinter> printers = [];

  @override
  void initState() {
    super.initState();
  }

  void onDiscoveryTCP() async {
    try {
      final data = await FlutterStarMicronics.onDiscovery(
          type: StarMicronicsDiscoveryType.TCP);
      print("onDiscoveryTCP Found ${data.length}");
      for (var item in data) {
        print(
            "  => Model: ${item.model} | Address: ${item.address} | PortName: ${item.portName}: Emulation: ${item.emulation}");
      }
      setState(() {
        printers = data;
      });
    } catch (e) {
      print(e.toString());
    }
  }

  void onPrintTest() async {
    StarMicronicsCommand startCmd = StarMicronicsCommand();
    StarMicronicsPrinter printer =
        printers != null && printers.length > 0 ? printers[0] : null;
    if (printer != null) {
      List<Map<String, dynamic>> commands = [];

      final cEncode = startCmd.appendEncoding(StarEncoding.UTF8);
      final cText = startCmd.append("DATA");
      final cMultiple1 =
          startCmd.appendMultiple('sadấd', width: 100, height: 20);

      commands.add(cEncode);
      commands.add(cText);
      commands.add(cMultiple1);

      log(commands.toString());
      final rep = await FlutterStarMicronics.onPrint(printer, commands);
      print("Rep $rep");
    }
  }

  void onShowLogs() {}

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.deepOrange,
          title: const Text('Flutter Star Micronics'),
          actions: [
            IconButton(icon: Icon(Icons.bug_report), onPressed: onShowLogs)
          ],
        ),
        body: Center(
          child: Container(
            padding: EdgeInsets.all(30),
            width: 400,
            child: CustomScrollView(
              slivers: [
                SliverList(
                  delegate: SliverChildListDelegate(
                    [
                      FlatButton(
                          color: Colors.deepOrange,
                          child: Text('Discovery TCP',
                              style: TextStyle(color: Colors.white)),
                          onPressed: onDiscoveryTCP),
                      FlatButton(
                          color: Colors.deepOrange,
                          child: Text('Print Test',
                              style: TextStyle(color: Colors.white)),
                          onPressed: onPrintTest),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
