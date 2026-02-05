/// EduOps Design System - API Constants
library;

import 'dart:io';

class ApiConstants {
  // ============================================================
  // IMPORTANT: For physical device testing, set your computer's
  // local IP address below (find it with 'ipconfig' on Windows).
  // Example: '192.168.1.100'
  // ============================================================
  static const String _physicalDeviceIP = '192.168.0.102'; // Your computer's IP

  // Base URLs for different platforms
  static const String _emulatorUrl = 'http://10.0.2.2:8080';
  static const String _iosSimulatorUrl = 'http://localhost:8080';

  /// Get the appropriate base URL based on platform
  static String get baseUrl {
    // For physical Android devices, use the configured IP
    if (Platform.isAndroid) {
      // Check if running on emulator (10.0.2.2 is only accessible from emulator)
      // For now, we use the physical device IP - user should update _physicalDeviceIP
      return 'http://$_physicalDeviceIP:8080';
    } else if (Platform.isIOS) {
      return _iosSimulatorUrl;
    }
    return _emulatorUrl;
  }

  // Legacy constants for backward compatibility
  static const String baseUrlIos = 'http://localhost:8080';

  // Auth Endpoints
  static const String login = '/api/auth/login';
  static const String register = '/api/auth/register';
  static const String refresh = '/api/auth/refresh';

  // Profile Endpoints
  static const String profile = '/api/profile';
  static const String profilePassword = '/api/profile/password';

  // Dashboard Endpoints
  static const String dashboardStudent = '/api/dashboard/student';
  static const String dashboardTeacher = '/api/dashboard/teacher';
  static const String dashboardAdmin = '/api/dashboard/admin';

  // Schedule Endpoints
  static const String schedule = '/api/schedule';
  static const String scheduleWeek = '/api/schedule/week';
  static const String scheduleGenerate = '/api/schedule/generate';
  static const String scheduleByClass = '/api/schedule/class';
  static const String scheduleByTeacher = '/api/schedule/teacher';
  static const String scheduleByClassroom = '/api/schedule/classroom';

  // Common/Public List Endpoints
  static const String commonTeachers = '/api/teachers';
  static const String commonClasses = '/api/classes';
  static const String commonClassrooms = '/api/admin/classrooms';

  // Grades Endpoints
  static const String grades = '/api/grades';
  static const String gradesBySubject = '/api/grades/by-subject';

  // Attendance Endpoints
  static const String attendance = '/api/attendance';
  static const String attendanceStats = '/api/attendance/stats';

  // Announcements Endpoints
  static const String announcements = '/api/announcements';

  // Homework Endpoints
  static const String homework = '/api/homework';
  static const String homeworkSubmissions = '/api/homework/submissions';

  // Messages Endpoints
  static const String messages = '/api/messages';

  // Notifications Endpoints
  static const String notifications = '/api/notifications';

  // Events Endpoints
  static const String events = '/api/events';

  // Admin Endpoints
  static const String adminUsers = '/api/admin/users';
  static const String adminStudents = '/api/admin/students';
  static const String adminTeachers = '/api/admin/teachers';
  static const String adminClasses = '/api/admin/classes';
  static const String adminSubjects = '/api/admin/subjects';
  static const String adminParents = '/api/admin/parents';
  static const String adminSimulation = '/api/admin/simulation/schedule';

  // Timeouts
  static const Duration connectTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
}
