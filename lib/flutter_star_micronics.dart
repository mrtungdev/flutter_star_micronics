
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterStarMicronics {
  static const MethodChannel _channel =
      const MethodChannel('flutter_star_micronics');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
