/// EduOps Design System - Color Constants
/// Based on Alatoo University branding: White, Red, Navy Blue
library;

import 'package:flutter/material.dart';

class AppColors {
  // === PRIMARY COLORS ===

  /// Pure White - Backgrounds, cards, input fields
  static const Color white = Color(0xFFFFFFFF);

  /// Deep Navy - Headers, primary buttons, navigation, text
  static const Color deepNavy = Color(0xFF2D2652);

  /// Accent Red - Alerts, notifications, important actions, badges
  static const Color accentRed = Color(0xFFD11021);

  // === SECONDARY COLORS ===

  /// Light Navy - Secondary buttons, hover states
  static const Color lightNavy = Color(0xFF4A4270);

  /// Soft Gray - Page backgrounds, dividers
  static const Color softGray = Color(0xFFF5F5F7);

  /// Medium Gray - Placeholder text, disabled states
  static const Color mediumGray = Color(0xFF8E8E93);

  /// Dark Gray - Body text, descriptions
  static const Color darkGray = Color(0xFF3A3A3C);

  /// Secondary Text Color (Alias for Medium Gray)
  static const Color textSecondary = mediumGray;

  /// Success Green - Success states, present attendance
  static const Color successGreen = Color(0xFF34C759);

  /// Warning Amber - Warnings, late attendance
  static const Color warningAmber = Color(0xFFFF9500);

  /// Light Red - Error backgrounds, absent highlight
  static const Color lightRed = Color(0xFFFFE5E7);

  /// Light Blue - Selected states, highlights
  static const Color lightBlue = Color(0xFFE8E6F0);

  /// Primary Blue - Primary actions, links
  static const Color primaryBlue = Color(0xFF007AFF);

  /// Light Gray - Borders, disabled states
  static const Color lightGray = Color(0xFFD1D1D6);

  /// Background Gray - Page backgrounds
  static const Color backgroundGray = Color(0xFFF2F2F7);

  // === MATERIAL COLOR SWATCH ===

  static const MaterialColor navySwatch =
      MaterialColor(0xFF2D2652, <int, Color>{
        50: Color(0xFFE8E6F0),
        100: Color(0xFFC5C1D9),
        200: Color(0xFF9F98C0),
        300: Color(0xFF796FA6),
        400: Color(0xFF5C5093),
        500: Color(0xFF2D2652),
        600: Color(0xFF3F3180),
        700: Color(0xFF362975),
        800: Color(0xFF2D226B),
        900: Color(0xFF1E1558),
      });
}
