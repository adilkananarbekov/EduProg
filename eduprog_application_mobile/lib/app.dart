/// EduOps - App Widget
library;

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'core/theme/app_theme.dart';
import 'core/router/app_router.dart';
import 'providers/auth_provider.dart';

class EduOpsApp extends StatefulWidget {
  const EduOpsApp({super.key});

  @override
  State<EduOpsApp> createState() => _EduOpsAppState();
}

class _EduOpsAppState extends State<EduOpsApp> {
  late final GoRouter _router;

  @override
  void initState() {
    super.initState();
    // Create router once, don't recreate on every state change
    final authProvider = context.read<AuthProvider>();
    _router = AppRouter.createRouter(authProvider);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'EduOps',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      routerConfig: _router,
    );
  }
}
