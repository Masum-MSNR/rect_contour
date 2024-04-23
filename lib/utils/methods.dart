import 'dart:io';

import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

class Methods {
  // pathToUInt8List(String path) async {
  //   final byteData = await rootBundle.load('assets/$path');
  //
  //   final file = File('${(await getTemporaryDirectory()).path}/$path');
  //   await file.create(recursive: true);
  //   await file.writeAsBytes(byteData.buffer
  //       .asUint8List(byteData.offsetInBytes, byteData.lengthInBytes));
  //   return await file.readAsBytes();
  // }

  static Future<Uint8List> assetToUInt8List(String path) async {
    final byteData = await rootBundle.load(path);

    final file = File('${(await getTemporaryDirectory()).path}/$path');
    await file.create(recursive: true);
    await file.writeAsBytes(byteData.buffer
        .asUint8List(byteData.offsetInBytes, byteData.lengthInBytes));
    return await file.readAsBytes();
  }
}
