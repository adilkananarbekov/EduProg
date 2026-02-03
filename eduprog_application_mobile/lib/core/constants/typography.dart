/// EduOps Design System - Typography Constants
library;

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'colors.dart';

class AppTypography {
  // === TYPE SCALE ===

  /// H1 - Hero: 32px Bold - Welcome screens, main titles
  static TextStyle h1Hero = GoogleFonts.inter(
    fontSize: 32,
    fontWeight: FontWeight.w700,
    height: 1.25,
    color: AppColors.deepNavy,
  );

  /// H2 - Title: 24px SemiBold - Page titles, section headers
  static TextStyle h2Title = GoogleFonts.inter(
    fontSize: 24,
    fontWeight: FontWeight.w600,
    height: 1.33,
    color: AppColors.deepNavy,
  );

  /// H3 - Subtitle: 20px SemiBold - Card titles, modal headers
  static TextStyle h3Subtitle = GoogleFonts.inter(
    fontSize: 20,
    fontWeight: FontWeight.w600,
    height: 1.4,
    color: AppColors.deepNavy,
  );

  /// Body Large: 17px Regular - Primary content
  static TextStyle bodyLarge = GoogleFonts.inter(
    fontSize: 17,
    fontWeight: FontWeight.w400,
    height: 1.41,
    color: AppColors.darkGray,
  );

  /// Body: 15px Regular - Standard text, descriptions
  static TextStyle body = GoogleFonts.inter(
    fontSize: 15,
    fontWeight: FontWeight.w400,
    height: 1.47,
    color: AppColors.darkGray,
  );

  /// Caption: 13px Regular - Labels, timestamps, hints
  static TextStyle caption = GoogleFonts.inter(
    fontSize: 13,
    fontWeight: FontWeight.w400,
    height: 1.38,
    color: AppColors.mediumGray,
  );

  /// Small: 11px Medium - Badges, tags, metadata
  static TextStyle small = GoogleFonts.inter(
    fontSize: 11,
    fontWeight: FontWeight.w500,
    height: 1.27,
    color: AppColors.mediumGray,
  );

  // === BUTTON STYLES ===

  static TextStyle buttonLarge = GoogleFonts.inter(
    fontSize: 17,
    fontWeight: FontWeight.w600,
    height: 1.29,
  );

  static TextStyle buttonMedium = GoogleFonts.inter(
    fontSize: 15,
    fontWeight: FontWeight.w600,
    height: 1.33,
  );
}
