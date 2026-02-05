/// EduOps - Main Scaffold with Bottom Navigation
library;

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../core/constants/colors.dart';
import '../models/user.dart';

class MainScaffold extends StatelessWidget {
  final Widget child;
  final String currentPath;
  final UserRole userRole;

  const MainScaffold({
    super.key,
    required this.child,
    required this.currentPath,
    required this.userRole,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(body: child, bottomNavigationBar: _buildBottomNav(context));
  }

  Widget _buildBottomNav(BuildContext context) {
    final items = _getNavItems();
    final currentIndex = _getCurrentIndex();

    return Container(
      decoration: BoxDecoration(
        boxShadow: [
          BoxShadow(
            color: AppColors.deepNavy.withValues(alpha: 0.15),
            blurRadius: 20,
            offset: const Offset(0, -4),
          ),
        ],
      ),
      child: ClipRRect(
        borderRadius: const BorderRadius.vertical(top: Radius.circular(20)),
        child: BottomNavigationBar(
          currentIndex: currentIndex,
          onTap: (index) => _onNavTap(context, index),
          items: items,
          type: BottomNavigationBarType.fixed,
          backgroundColor: AppColors.deepNavy,
          selectedItemColor: AppColors.white,
          unselectedItemColor: AppColors.lightNavy,
          showUnselectedLabels: true,
          selectedFontSize: 12,
          unselectedFontSize: 11,
        ),
      ),
    );
  }

  List<BottomNavigationBarItem> _getNavItems() {
    switch (userRole) {
      case UserRole.student:
        return const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home_rounded),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.calendar_today_rounded),
            label: 'Schedule',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.grade_rounded),
            label: 'Grades',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person_rounded),
            label: 'Profile',
          ),
        ];
      case UserRole.teacher:
        return const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home_rounded),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.calendar_today_rounded),
            label: 'Schedule',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.grade_rounded),
            label: 'Grades',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person_rounded),
            label: 'Profile',
          ),
        ];
      case UserRole.operator:
        return const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home_rounded),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.calendar_today_rounded),
            label: 'Schedule',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.grade_rounded),
            label: 'Grades',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person_rounded),
            label: 'Profile',
          ),
        ];
      case UserRole.admin:
        return const [
          BottomNavigationBarItem(
            icon: Icon(Icons.dashboard_rounded),
            label: 'Dashboard',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.people_rounded),
            label: 'Users',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.campaign_rounded),
            label: 'Announce',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person_rounded),
            label: 'Profile',
          ),
        ];
    }
  }

  int _getCurrentIndex() {
    switch (userRole) {
      case UserRole.student:
        if (currentPath == '/student') return 0;
        if (currentPath == '/schedule') return 1;
        if (currentPath == '/grades') return 2;
        if (currentPath == '/profile') return 3;
        return 0;
      case UserRole.teacher:
        if (currentPath == '/teacher') return 0;
        if (currentPath == '/schedule') return 1;
        if (currentPath == '/grades') return 2;
        if (currentPath == '/profile') return 3;
        return 0;
      case UserRole.operator:
        if (currentPath == '/teacher') return 0;
        if (currentPath == '/schedule') return 1;
        if (currentPath == '/grades') return 2;
        if (currentPath == '/profile') return 3;
        return 0;
      case UserRole.admin:
        if (currentPath == '/admin') return 0;
        if (currentPath.startsWith('/admin/users')) return 1;
        if (currentPath.startsWith('/admin/announcements')) return 2;
        if (currentPath == '/profile') return 3;
        return 0;
    }
  }

  void _onNavTap(BuildContext context, int index) {
    switch (userRole) {
      case UserRole.student:
        final routes = ['/student', '/schedule', '/grades', '/profile'];
        context.go(routes[index]);
        break;
      case UserRole.teacher:
        final routes = ['/teacher', '/schedule', '/grades', '/profile'];
        context.go(routes[index]);
        break;
      case UserRole.operator:
        final routes = ['/teacher', '/schedule', '/grades', '/profile'];
        context.go(routes[index]);
        break;
      case UserRole.admin:
        final routes = [
          '/admin',
          '/admin/users',
          '/admin/announcements',
          '/profile',
        ];
        context.go(routes[index]);
        break;
    }
  }
}
