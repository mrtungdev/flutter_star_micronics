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
    this.type,
    this.success,
    this.message,
    this.content,
  });

  String type;
  bool success;
  String message;
  dynamic content;

  StarMicronicsResponse copyWith({
    String type,
    bool success,
    String message,
    dynamic content,
  }) =>
      StarMicronicsResponse(
        type: type ?? this.type,
        success: success ?? this.success,
        message: message ?? this.message,
        content: content ?? this.content,
      );

  factory StarMicronicsResponse.fromRawJson(String str) =>
      StarMicronicsResponse.fromJson(json.decode(str));

  String toRawJson() => json.encode(toJson());

  factory StarMicronicsResponse.fromJson(Map<String, dynamic> json) =>
      StarMicronicsResponse(
        type: json["type"] == null ? null : json["type"],
        success: json["success"] == null ? null : json["success"],
        message: json["message"] == null ? null : json["message"],
        content: json["content"] == null ? null : json["content"],
      );

  Map<String, dynamic> toJson() => {
        "type": type == null ? null : type,
        "success": success == null ? null : success,
        "message": message == null ? null : message,
        "content": content == null ? null : content,
      };
}
