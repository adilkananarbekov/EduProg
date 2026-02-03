import 'package:flutter_test/flutter_test.dart';
import 'package:eduprog_mobile/app.dart';

void main() {
  testWidgets('EduOps app smoke test', (WidgetTester tester) async {
    // Build our app and trigger a frame
    await tester.pumpWidget(const EduOpsApp());
    await tester.pumpAndSettle();

    // Verify app loads (splash screen should show EduOps text)
    expect(find.text('EduOps'), findsWidgets);
  });
}
