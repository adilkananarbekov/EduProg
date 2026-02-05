/// EduOps - App Router (GoRouter Navigation)
library;

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../../providers/auth_provider.dart';
import '../../models/user.dart';
import '../../screens/auth/splash_screen.dart';
import '../../screens/auth/login_screen.dart';
import '../../screens/student/student_home_screen.dart';
import '../../screens/student/schedule_screen.dart';
import '../../screens/student/grades_screen.dart';
import '../../screens/student/attendance_screen.dart';
import '../../screens/teacher/teacher_home_screen.dart';
import '../../screens/admin/admin_home_screen.dart';
import '../../screens/admin/admin_users_screen.dart';
import '../../screens/admin/admin_announcements_screen.dart';
import '../../screens/admin/admin_classes_screen.dart';
import '../../screens/admin/admin_classrooms_screen.dart';
import '../../screens/admin/admin_parents_screen.dart';
import '../../screens/profile/profile_screen.dart';
import '../../screens/common/universal_schedule_screen.dart';
import '../../widgets/main_scaffold.dart';

class AppRouter {
  static final navigatorKey = GlobalKey<NavigatorState>();

  static GoRouter createRouter(AuthProvider authProvider) {
    return GoRouter(
      navigatorKey: navigatorKey,
      initialLocation: '/splash',
      debugLogDiagnostics: true, // Enable debug logging
      routes: [
        // Auth routes
        GoRoute(
          path: '/splash',
          builder: (context, state) => const SplashScreen(),
        ),
        GoRoute(
          path: '/login',
          builder: (context, state) => const LoginScreen(),
        ),

        // Student routes with shell
        ShellRoute(
          builder: (context, state, child) => MainScaffold(
            currentPath: state.uri.path,
            userRole:
                context.watch<AuthProvider>().user?.role ?? UserRole.student,
            child: child,
          ),
          routes: [
            GoRoute(
              path: '/student',
              builder: (context, state) => const StudentHomeScreen(),
            ),
            GoRoute(
              path: '/schedule',
              builder: (context, state) => const ScheduleScreen(),
            ),
            GoRoute(
              path: '/grades',
              builder: (context, state) => const GradesScreen(),
            ),
            GoRoute(
              path: '/attendance',
              builder: (context, state) => const AttendanceScreen(),
            ),
            GoRoute(
              path: '/teacher',
              builder: (context, state) => const TeacherHomeScreen(),
            ),
            GoRoute(
              path: '/admin',
              builder: (context, state) => const AdminHomeScreen(),
            ),
            // Admin sub-routes
            GoRoute(
              path: '/admin/users',
              builder: (context, state) => const AdminUsersScreen(),
            ),
            GoRoute(
              path: '/admin/announcements',
              builder: (context, state) => const AdminAnnouncementsScreen(),
            ),
            GoRoute(
              path: '/admin/classrooms',
              builder: (context, state) => const AdminClassroomsScreen(),
            ),
            GoRoute(
              path: '/admin/classes',
              builder: (context, state) => const AdminClassesScreen(),
            ),
            GoRoute(
              path: '/admin/parents',
              builder: (context, state) => const AdminParentsScreen(),
            ),
            GoRoute(
              path: '/profile',
              builder: (context, state) => const ProfileScreen(),
            ),
            GoRoute(
              path: '/schedule-viewer',
              builder: (context, state) => const UniversalScheduleScreen(),
            ),
          ],
        ),
      ],
    );
  }
}
