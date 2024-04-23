import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'rect_contour_platform_interface.dart';

/// An implementation of [RectContourPlatform] that uses method channels.
class MethodChannelRectContour extends RectContourPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('rect_contour');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
