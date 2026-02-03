/// EduOps - Splash Screen with Alatoo Logo
library;

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../../core/constants/colors.dart';
import '../../providers/auth_provider.dart';
import '../../models/user.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;
  late Animation<double> _scaleAnimation;
  String _statusMessage = 'Loading...';
  String _debugInfo = '';

  @override
  void initState() {
    super.initState();

    _animationController = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    );

    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _animationController,
        curve: const Interval(0.0, 0.6, curve: Curves.easeOut),
      ),
    );

    _scaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(
        parent: _animationController,
        curve: const Interval(0.0, 0.6, curve: Curves.easeOutBack),
      ),
    );

    _animationController.forward();

    // Use addPostFrameCallback to ensure context is ready
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _checkAuthAndNavigate();
    });
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  Future<void> _checkAuthAndNavigate() async {
    debugPrint('SplashScreen: Starting auth check...');
    _updateStatus('Checking authentication...');

    // Wait for animation to play
    await Future.delayed(const Duration(milliseconds: 2000));

    if (!mounted) {
      debugPrint('SplashScreen: Widget not mounted, aborting');
      return;
    }

    final authProvider = context.read<AuthProvider>();
    bool timedOut = false;

    try {
      _updateStatus('Connecting to backend...');
      // Add timeout to prevent infinite loading
      await authProvider.checkAuthStatus().timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          debugPrint('SplashScreen: Auth check timed out');
          _updateStatus('Backend connection timeout - proceeding to login');
          timedOut = true;
          throw TimeoutException('Auth check timed out');
        },
      );
      _updateStatus('Auth check complete');
    } on TimeoutException catch (e) {
      debugPrint('SplashScreen: Timeout: $e');
      _updateStatus('Connection timeout');
      // Wait briefly then proceed to login
      await Future.delayed(const Duration(seconds: 1));
    } catch (e) {
      debugPrint('SplashScreen: Auth check error: $e');
      _updateStatus('Error: $e');
      // Show error for 2 seconds before navigating to login
      await Future.delayed(const Duration(seconds: 2));
    }

    if (!mounted) {
      debugPrint('SplashScreen: Widget not mounted after auth check');
      return;
    }

    // If timed out, go directly to login
    if (timedOut) {
      debugPrint('SplashScreen: Timed out, going to /login');
      if (mounted) context.go('/login');
      return;
    }

    debugPrint('SplashScreen: isAuthenticated=${authProvider.isAuthenticated}');
    debugPrint('SplashScreen: Navigating...');

    // Navigate based on auth state
    if (authProvider.isAuthenticated) {
      final role = authProvider.user?.role;
      debugPrint('SplashScreen: User role=$role');

      final route = switch (role) {
        UserRole.admin => '/admin',
        UserRole.teacher => '/teacher',
        _ => '/student',
      };

      debugPrint('SplashScreen: Going to $route');
      if (mounted) context.go(route);
    } else {
      debugPrint('SplashScreen: Going to /login');
      if (mounted) context.go('/login');
    }
  }

  void _updateStatus(String message) {
    if (mounted) {
      setState(() {
        _statusMessage = message;
        _debugInfo = 'Backend URL: http://192.168.0.102:8080\nStatus: $message';
      });
    }
    debugPrint('SplashScreen: $message');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.deepNavy,
      body: Container(
        width: double.infinity,
        height: double.infinity,
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              AppColors.deepNavy,
              AppColors.deepNavy.withValues(alpha: 0.9),
              AppColors.lightNavy.withValues(alpha: 0.3),
            ],
          ),
        ),
        child: SafeArea(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Spacer(flex: 2),

              // Animated Logo
              AnimatedBuilder(
                animation: _animationController,
                builder: (context, child) {
                  return Opacity(
                    opacity: _fadeAnimation.value,
                    child: Transform.scale(
                      scale: _scaleAnimation.value,
                      child: child,
                    ),
                  );
                },
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    // Alatoo Logo
                    Container(
                      width: 180,
                      height: 180,
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: AppColors.white,
                        borderRadius: BorderRadius.circular(24),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withValues(alpha: 0.2),
                            blurRadius: 30,
                            offset: const Offset(0, 10),
                          ),
                        ],
                      ),
                      child: Image.asset(
                        'assets/images/cropped-ALATOO-LOGO-2-2048x2048.png',
                        fit: BoxFit.contain,
                        errorBuilder: (context, error, stackTrace) {
                          debugPrint('SplashScreen: Logo error: $error');
                          return const Icon(
                            Icons.school,
                            size: 100,
                            color: AppColors.deepNavy,
                          );
                        },
                      ),
                    ),

                    const SizedBox(height: 32),

                    // App Name
                    const Text(
                      'EduOps',
                      style: TextStyle(
                        fontSize: 42,
                        fontWeight: FontWeight.w700,
                        color: AppColors.white,
                        letterSpacing: 2,
                      ),
                    ),

                    const SizedBox(height: 8),

                    // Tagline
                    Text(
                      'Alatoo International University',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w400,
                        color: AppColors.white.withValues(alpha: 0.8),
                        letterSpacing: 0.5,
                      ),
                    ),
                  ],
                ),
              ),

              const Spacer(flex: 2),

              // Loading Indicator
              AnimatedBuilder(
                animation: _animationController,
                builder: (context, child) {
                  return Opacity(opacity: _fadeAnimation.value, child: child);
                },
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    SizedBox(
                      width: 40,
                      height: 40,
                      child: CircularProgressIndicator(
                        strokeWidth: 3,
                        valueColor: AlwaysStoppedAnimation<Color>(
                          AppColors.white.withValues(alpha: 0.8),
                        ),
                      ),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'Loading...',
                      style: TextStyle(
                        fontSize: 14,
                        color: AppColors.white.withValues(alpha: 0.6),
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 48),

              // Status and Debug Info
              AnimatedBuilder(
                animation: _animationController,
                builder: (context, child) {
                  return Opacity(opacity: _fadeAnimation.value, child: child);
                },
                child: Container(
                  margin: const EdgeInsets.symmetric(horizontal: 24),
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Colors.black.withValues(alpha: 0.3),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        _statusMessage,
                        style: TextStyle(
                          fontSize: 14,
                          color: AppColors.white.withValues(alpha: 0.8),
                          fontWeight: FontWeight.w500,
                        ),
                        textAlign: TextAlign.center,
                      ),
                      if (_debugInfo.isNotEmpty) ...[
                        const SizedBox(height: 8),
                        Text(
                          _debugInfo,
                          style: TextStyle(
                            fontSize: 11,
                            color: AppColors.white.withValues(alpha: 0.5),
                            fontFamily: 'monospace',
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ],
                    ],
                  ),
                ),
              ),

              const SizedBox(height: 24),

              // Footer
              Text(
                '© 2026 Alatoo International University',
                style: TextStyle(
                  fontSize: 12,
                  color: AppColors.white.withValues(alpha: 0.5),
                ),
              ),

              const SizedBox(height: 24),
            ],
          ),
        ),
      ),
    );
  }
}
