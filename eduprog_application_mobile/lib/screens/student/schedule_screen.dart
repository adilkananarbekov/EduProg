/// EduOps - Schedule Screen
library;

import 'package:flutter/material.dart';
import '../../core/constants/colors.dart';
import '../../services/schedule_service.dart';
import '../../core/network/api_client.dart';
import '../../models/schedule.dart';
import 'package:intl/intl.dart';

class ScheduleScreen extends StatefulWidget {
  const ScheduleScreen({super.key});

  @override
  State<ScheduleScreen> createState() => _ScheduleScreenState();
}

class _ScheduleScreenState extends State<ScheduleScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  late ScheduleService _scheduleService;

  DateTime _selectedDate = DateTime.now();
  List<Schedule> _weekSchedule = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _scheduleService = ScheduleService(ApiClient());
    _loadSchedule();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadSchedule() async {
    setState(() => _isLoading = true);
    try {
      final schedule = await _scheduleService.getWeekSchedule();
      setState(() {
        _weekSchedule = schedule;
        _isLoading = false;
      });
    } catch (e) {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  List<Schedule> _getScheduleForDate(DateTime date) {
    // 1=Mon, 7=Sun
    return _weekSchedule.where((s) => s.dayOfWeek == date.weekday).toList()
      ..sort((a, b) => a.startTime.compareTo(b.startTime));
  }

  Color _getSubjectColor(String subjectName) {
    // Deterministic color generation based on subject name
    final colors = [
      AppColors.deepNavy,
      AppColors.accentRed,
      AppColors.successGreen,
      AppColors.warningAmber,
      Colors.teal,
      Colors.indigo,
      Colors.purple,
    ];
    return colors[subjectName.length % colors.length];
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.softGray,
      appBar: AppBar(
        title: const Text('Schedule'),
        backgroundColor: AppColors.deepNavy,
        elevation: 0,
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: AppColors.accentRed, // More visible
          indicatorWeight: 3,
          labelColor: AppColors.white,
          unselectedLabelColor: AppColors.white.withOpacity(0.6),
          tabs: const [
            Tab(text: 'Day View'),
            Tab(text: 'Week View'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [_buildDayView(), _buildWeekView()],
      ),
    );
  }

  Widget _buildDayView() {
    return Column(
      children: [
        _buildDateSelector(),
        Expanded(
          child: _isLoading
              ? const Center(
                  child: CircularProgressIndicator(color: AppColors.deepNavy),
                )
              : _buildDayScheduleList(_getScheduleForDate(_selectedDate)),
        ),
      ],
    );
  }

  Widget _buildDateSelector() {
    final now = DateTime.now();
    // Generate dates for current week (Mon-Sun)
    final monday = now.subtract(Duration(days: now.weekday - 1));
    final weekDates = List.generate(
      7,
      (index) => monday.add(Duration(days: index)),
    );

    return Container(
      color: AppColors.white,
      padding: const EdgeInsets.symmetric(vertical: 12),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: weekDates.map((date) {
          final isSelected =
              _selectedDate.day == date.day &&
              _selectedDate.month == date.month;
          final isToday = now.day == date.day && now.month == date.month;

          return GestureDetector(
            onTap: () => setState(() => _selectedDate = date),
            child: Container(
              width: 45,
              padding: const EdgeInsets.symmetric(vertical: 8),
              decoration: BoxDecoration(
                color: isSelected ? AppColors.deepNavy : Colors.transparent,
                borderRadius: BorderRadius.circular(12),
                border: isToday && !isSelected
                    ? Border.all(color: AppColors.deepNavy, width: 1)
                    : null,
              ),
              child: Column(
                children: [
                  Text(
                    DateFormat('E').format(date),
                    style: TextStyle(
                      fontSize: 12,
                      color: isSelected
                          ? AppColors.white
                          : AppColors.mediumGray,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '${date.day}',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: isSelected ? AppColors.white : AppColors.deepNavy,
                    ),
                  ),
                ],
              ),
            ),
          );
        }).toList(),
      ),
    );
  }

  Widget _buildDayScheduleList(List<Schedule> schedules) {
    if (schedules.isEmpty) {
      return _buildEmptyState();
    }

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: schedules.length,
      itemBuilder: (context, index) {
        final schedule = schedules[index];
        return _buildDetailedClassCard(schedule);
      },
    );
  }

  Widget _buildDetailedClassCard(Schedule schedule) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: IntrinsicHeight(
        child: Row(
          children: [
            // Color Strip
            Container(
              width: 6,
              decoration: BoxDecoration(
                color: _getSubjectColor(schedule.subjectName),
                borderRadius: const BorderRadius.horizontal(
                  left: Radius.circular(16),
                ),
              ),
            ),
            // Content
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    // Time
                    Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          schedule.startTime.substring(0, 5),
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: AppColors.deepNavy,
                          ),
                        ),
                        Text(
                          schedule.endTime.substring(0, 5),
                          style: const TextStyle(
                            fontSize: 14,
                            color: AppColors.mediumGray,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(width: 16),
                    Container(width: 1, color: AppColors.softGray),
                    const SizedBox(width: 16),
                    // Details
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            schedule.subjectName,
                            style: const TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                              color: AppColors.deepNavy,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Row(
                            children: [
                              const Icon(
                                Icons.person_outline,
                                size: 14,
                                color: AppColors.mediumGray,
                              ),
                              const SizedBox(width: 4),
                              Expanded(
                                child: Text(
                                  schedule.teacherName,
                                  style: const TextStyle(
                                    fontSize: 13,
                                    color: AppColors.mediumGray,
                                  ),
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 2),
                          Row(
                            children: [
                              const Icon(
                                Icons.room_outlined,
                                size: 14,
                                color: AppColors.mediumGray,
                              ),
                              const SizedBox(width: 4),
                              Text(
                                schedule.room,
                                style: const TextStyle(
                                  fontSize: 13,
                                  color: AppColors.mediumGray,
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildWeekView() {
    if (_isLoading) {
      return const Center(
        child: CircularProgressIndicator(color: AppColors.deepNavy),
      );
    }

    // Advanced "Timetable" Grid View
    final days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'];
    // Assuming periods 1-7, approximately 8am to 3pm
    // This is a simplified grid for mobile view

    return Column(
      children: [
        // Grid Header
        Container(
          height: 40,
          color: AppColors.white,
          child: Row(
            children: [
              const SizedBox(width: 50), // Time column spacer
              ...days.map((day) {
                final isToday =
                    DateTime.now().weekday == (days.indexOf(day) + 1);
                return Expanded(
                  child: Container(
                    alignment: Alignment.center,
                    color: isToday ? AppColors.deepNavy.withOpacity(0.1) : null,
                    child: Text(
                      day,
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: isToday
                            ? AppColors.deepNavy
                            : AppColors.mediumGray,
                      ),
                    ),
                  ),
                );
              }),
            ],
          ),
        ),

        // Grid Body
        Expanded(
          child: SingleChildScrollView(
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Time Column
                SizedBox(
                  width: 50,
                  child: Column(
                    children: List.generate(8, (index) {
                      // Generating time slots from 08:00
                      final time = 8 + index;
                      return SizedBox(
                        height: 60, // Fixed height per hour/slot
                        child: Center(
                          child: Text(
                            '$time:00',
                            style: const TextStyle(
                              fontSize: 12,
                              color: AppColors.mediumGray,
                            ),
                          ),
                        ),
                      );
                    }),
                  ),
                ),

                // Days Columns
                Expanded(
                  child: Row(
                    children: List.generate(5, (dayIndex) {
                      // Filter schedules for this day (Mon=1, etc)
                      final daySchedules = _weekSchedule
                          .where((s) => s.dayOfWeek == dayIndex + 1)
                          .toList();

                      return Expanded(
                        child: Container(
                          decoration: BoxDecoration(
                            border: Border(
                              left: BorderSide(
                                color: Colors.grey.withOpacity(0.1),
                              ),
                            ),
                          ),
                          child: SizedBox(
                            height: 60 * 8, // 8 hours * 60px
                            child: Stack(
                              children: daySchedules.map((schedule) {
                                // Calculate position based on time
                                // Parsing "08:30:00"
                                final parts = schedule.startTime.split(':');
                                final hour = int.parse(parts[0]);
                                final minute = int.parse(parts[1]);

                                // Start at 8:00 (offset 8)
                                final double top =
                                    ((hour - 8) * 60) + minute.toDouble();

                                return Positioned(
                                  top: top,
                                  left: 2,
                                  right: 2,
                                  height:
                                      55, // Fixed duration height for visual clarity roughly 1hr slot
                                  child: Container(
                                    padding: const EdgeInsets.all(4),
                                    decoration: BoxDecoration(
                                      color: _getSubjectColor(
                                        schedule.subjectName,
                                      ).withOpacity(0.9),
                                      borderRadius: BorderRadius.circular(6),
                                    ),
                                    child: Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.center,
                                      children: [
                                        Text(
                                          schedule.subjectShortName ??
                                              schedule.subjectName
                                                  .substring(0, 3)
                                                  .toUpperCase(),
                                          style: const TextStyle(
                                            color: Colors.white,
                                            fontSize: 10,
                                            fontWeight: FontWeight.bold,
                                          ),
                                          textAlign: TextAlign.center,
                                          maxLines: 1,
                                          overflow: TextOverflow.ellipsis,
                                        ),
                                        Text(
                                          schedule.room,
                                          style: const TextStyle(
                                            color: Colors.white70,
                                            fontSize: 8,
                                          ),
                                          maxLines: 1,
                                        ),
                                      ],
                                    ),
                                  ),
                                );
                              }).toList(),
                            ),
                          ),
                        ),
                      );
                    }),
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.calendar_today_outlined,
            size: 64,
            color: AppColors.mediumGray.withOpacity(0.5),
          ),
          const SizedBox(height: 16),
          const Text(
            'No classes for this day',
            style: TextStyle(color: AppColors.mediumGray, fontSize: 16),
          ),
        ],
      ),
    );
  }
}
