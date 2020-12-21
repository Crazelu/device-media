import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:device_media/device_media.dart';

void main() {
  const MethodChannel channel = MethodChannel('device_media');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await DeviceMedia.platformVersion, '42');
  });
}
