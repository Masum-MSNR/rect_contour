import 'dart:typed_data';

import 'package:rect_contour/models/rect_point.dart';
import 'package:rect_contour/utils/methods.dart';

import 'rect_contour_platform_interface.dart';

class RectContour {
  Future<List<RectPoint>?> getPoints(Uint8List image) async {
    return await RectContourPlatform.instance.getPoints(image);
  }

  Future<List<RectPoint>?> getPointsAsset(String path) async {
    return await RectContourPlatform.instance.getPoints(
      await Methods.assetToUInt8List(path),
    );
  }
}
