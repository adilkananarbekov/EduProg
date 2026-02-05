/// EduOps - Student Home Screen (Dashboard)
library;

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../core/constants/colors.dart';
import '../../providers/auth_provider.dart';
import '../../services/schedule_service.dart';
import '../../services/announcement_service.dart';
import '../../core/network/api_client.dart';
import '../../models/schedule.dart';
import '../../models/announcement.dart';
import 'notifications_screen.dart';
import 'teachers_list_screen.dart';

class StudentHomeScreen extends StatefulWidget {
  const StudentHomeScreen({super.key});

  @override
  State<StudentHomeScreen> createState() => _StudentHomeScreenState();
}

class _StudentHomeScreenState extends State<StudentHomeScreen> {
  late ScheduleService _scheduleService;
  late AnnouncementService _announcementService;

  List<Schedule> _todaySchedule = [];
  List<Announcement> _announcements = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    final apiClient = ApiClient();
    _scheduleService = ScheduleService(apiClient);
    _announcementService = AnnouncementService(apiClient);
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final schedule = await _scheduleService.getTodaySchedule();
      final announcements = await _announcementService.getAnnouncements();
      setState(() {
        _todaySchedule = schedule;
        _announcements = announcements.take(3).toList();
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  String _getGreeting() {
    final hour = DateTime.now().hour;
    if (hour < 12) return 'Good Morning';
    if (hour < 17) return 'Good Afternoon';
    return 'Good Evening';
  }

  @override
  Widget build(BuildContext context) {
    final user = context.watch<AuthProvider>().user;

    return Scaffold(
      backgroundColor: AppColors.softGray,
      body: RefreshIndicator(
        onRefresh: _loadData,
        color: AppColors.deepNavy,
        child: CustomScrollView(
          slivers: [
            // App Bar with Gradient
            SliverAppBar(
              expandedHeight: 180,
              pinned: true,
              backgroundColor: AppColors.deepNavy,
              flexibleSpace: FlexibleSpaceBar(
                background: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                      colors: [AppColors.deepNavy, AppColors.lightNavy],
                    ),
                  ),
                  child: SafeArea(
                    child: Padding(
                      padding: const EdgeInsets.all(20),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    '${_getGreeting()},',
                                    style: TextStyle(
                                      fontSize: 16,
                                      color: AppColors.white.withValues(
                                        alpha: 0.8,
                                      ),
                                    ),
                                  ),
                                  const SizedBox(height: 4),
                                  Text(
                                    user?.firstName ?? 'Student',
                                    style: const TextStyle(
                                      fontSize: 28,
                                      fontWeight: FontWeight.w700,
                                      color: AppColors.white,
                                    ),
                                  ),
                                ],
                              ),
                              // Notification and Profile
                              Row(
                                children: [
                                  IconButton(
                                    onPressed: () => Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                        builder: (_) =>
                                            const NotificationsScreen(),
                                      ),
                                    ),
                                    icon: Stack(
                                      children: [
                                        const Icon(
                                          Icons.notifications_outlined,
                                          color: AppColors.white,
                                          size: 28,
                                        ),
                                        Positioned(
                                          right: 0,
                                          top: 0,
                                          child: Container(
                                            width: 10,
                                            height: 10,
                                            decoration: const BoxDecoration(
                                              color: AppColors.accentRed,
                                              shape: BoxShape.circle,
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                  const SizedBox(width: 4),
                                  GestureDetector(
                                    onTap: () => context.go('/profile'),
                                    child: CircleAvatar(
                                      radius: 22,
                                      backgroundColor: AppColors.white,
                                      child: Text(
                                        user?.firstName
                                                .substring(0, 1)
                                                .toUpperCase() ??
                                            'S',
                                        style: const TextStyle(
                                          color: AppColors.deepNavy,
                                          fontWeight: FontWeight.w700,
                                          fontSize: 18,
                                        ),
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                          const SizedBox(height: 8),
                          Text(
                            DateFormat(
                              'EEEE, MMMM d, y',
                            ).format(DateTime.now()),
                            style: TextStyle(
                              fontSize: 14,
                              color: AppColors.white.withValues(alpha: 0.7),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),

            // Content
            SliverPadding(
              padding: const EdgeInsets.all(16),
              sliver: SliverList(
                delegate: SliverChildListDelegate([
                  // Quick Stats
                  _buildQuickStats(),
                  const SizedBox(height: 20),

                  // Today's Schedule
                  _buildSectionHeader(
                    "Today's Classes",
                    onSeeAll: () => context.go('/schedule'),
                  ),
                  const SizedBox(height: 12),
                  _buildTodaySchedule(),
                  const SizedBox(height: 20),

                  // Announcements
                  _buildSectionHeader('Announcements'),
                  const SizedBox(height: 12),
                  _buildAnnouncements(),
                  const SizedBox(height: 20),

                  // Quick Actions
                  _buildSectionHeader('Quick Actions'),
                  const SizedBox(height: 12),
                  _buildQuickActions(),
                  const SizedBox(height: 100),
                ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildQuickStats() {
    return Row(
      children: [
        Expanded(
          child: _buildStatCard(
            'Attendance',
            '95%',
            AppColors.successGreen,
            Icons.check_circle_outline,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: _buildStatCard(
            'GPA',
            '3.7',
            AppColors.deepNavy,
            Icons.school_outlined,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: _buildStatCard(
            'Classes',
            '${_todaySchedule.length}',
            AppColors.accentRed,
            Icons.calendar_today_outlined,
          ),
        ),
      ],
    );
  }

  Widget _buildStatCard(
    String title,
    String value,
    Color color,
    IconData icon,
  ) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: AppColors.deepNavy.withValues(alpha: 0.06),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        children: [
          Icon(icon, color: color, size: 28),
          const SizedBox(height: 8),
          Text(
            value,
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.w700,
              color: color,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            title,
            style: const TextStyle(fontSize: 12, color: AppColors.mediumGray),
          ),
        ],
      ),
    );
  }

  Widget _buildSectionHeader(String title, {VoidCallback? onSeeAll}) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          title,
          style: const TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.w600,
            color: AppColors.deepNavy,
          ),
        ),
        if (onSeeAll != null)
          TextButton(
            onPressed: onSeeAll,
            child: const Text(
              'See All',
              style: TextStyle(
                color: AppColors.accentRed,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildTodaySchedule() {
    if (_isLoading) {
      return _buildLoadingCard();
    }

    if (_todaySchedule.isEmpty) {
      return _buildEmptyCard(
        'No classes scheduled for today',
        Icons.event_available,
      );
    }

    return Container(
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: AppColors.deepNavy.withValues(alpha: 0.06),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: ListView.separated(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: _todaySchedule.length,
        separatorBuilder: (context, index) => const Divider(height: 1),
        itemBuilder: (context, index) {
          final schedule = _todaySchedule[index];
          return ListTile(
            contentPadding: const EdgeInsets.symmetric(
              horizontal: 16,
              vertical: 8,
            ),
            leading: Container(
              width: 50,
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: AppColors.lightBlue,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    schedule.startTime.split(':').take(2).join(':'),
                    style: const TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                      color: AppColors.deepNavy,
                    ),
                  ),
                ],
              ),
            ),
            title: Text(
              schedule.subjectName,
              style: const TextStyle(
                fontWeight: FontWeight.w600,
                color: AppColors.deepNavy,
              ),
            ),
            subtitle: Text(
              '${schedule.teacherName} • Room ${schedule.room}',
              style: const TextStyle(fontSize: 13, color: AppColors.mediumGray),
            ),
            trailing: Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(
                color: AppColors.softGray,
                borderRadius: BorderRadius.circular(6),
              ),
              child: Text(
                schedule.room,
                style: const TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w500,
                  color: AppColors.darkGray,
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildAnnouncements() {
    if (_isLoading) {
      return _buildLoadingCard();
    }

    if (_announcements.isEmpty) {
      return _buildEmptyCard('No announcements yet', Icons.campaign_outlined);
    }

    return Column(
      children: _announcements.map((announcement) {
        return Container(
          margin: const EdgeInsets.only(bottom: 12),
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: announcement.important
                ? AppColors.lightRed
                : AppColors.white,
            borderRadius: BorderRadius.circular(16),
            border: announcement.important
                ? Border.all(color: AppColors.accentRed.withValues(alpha: 0.3))
                : null,
            boxShadow: [
              BoxShadow(
                color: AppColors.deepNavy.withValues(alpha: 0.06),
                blurRadius: 10,
                offset: const Offset(0, 4),
              ),
            ],
          ),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: announcement.important
                      ? AppColors.accentRed.withValues(alpha: 0.1)
                      : AppColors.lightBlue,
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Icon(
                  announcement.important
                      ? Icons.priority_high
                      : Icons.campaign_outlined,
                  color: announcement.important
                      ? AppColors.accentRed
                      : AppColors.deepNavy,
                  size: 20,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      announcement.title,
                      style: const TextStyle(
                        fontWeight: FontWeight.w600,
                        color: AppColors.deepNavy,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      announcement.content,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        fontSize: 13,
                        color: AppColors.darkGray,
                      ),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      DateFormat('MMM d, y').format(announcement.createdAt),
                      style: const TextStyle(
                        fontSize: 11,
                        color: AppColors.mediumGray,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }

  Widget _buildLoadingCard() {
    return Container(
      height: 100,
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(16),
      ),
      child: const Center(
        child: CircularProgressIndicator(color: AppColors.deepNavy),
      ),
    );
  }

  Widget _buildEmptyCard(String message, IconData icon) {
    return Container(
      padding: const EdgeInsets.all(32),
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        children: [
          Icon(icon, size: 48, color: AppColors.mediumGray),
          const SizedBox(height: 12),
          Text(
            message,
            style: const TextStyle(color: AppColors.mediumGray, fontSize: 14),
          ),
        ],
      ),
    );
  }

  Widget _buildQuickActions() {
    return Container(
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: AppColors.deepNavy.withValues(alpha: 0.06),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        children: [
          _buildQuickActionItem(
            icon: Icons.people_outline,
            title: 'Teachers',
            subtitle: 'View all teachers and their schedules',
            color: AppColors.deepNavy,
            onTap: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const TeachersListScreen()),
            ),
          ),
          const Divider(height: 1),
          _buildQuickActionItem(
            icon: Icons.notifications_outlined,
            title: 'Notifications',
            subtitle: 'Check your messages and updates',
            color: AppColors.accentRed,
            onTap: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const NotificationsScreen()),
            ),
          ),
          const Divider(height: 1),
          _buildQuickActionItem(
            icon: Icons.calendar_month_outlined,
            title: 'Full Schedule',
            subtitle: 'View your complete weekly schedule',
            color: AppColors.successGreen,
            onTap: () => context.go('/schedule'),
          ),
        ],
      ),
    );
  }

  Widget _buildQuickActionItem({
    required IconData icon,
    required String title,
    required String subtitle,
    required Color color,
    required VoidCallback onTap,
  }) {
    return ListTile(
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      leading: Container(
        padding: const EdgeInsets.all(10),
        decoration: BoxDecoration(
          color: color.withValues(alpha: 0.1),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Icon(icon, color: color, size: 24),
      ),
      title: Text(
        title,
        style: const TextStyle(
          fontWeight: FontWeight.w600,
          color: AppColors.deepNavy,
        ),
      ),
      subtitle: Text(
        subtitle,
        style: const TextStyle(fontSize: 12, color: AppColors.mediumGray),
      ),
      trailing: Icon(Icons.chevron_right, color: AppColors.mediumGray),
      onTap: onTap,
    );
  }
}
