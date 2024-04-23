import 'dart:typed_data';

import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:rect_contour/models/rect_point.dart';

import 'rect_contour_method_channel.dart';

abstract class RectContourPlatform extends PlatformInterface {
  /// Constructs a RectContourPlatform.
  RectContourPlatform() : super(token: _token);

  static final Object _token = Object();

  static RectContourPlatform _instance = MethodChannelRectContour();

  /// The default instance of [RectContourPlatform] to use.
  ///
  /// Defaults to [MethodChannelRectContour].
  static RectContourPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [RectContourPlatform] when
  /// they register themselves.
  static set instance(RectContourPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<List<RectPoint>?> getPoints(Uint8List image) {
    throw UnimplementedError('getPoints() has not been implemented.');
  }
}
