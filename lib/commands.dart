import 'enums.dart';

class StarMicronicsCommand {
  String _enumText(dynamic enumName) {
    List<String> ns = enumName.toString().split('.');
    if (ns != null && ns.length > 0) {
      return ns.last;
    }
    return enumName.toString();
  }

  Map<String, dynamic> appendEncoding({StarEncoding encoding}) {
    return {
      "id": "appendEncoding",
      "value": _enumText(encoding ?? StarEncoding.UTF8)
    };
  }

  Map<String, dynamic> append(dynamic data) {
    return {"id": "append", "value": data};
  }

  Map<String, dynamic> appendRaw(dynamic data) {
    return {"id": "appendRaw", "value": data};
  }

  Map<String, dynamic> appendFontStyle(StarFontStyleType font) {
    return {"id": "appendFontStyle", "value": _enumText(font)};
  }

  Map<String, dynamic> appendCodePage(StarCodePageType em) {
    return {"id": "appendCodePage", "value": _enumText(em)};
  }

  Map<String, dynamic> appendInternational(StarInternationalType em) {
    return {"id": "appendInternational", "value": _enumText(em)};
  }

  /// appendLineFeed
  /// line: Units(Lines)
  Map<String, dynamic> appendLineFeed({dynamic bytesArray, int line}) {
    Map<String, dynamic> cmd = {
      "id": "appendLineFeed",
    };
    if (bytesArray != null) {
      cmd['bytesArray'] = bytesArray;
    }
    if (line != null) {
      cmd['line'] = line;
    }
    return cmd;
  }

  /// appendUnitFeed
  /// unit: Units(Dots)
  Map<String, dynamic> appendUnitFeed({dynamic bytesArray, int unit}) {
    Map<String, dynamic> cmd = {
      "id": "appendUnitFeed",
    };
    if (bytesArray != null) {
      cmd['bytesArray'] = bytesArray;
    }
    if (unit != null) {
      cmd['unit'] = unit;
    }
    return cmd;
  }

  /// appendCharacterSpace
  /// Set command of the character space is generated and added to the command buffer.
  ///
  /// Important: In Japanese, Simplified Chinese, Traditional Chinese, and Korean (DBCS), the character space is to be twice.
  ///
  /// space: Units (Dots)
  Map<String, dynamic> appendCharacterSpace({int space}) {
    return {"id": "appendCharacterSpace", "value": space ?? 0};
  }

  /// appendLineSpace
  ///
  /// lineSpace: Units : Dots
  Map<String, dynamic> appendLineSpace({int lineSpace}) {
    return {"id": "appendLineSpace", "value": lineSpace ?? 0};
  }

  Map<String, dynamic> appendEmphasis(bool isEmphasis) {
    return {"id": "appendEmphasis", "value": isEmphasis ?? true};
  }

  Map<String, dynamic> appendInvert(bool isInvert) {
    return {"id": "appendInvert", "value": isInvert ?? true};
  }

  Map<String, dynamic> appendMultiple(int width, int height, {dynamic data}) {
    Map<String, dynamic> cmd = {
      "id": "appendMultiple",
      "width": width,
      "height": height
    };
    if (data != null) {
      cmd["value"] = data;
    }
    return cmd;
  }

  Map<String, dynamic> appendUnderLine(bool isUnderLine) {
    return {"id": "appendUnderLine", "value": isUnderLine ?? true};
  }

  /// appendAbsolutePosition
  /// Absolute position command is generated and added to the command buffer.
  ///
  /// position: Units (Dots)
  Map<String, dynamic> appendAbsolutePosition({int position}) {
    return {"id": "appendAbsolutePosition", "value": position ?? 0};
  }

  /// appendAlignment
  /// Alignment position constants
  Map<String, dynamic> appendAlignment(StarAlignmentPosition align) {
    return {
      "id": "appendAlignment",
      "value": align != null
          ? _enumText(align)
          : _enumText(StarAlignmentPosition.Left)
    };
  }

