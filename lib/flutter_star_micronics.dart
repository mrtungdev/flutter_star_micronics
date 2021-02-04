import 'dart:async';

import 'package:flutter/services.dart';

enum StarMicronicsDiscoveryType { TCP, BLUETOOTH }

class FlutterStarMicronics {
  static const MethodChannel _channel =
      const MethodChannel('flutter_star_micronics');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> onDiscovery(
      {StarMicronicsDiscoveryType type =
          StarMicronicsDiscoveryType.TCP}) async {
    String printType = "tcp";
    if (type == StarMicronicsDiscoveryType.BLUETOOTH) {
      printType = "bluetooth";
    }
    final Map<String, dynamic> params = {"type": printType};
    final dynamic rep = await _channel.invokeMethod('discovery', params);
    return rep;
  }
}
