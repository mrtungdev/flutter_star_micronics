import 'dart:convert';

class StarMicronicsModel {
  String name;
  String emulation;
  List<String> models;
  StarMicronicsModel({this.name, this.emulation, this.models});
}

/*
 * Star Micronics Printer Model
 */
class StarMicronicsPrinter {
  StarMicronicsPrinter(
      {this.address, this.portName, this.model, this.emulation});

  String address;
  String portName;
  String model;
  String emulation;

  StarMicronicsPrinter copyWith(
          {String address, String portName, String model, String emulation}) =>
      StarMicronicsPrinter(
        address: address ?? this.address,
        portName: portName ?? this.portName,
        model: model ?? this.model,
        emulation: emulation ?? this.emulation,
      );

  factory StarMicronicsPrinter.fromRawJson(String str) =>
      StarMicronicsPrinter.fromJson(json.decode(str));

  String toRawJson() => json.encode(toJson());

  factory StarMicronicsPrinter.fromJson(Map<String, dynamic> json) =>
      StarMicronicsPrinter(
        address: json["address"] == null ? null : json["address"],
        portName: json["portName"] == null ? null : json["portName"],
        model: json["model"] == null ? null : json["model"],
        emulation: json["emulation"] == null ? null : json["emulation"],
      );

  Map<String, dynamic> toJson() => {
        "address": address == null ? null : address,
        "portName": portName == null ? null : portName,
        "model": model == null ? null : model,
        "emulation": emulation == null ? null : emulation,
      };
}

class StarMicronicsResponse {
  StarMicronicsResponse({
    this.coverOpen,
    this.cutterError,
    this.message,
    this.offline,
    this.overTemp,
    this.receiptPaperEmpty,
    this.success,
    this.type,
    this.errorMessage,
    this.content,
    this.infoMessage,
  });

  final bool coverOpen;
  final bool cutterError;
  final String message;
  final bool offline;
  final bool overTemp;
  final bool receiptPaperEmpty;
  final bool success;
  final String type;
  final String errorMessage;
  final dynamic content;
  final String infoMessage;

  StarMicronicsResponse copyWith({
    bool coverOpen,
    bool cutterError,
    String message,
    bool offline,
    bool overTemp,
    bool receiptPaperEmpty,
    bool success,
    String type,
    String errorMessage,
    dynamic content,
    String infoMessage,
  }) =>
      StarMicronicsResponse(
        coverOpen: coverOpen ?? this.coverOpen,
        cutterError: cutterError ?? this.cutterError,
        message: message ?? this.message,
        offline: offline ?? this.offline,
        overTemp: overTemp ?? this.overTemp,
        receiptPaperEmpty: receiptPaperEmpty ?? this.receiptPaperEmpty,
        success: success ?? this.success,
        type: type ?? this.type,
        errorMessage: errorMessage ?? this.errorMessage,
        content: content ?? this.content,
        infoMessage: infoMessage ?? this.infoMessage,
      );

  factory StarMicronicsResponse.fromRawJson(String str) =>
      StarMicronicsResponse.fromMap(json.decode(str));

  String toJson() => json.encode(toMap());

  factory StarMicronicsResponse.fromMap(Map<String, dynamic> json) =>
      StarMicronicsResponse(
        coverOpen: json["coverOpen"] == null ? null : json["coverOpen"],
        cutterError: json["cutterError"] == null ? null : json["cutterError"],
        message: json["message"] == null ? null : json["message"],
        offline: json["offline"] == null ? null : json["offline"],
        overTemp: json["overTemp"] == null ? null : json["overTemp"],
        receiptPaperEmpty: json["receiptPaperEmpty"] == null
            ? null
            : json["receiptPaperEmpty"],
        success: json["success"] == null ? null : json["success"],
        type: json["type"] == null ? null : json["type"],
        errorMessage:
            json["errorMessage"] == null ? null : json["errorMessage"],
        content: json["content"] == null ? null : json["content"],
        infoMessage: json["infoMessage"] == null ? null : json["infoMessage"],
      );

  Map<String, dynamic> toMap() => {
        "coverOpen": coverOpen == null ? null : coverOpen,
        "cutterError": cutterError == null ? null : cutterError,
        "message": message == null ? null : message,
        "offline": offline == null ? null : offline,
        "overTemp": overTemp == null ? null : overTemp,
        "receiptPaperEmpty":
            receiptPaperEmpty == null ? null : receiptPaperEmpty,
        "success": success == null ? null : success,
        "type": type == null ? null : type,
        "errorMessage": errorMessage == null ? null : errorMessage,
        "content": content == null ? null : content.toMap(),
        "infoMessage": infoMessage == null ? null : infoMessage,
      };
}
