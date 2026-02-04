import 'package:flutter_test/flutter_test.dart';
import 'package:eduprog_mobile/app.dart';
import 'package:eduprog_mobile/providers/auth_provider.dart';
import 'package:eduprog_mobile/services/auth_service.dart';
import 'package:eduprog_mobile/core/network/api_client.dart';
import 'package:provider/provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  const MethodChannel channel = MethodChannel('plugins.it_nomads.com/flutter_secure_storage');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return null;
      },
    );
  });

  testWidgets('EduOps app smoke test', (WidgetTester tester) async {
    // Initialize dependencies
    final apiClient = ApiClient();
    final authService = AuthService(apiClient);
    final authProvider = AuthProvider(authService, apiClient);

    // Build our app and trigger a frame
    await tester.pumpWidget(
      MultiProvider(
        providers: [
          ChangeNotifierProvider<AuthProvider>.value(value: authProvider),
        ],
        child: const EduOpsApp(),
      ),
    );

    // Trigger initial frame to show SplashScreen
    await tester.pump();

    // Verify splash screen shows EduOps text
    expect(find.text('EduOps'), findsWidgets);

    // Advance time to allow SplashScreen timer (2 seconds) to complete
    // and subsequent auth check to run (which uses the mocked channel)
    await tester.pump(const Duration(seconds: 3));

    // Allow navigation to complete (Splash -> Login)
    await tester.pumpAndSettle();

    // At this point we should be on LoginScreen
    expect(find.text('Welcome Back'), findsWidgets);
  });
}