  /// appendHorizontalTabPosition
  /// Alignment position constants
  Map<String, dynamic> appendHorizontalTabPosition(List<int> ints) {
    return {"id": "appendHorizontalTabPosition", "value": ints};
  }

  Map<String, dynamic> appendCutPaper(StarCutPaperAction cutPaper) {
    return {"id": "appendCutPaper", "value": _enumText(cutPaper)};
  }

  /// appendHorizontalTabPosition
  /// Cash drawer command is generated and added to the command buffer.
  Map<String, dynamic> appendPeripheral(
      StarPeripheralChannel channel, int time) {
    Map<String, dynamic> cmd = {
      "id": "appendPeripheral",
      "value": _enumText(channel)
    };
    if (time != null) {
      cmd['time'] = time;
    }
    return cmd;
  }

  Map<String, dynamic> appendSound(StarSoundChannel channel,
      {int repeat, int driveTime, int delayTime}) {
    Map<String, dynamic> cmd = {
      "id": "appendSound",
      "value": _enumText(channel)
    };
    if (repeat != null) {
      cmd['repeat'] = repeat;
    }
    if (driveTime != null) {
      cmd['driveTime'] = driveTime;
    }
    if (delayTime != null) {
      cmd['delayTime'] = delayTime;
    }
    return cmd;
  }

  Map<String, dynamic> appendBarcode(dynamic data,
      {StarBarcodeSymbology symbology,
      StarBarcodeWidth width,
      int height,
      bool hri}) {
    Map<String, dynamic> cmd = {"id": "appendBarcode", "value": data};
    if (symbology != null) {
      cmd['symbology'] = _enumText(symbology);
    }
    if (width != null) {
      cmd['width'] = width;
    }
    if (height != null) {
      cmd['height'] = height;
    }
    if (hri != null) {
      cmd['hri'] = hri;
    }
    return cmd;
  }

  Map<String, dynamic> appendBitmap(dynamic data,
      {bool diffusion,
      int width,
      bool bothScale,
      StarBitmapConverterRotation rotation}) {
    Map<String, dynamic> cmd = {"id": "appendBitmap", "value": data};
    if (rotation != null) {
      cmd['rotation'] = _enumText(rotation);
    }
    if (diffusion != null) {
      cmd['diffusion'] = diffusion;
    }
    if (bothScale != null) {
      cmd['bothScale'] = bothScale;
    }
    return cmd;
  }

  Map<String, dynamic> appendBitmapWithAlignment(dynamic data,
      {bool diffusion,
      int width,
      bool bothScale,
      StarAlignmentPosition position}) {
    Map<String, dynamic> cmd = {
      "id": "appendBitmapWithAlignment",
      "value": data
    };
    if (position != null) {
      cmd['position'] = _enumText(position);
    }
    if (diffusion != null) {
      cmd['diffusion'] = diffusion;
    }
    if (bothScale != null) {
      cmd['bothScale'] = bothScale;
    }
    return cmd;
  }

  Map<String, dynamic> appendBitmapWithAbsolutePosition(dynamic data,
      {bool diffusion,
      int width,
      bool bothScale,
      int position,
      StarBitmapConverterRotation rotation}) {
    Map<String, dynamic> cmd = {
      "id": "appendBitmapWithAbsolutePosition",
      "value": data
    };
    if (rotation != null) {
      cmd['rotation'] = _enumText(rotation);
    }
    if (diffusion != null) {
      cmd['diffusion'] = diffusion;
    }
    if (bothScale != null) {
      cmd['bothScale'] = bothScale;
    }
    if (position != null) {
      cmd['position'] = position;
    }
    return cmd;
  }

  Map<String, dynamic> appendPrintableArea(StarPrintableAreaType enu) {
    return {
      "id": "appendPrintableArea",
      "value": enu ?? _enumText(StarPrintableAreaType.Standard)
    };
  }
}
