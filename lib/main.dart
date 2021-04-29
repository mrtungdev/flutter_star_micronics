import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

import 'enums.dart';
import 'models.dart';
import 'ultilities.dart';

class FlutterStarMicronics {
  static const MethodChannel _channel =
      const MethodChannel('flutter_star_micronics');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<List<StarMicronicsPrinter>> onDiscovery(
      {StarMicronicsDiscoveryType type =
          StarMicronicsDiscoveryType.TCP}) async {
    if (!Platform.isAndroid) {
      throw 'Device not supported';
    }
    List<StarMicronicsPrinter> printers = [];
    String printType = "tcp";
    if (type == StarMicronicsDiscoveryType.BLUETOOTH) {
      printType = "bluetooth";
    }
    final Map<String, dynamic> params = {"type": printType};
    final rep = await _channel.invokeMethod('discovery', params);
    if (rep != null) {
      try {
        final response = StarMicronicsResponse.fromRawJson(rep);
        List<dynamic> prs = response?.content;
        if (prs != null && prs.length > 0) {
          prs.forEach((pr) {
            String modelName = pr['model'];
            StarMicronicsPrinter printer = StarMicronicsPrinter(
                address: pr['address'], portName: pr['portName']);
            printer.model = modelName;
            if (modelName != null && modelName != '') {
              final em =
                  StarMicronicsUltilities.detectEmulation(modelName: modelName);
              printer.emulation = em?.emulation;
            }
            printers.add(printer);
          });
        }
        return printers;
      } catch (e) {
        throw e;
      }
    }
    return [];
  }

  static Future<StarMicronicsResponse> onPrint(
      StarMicronicsPrinter printer, List<Map<String, dynamic>> commands) async {
    final Map<String, dynamic> params = {
      "address": printer.address,
      "portName": printer.portName,
      "emulation": printer.emulation,
      "commands": commands
    };
    final rep = await _channel.invokeMethod('print', params);
    if (rep != null) {
      try {
        final response = StarMicronicsResponse.fromRawJson(rep);
        return response;
      } catch (e) {
        throw e;
      }
    }
    return null;
  }
}
