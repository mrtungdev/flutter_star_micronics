import 'dart:convert';
import 'dart:developer';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_star_micronics/flutter_star_micronics.dart';
import 'models.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<AppLogs> logs = [];
  List<StarMicronicsPrinter> printers = [];
  Map<String, bool> loading = {};

  @override
  void initState() {
    super.initState();
  }

  void setLoading(String type, {bool isLoading = true}) {
    setState(() {
      loading[type] = isLoading;
    });
  }

  bool isLoading(String type) {
    return loading[type] ?? false;
  }

  void onDiscoveryTCP() async {
    try {
      setLoading('DiscoveryTCP', isLoading: true);
      final data = await FlutterStarMicronics.onDiscovery(
          type: StarMicronicsDiscoveryType.TCP);
      print("onDiscoveryTCP Found ${data.length}");
      for (var item in data) {
        print(
            "  => Model: ${item.model} | Address: ${item.address} | PortName: ${item.portName}: Emulation: ${item.emulation}");
      }
      List<String> msg = data.map((item) {
        return "=> Model: ${item.model} | Address: ${item.address} | PortName: ${item.portName}: Emulation: ${item.emulation}";
      }).toList();
      setState(() {
        printers = data;

        AppLogs log = AppLogs(
            isObject: true,
            title: "Discovery Printer via TCP",
            message: msg.join('\n'));
        logs.insert(0, log);
      });
      setLoading('DiscoveryTCP', isLoading: false);
    } catch (e) {
      print(e.toString());
      setLoading('DiscoveryTCP', isLoading: false);
    }
  }

  void onPrintTest() async {
    // PrintTestTCP
    setLoading('PrintTestTCP', isLoading: true);
    StarMicronicsCommand startCmd = StarMicronicsCommand();
    StarMicronicsPrinter printer =
        printers != null && printers.length > 0 ? printers[0] : null;
    if (printer != null) {
      List<Map<String, dynamic>> commands = [];

      final cEncode = startCmd.appendEncoding(encoding: StarEncoding.UTF8);
      final codePage = startCmd.appendCodePage(StarCodePageType.UTF8);
      final cFontA = startCmd.appendFontStyle(StarFontStyleType.A);
      final cFontB = startCmd.appendFontStyle(StarFontStyleType.B);
      final cText = startCmd.append(
          "FONT A DATA ASKJH ASD K AKSD KASDH KASDH JKASDH KJASHD JKASDH JKASDHJKHD\n");
      final cText2 = startCmd.append("Font B data thử coi tiếng việt sao");
      final cMultiple1 = startCmd.appendMultiple(3, 4,
          data:
              "\nFONT A DATA ASKJH ASD K AKSD KASDH KASDH JKASDH KJASHD JKASDH JKASDHJKHD\n");

      final cAppendLineFeed = startCmd.appendLineFeed(line: 1);
      final receiptImagePath = "assets/images/receipt.png";
      // Image img = Image.asset(receiptImagePath);
      ByteData bytesData = await rootBundle.load(receiptImagePath);

      var buffer = bytesData.buffer;
      var m = base64.encode(Uint8List.view(buffer));

      final cBitMap = startCmd.appendBitmap(m,
          width: 576, bothScale: true, diffusion: true);
      final cCut = startCmd.appendCutPaper(StarCutPaperAction.PartialCut);

      commands.add(cEncode);
      commands.add(codePage);
      commands.add(cFontA);
      commands.add(cText);
      commands.add(cAppendLineFeed);
      commands.add(cFontB);
      commands.add(cText2);
      commands.add(cMultiple1);
      commands.add(cAppendLineFeed);
      // commands.add(cBitMap);
      commands.add(cCut);
      try {
        final rep = await FlutterStarMicronics.onPrint(printer, commands);
        setState(() {
          AppLogs log = AppLogs(
              isObject: true,
              title:
                  "Print Test: ${printer.model} - ${printer.address} - ${printer.portName}");
          log.message = rep['message'];
          logs.insert(0, log);
        });
        print("Rep $rep");
        setLoading('PrintTestTCP', isLoading: false);
      } catch (e) {
        print("Error ${e.toString()}");
        setLoading('PrintTestTCP', isLoading: false);
      }
    }
  }

  void onClearLogs() {
    setState(() {
      logs = [];
      loading = {};
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.deepOrange,
          title: const Text('Flutter Star Micronics'),
          actions: [
            IconButton(icon: Icon(Icons.clear_all), onPressed: onClearLogs)
          ],
        ),
        body: Center(
          child: Container(
            padding: EdgeInsets.all(10),
            // width: 400,
            child: CustomScrollView(
              slivers: [
                SliverList(
                  delegate: SliverChildListDelegate(
                    [
                      Row(
                        children: [
                          FlatButton(
                              color: Colors.deepOrange,
                              disabledColor: Colors.deepOrange[900],
                              child: isLoading('DiscoveryTCP')
                                  ? Container(
                                      height: 24,
                                      width: 24,
                                      child: CircularProgressIndicator(
                                        valueColor:
                                            new AlwaysStoppedAnimation<Color>(
                                                Colors.white),
                                        strokeWidth: 2,
                                      ),
                                    )
                                  : Text('Discovery TCP',
                                      style: TextStyle(color: Colors.white)),
                              onPressed: isLoading('DiscoveryTCP')
                                  ? null
                                  : onDiscoveryTCP),
                          SizedBox(width: 16),
                          FlatButton(
                              color: Colors.deepOrange,
                              disabledColor: Colors.deepOrange[900],
                              child: isLoading('PrintTestTCP')
                                  ? Container(
                                      height: 24,
                                      width: 24,
                                      child: CircularProgressIndicator(
                                        valueColor:
                                            new AlwaysStoppedAnimation<Color>(
                                                Colors.white),
                                        strokeWidth: 2,
                                      ),
                                    )
                                  : Text('Print test TCP (the first TCP found)',
                                      style: TextStyle(color: Colors.white)),
                              onPressed: isLoading('PrintTestTCP')
                                  ? null
                                  : onPrintTest),
                        ],
                      ),
                      SizedBox(height: 16),
                      Text('Logs',
                          style:
                              TextStyle(fontSize: 24, color: Colors.black38)),
                      ListView.separated(
                        itemCount: logs?.length ?? 0,
                        primary: false,
                        shrinkWrap: true,
                        separatorBuilder: (context, index) {
                          return Divider();
                        },
                        itemBuilder: (BuildContext context, int index) {
                          AppLogs logItem = logs[index];
                          Widget itemTitle;
                          Widget itemMessage;
                          if (logItem?.title != null) {
                            itemTitle = Text('${logItem.title}',
                                style: TextStyle(
                                    fontWeight: FontWeight.w600, fontSize: 15));
                          }
                          if (logItem?.message != null) {
                            itemMessage = Padding(
                              padding: const EdgeInsets.only(left: 16, top: 4),
                              child: Text('${logItem?.message}'),
                            );
                          }
                          return ListTile(
                              title: itemTitle,
                              subtitle: itemMessage,
                              contentPadding: EdgeInsets.all(0));
                        },
                      )
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
