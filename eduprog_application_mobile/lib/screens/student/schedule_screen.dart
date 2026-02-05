/// EduOps - Schedule Screen
library;

import 'package:flutter/material.dart';
import '../../core/constants/colors.dart';
import '../../services/schedule_service.dart';
import '../../core/network/api_client.dart';
import '../../models/schedule.dart';

class ScheduleScreen extends StatefulWidget {
  const ScheduleScreen({super.key});

  @override
  State<ScheduleScreen> createState() => _ScheduleScreenState();
}

class _ScheduleScreenState extends State<ScheduleScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  late ScheduleService _scheduleService;

  int _selectedDay = DateTime.now().weekday;
  List<Schedule> _weekSchedule = [];
  bool _isLoading = true;

  final List<String> _days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

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
      setState(() => _isLoading = false);
    }
  }

  List<Schedule> _getScheduleForDay(int day) {
    return _weekSchedule.where((s) => s.dayOfWeek == day).toList()
      ..sort((a, b) => a.startTime.compareTo(b.startTime));
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
          indicatorColor: AppColors.white,
          indicatorWeight: 3,
          labelColor: AppColors.white,
          unselectedLabelColor: AppColors.white.withValues(alpha: 0.6),
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
        // Day Selector
        Container(
          color: AppColors.white,
          padding: const EdgeInsets.symmetric(vertical: 12),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: List.generate(7, (index) {
              final day = index + 1;
              final isSelected = _selectedDay == day;
              return GestureDetector(
                onTap: () => setState(() => _selectedDay = day),
                child: AnimatedContainer(
                  duration: const Duration(milliseconds: 200),
                  width: 44,
                  height: 56,
                  decoration: BoxDecoration(
                    color: isSelected ? AppColors.deepNavy : Colors.transparent,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        _days[index],
                        style: TextStyle(
                          fontSize: 12,
                          fontWeight: FontWeight.w500,
                          color: isSelected
                              ? AppColors.white
                              : AppColors.mediumGray,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        '${DateTime.now().add(Duration(days: index - DateTime.now().weekday + 1)).day}',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w700,
                          color: isSelected
                              ? AppColors.white
                              : AppColors.deepNavy,
                        ),
                      ),
                    ],
                  ),
                ),
              );
            }),
          ),
        ),

        const SizedBox(height: 8),

        // Schedule List
        Expanded(
          child: _isLoading
              ? const Center(
                  child: CircularProgressIndicator(color: AppColors.deepNavy),
                )
              : _buildScheduleList(_getScheduleForDay(_selectedDay)),
        ),
      ],
    );
  }

  Widget _buildScheduleList(List<Schedule> schedules) {
    if (schedules.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.event_available,
              size: 64,
              color: AppColors.mediumGray.withValues(alpha: 0.5),
            ),
            const SizedBox(height: 16),
            const Text(
              'No classes scheduled',
              style: TextStyle(fontSize: 16, color: AppColors.mediumGray),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: schedules.length,
      itemBuilder: (context, index) {
        final schedule = schedules[index];
        return _buildScheduleCard(schedule, index);
      },
    );
  }

  Widget _buildScheduleCard(Schedule schedule, int index) {
    final colors = [
      AppColors.deepNavy,
      AppColors.accentRed,
      AppColors.successGreen,
      AppColors.warningAmber,
      AppColors.lightNavy,
    ];
    final color = colors[index % colors.length];

    return Container(
      margin: const EdgeInsets.only(bottom: 12),
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
      child: Row(
        children: [
          Container(
            width: 6,
            height: 100,
            decoration: BoxDecoration(
              color: color,
              borderRadius: const BorderRadius.horizontal(
                left: Radius.circular(16),
              ),
            ),
          ),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  // Time
                  Column(
                    children: [
                      Text(
                        schedule.startTime.split(':').take(2).join(':'),
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w700,
                          color: AppColors.deepNavy,
                        ),
                      ),
                      Container(
                        width: 2,
                        height: 20,
                        margin: const EdgeInsets.symmetric(vertical: 4),
                        color: AppColors.softGray,
                      ),
                      Text(
                        schedule.endTime.split(':').take(2).join(':'),
                        style: const TextStyle(
                          fontSize: 14,
                          color: AppColors.mediumGray,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(width: 16),
                  Container(width: 1, height: 60, color: AppColors.softGray),
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
                        const SizedBox(height: 4),
                        Row(
                          children: [
                            const Icon(
                              Icons.location_on_outlined,
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
    );
  }

  Widget _buildWeekView() {
    if (_isLoading) {
      return const Center(
        child: CircularProgressIndicator(color: AppColors.deepNavy),
      );
    }

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: List.generate(5, (dayIndex) {
          final day = dayIndex + 1;
          final schedules = _getScheduleForDay(day);

          return Expanded(
            child: Container(
              margin: EdgeInsets.only(right: dayIndex < 4 ? 8 : 0),
              child: Column(
                children: [
                  // Day Header
                  Container(
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    decoration: BoxDecoration(
                      color: day == DateTime.now().weekday
                          ? AppColors.deepNavy
                          : AppColors.white,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Center(
                      child: Text(
                        _days[dayIndex],
                        style: TextStyle(
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                          color: day == DateTime.now().weekday
                              ? AppColors.white
                              : AppColors.deepNavy,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 8),
                  // Classes
                  ...schedules.map((schedule) => _buildWeekClassCard(schedule)),
                ],
              ),
            ),
          );
        }),
      ),
    );
  }

  Widget _buildWeekClassCard(Schedule schedule) {
    return Container(
      margin: const EdgeInsets.only(bottom: 6),
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: AppColors.lightBlue,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            schedule.startTime.split(':').take(2).join(':'),
            style: const TextStyle(
              fontSize: 10,
              fontWeight: FontWeight.w500,
              color: AppColors.deepNavy,
            ),
          ),
          const SizedBox(height: 2),
          Text(
            schedule.subjectName,
            style: const TextStyle(
              fontSize: 10,
              fontWeight: FontWeight.w600,
              color: AppColors.deepNavy,
            ),
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }
}
