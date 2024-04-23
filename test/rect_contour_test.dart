import 'package:flutter_test/flutter_test.dart';
import 'package:rect_contour/rect_contour.dart';
import 'package:rect_contour/rect_contour_platform_interface.dart';
import 'package:rect_contour/rect_contour_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockRectContourPlatform
    with MockPlatformInterfaceMixin
    implements RectContourPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final RectContourPlatform initialPlatform = RectContourPlatform.instance;

  test('$MethodChannelRectContour is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelRectContour>());
  });

  test('getPlatformVersion', () async {
    RectContour rectContourPlugin = RectContour();
    MockRectContourPlatform fakePlatform = MockRectContourPlatform();
    RectContourPlatform.instance = fakePlatform;

    expect(await rectContourPlugin.getPlatformVersion(), '42');
  });
}
