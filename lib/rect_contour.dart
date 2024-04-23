import 'dart:typed_data';

import 'package:rect_contour/models/rect_point.dart';

import 'rect_contour_platform_interface.dart';

class RectContour {
  Future<List<RectPoint>?> getPoints(Uint8List image) {
    return RectContourPlatform.instance.getPoints(image);
  }
}
