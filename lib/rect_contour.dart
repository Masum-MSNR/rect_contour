
import 'rect_contour_platform_interface.dart';

class RectContour {
  Future<String?> getPlatformVersion() {
    return RectContourPlatform.instance.getPlatformVersion();
  }
}
