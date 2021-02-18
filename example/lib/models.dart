import 'dart:convert';

class AppLogs {
  AppLogs({
    this.title,
    this.message,
    this.isObject,
    this.type,
  });

  String title;
  String message;
  bool isObject;
  String type;

  factory AppLogs.fromRawJson(String str) => AppLogs.fromJson(json.decode(str));

  String toRawJson() => json.encode(toJson());

  factory AppLogs.fromJson(Map<String, dynamic> json) => AppLogs(
        title: json["title"] == null ? null : json["title"],
        message: json["message"] == null ? null : json["message"],
        isObject: json["isObject"] == null ? null : json["isObject"],
        type: json["type"] == null ? null : json["type"],
      );

  Map<String, dynamic> toJson() => {
        "title": title == null ? null : title,
        "message": message == null ? null : message,
        "isObject": isObject == null ? null : isObject,
        "type": type == null ? null : type,
      };
}
