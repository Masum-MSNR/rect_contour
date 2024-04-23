import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:rect_contour/models/rect_point.dart';

import 'rect_contour_platform_interface.dart';

/// An implementation of [RectContourPlatform] that uses method channels.
class MethodChannelRectContour extends RectContourPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('rect_contour');

  @override
  Future<List<RectPoint>?> getPoints(Uint8List image) async {
    var json = await methodChannel.invokeMethod<String>('detect', image);
    if (json == null) {
      return null;
    }

    try {
      var points = <RectPoint>[];
      var decodedJson = jsonDecode(json);
      var tl = decodedJson["tl"];
      var tr = decodedJson["tr"];
      var br = decodedJson["br"];
      var bl = decodedJson["bl"];

      var tls = tl.replaceAll("{", "").replaceAll("}", "").split(", ");
      var trs = tr.replaceAll("{", "").replaceAll("}", "").split(", ");
      var brs = br.replaceAll("{", "").replaceAll("}", "").split(", ");
      var bls = bl.replaceAll("{", "").replaceAll("}", "").split(", ");
      points.add(RectPoint(double.parse(tls[0]), double.parse(tls[1])));
      points.add(RectPoint(double.parse(trs[0]), double.parse(trs[1])));
      points.add(RectPoint(double.parse(brs[0]), double.parse(brs[1])));
      points.add(RectPoint(double.parse(bls[0]), double.parse(bls[1])));
      return points;
    } catch (e) {
      return null;
    }
  }
}
