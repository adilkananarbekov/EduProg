/// EduOps - Class Schedule Screen (for teachers)
library;

import 'package:flutter/material.dart';
import '../../models/schedule.dart';
import '../../services/teacher_service.dart';
import '../../core/network/api_client.dart';

class ClassScheduleScreen extends StatefulWidget {
  final int classId;
  final String className;

  const ClassScheduleScreen({
    super.key,
    required this.classId,
    required this.className,
  });

  @override
  State<ClassScheduleScreen> createState() => _ClassScheduleScreenState();
}

class _ClassScheduleScreenState extends State<ClassScheduleScreen>
    with SingleTickerProviderStateMixin {
  late TeacherService _teacherService;
  late TabController _tabController;
  WeekSchedule? _weekSchedule;
  bool _isLoading = true;
  String? _error;

  final List<String> _weekDays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'];

  @override
  void initState() {
    super.initState();
    _teacherService = TeacherService(ApiClient());
    _tabController = TabController(length: 5, vsync: this);

    // Set initial tab to current day
    final now = DateTime.now();
    if (now.weekday >= 1 && now.weekday <= 5) {
      _tabController.index = now.weekday - 1;
    }

    _loadSchedule();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadSchedule() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final schedules = await _teacherService.getClassSchedule(widget.classId);
      setState(() {
        _weekSchedule = WeekSchedule.fromList(schedules);
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Failed to load schedule: $e';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Class ${widget.className}'),
            Text('Weekly Schedule', style: theme.textTheme.bodySmall),
          ],
        ),
        bottom: TabBar(
          controller: _tabController,
          tabs: _weekDays.map((day) => Tab(text: day)).toList(),
        ),
      ),
      body: _buildContent(theme),
    );
  }

  Widget _buildContent(ThemeData theme) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 48, color: theme.colorScheme.error),
            const SizedBox(height: 16),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 32),
              child: Text(_error!, textAlign: TextAlign.center),
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadSchedule,
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    return TabBarView(
      controller: _tabController,
      children: [
        _buildDaySchedule(1, theme),
        _buildDaySchedule(2, theme),
        _buildDaySchedule(3, theme),
        _buildDaySchedule(4, theme),
        _buildDaySchedule(5, theme),
      ],
    );
  }

  Widget _buildDaySchedule(int dayOfWeek, ThemeData theme) {
    final daySchedule = _weekSchedule?.getForDay(dayOfWeek) ?? [];

    if (daySchedule.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.free_cancellation,
              size: 64,
              color: theme.colorScheme.outline,
            ),
            const SizedBox(height: 16),
            Text(
              'No classes on this day',
              style: theme.textTheme.titleMedium?.copyWith(
                color: theme.colorScheme.outline,
              ),
            ),
          ],
        ),
      );
    }

    daySchedule.sort((a, b) => a.startTime.compareTo(b.startTime));

    return RefreshIndicator(
      onRefresh: _loadSchedule,
      child: ListView.builder(
        itemCount: daySchedule.length,
        padding: const EdgeInsets.all(16),
        itemBuilder: (context, index) {
          final schedule = daySchedule[index];
          return _ClassScheduleCard(schedule: schedule);
        },
      ),
    );
  }
}

class _ClassScheduleCard extends StatelessWidget {
  final Schedule schedule;

  const _ClassScheduleCard({required this.schedule});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: 4,
              height: 80,
              decoration: BoxDecoration(
                color: theme.colorScheme.secondary,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    schedule.subjectName,
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      Icon(
                        Icons.access_time,
                        size: 16,
                        color: theme.colorScheme.primary,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        schedule.timeRange,
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: theme.colorScheme.primary,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Icon(
                        Icons.person_outline,
                        size: 16,
                        color: theme.colorScheme.outline,
                      ),
                      const SizedBox(width: 4),
                      Expanded(
                        child: Text(
                          schedule.teacherName,
                          style: theme.textTheme.bodyMedium?.copyWith(
                            color: theme.colorScheme.onSurfaceVariant,
                          ),
                        ),
                      ),
                    ],
                  ),
                  if (schedule.room.isNotEmpty) ...[
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Icon(
                          Icons.location_on_outlined,
                          size: 16,
                          color: theme.colorScheme.outline,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          'Room ${schedule.room}',
                          style: theme.textTheme.bodyMedium?.copyWith(
                            color: theme.colorScheme.onSurfaceVariant,
                          ),
                        ),
                      ],
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
