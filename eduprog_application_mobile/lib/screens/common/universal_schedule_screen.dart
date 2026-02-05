import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/schedule.dart';
import '../../services/schedule_service.dart';
import '../../core/constants/colors.dart';
import '../../providers/auth_provider.dart';
import '../../models/user.dart';
import '../admin/manage_schedule_dialog.dart';

class UniversalScheduleScreen extends StatefulWidget {
  const UniversalScheduleScreen({super.key});

  @override
  State<UniversalScheduleScreen> createState() =>
      _UniversalScheduleScreenState();
}

class _UniversalScheduleScreenState extends State<UniversalScheduleScreen> {
  String _selectedType = 'Class'; // Class, Teacher, Room
  String? _selectedId; // The ID of the selected entity
  List<Schedule> _schedules = [];
  bool _isLoading = false;
  List<Map<String, dynamic>> _entityList = [];

  // Calendar State
  DateTime _selectedDate = DateTime.now();
  final List<String> _weekDays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'];

  @override
  void initState() {
    super.initState();
    _fetchEntityList();
  }

  Future<void> _fetchEntityList() async {
    setState(() => _isLoading = true);
    try {
      final service = context.read<ScheduleService>();
      List<Map<String, dynamic>> data = [];

      if (_selectedType == 'Class') {
        data = await service.getPublicClasses();
      } else if (_selectedType == 'Teacher') {
        data = await service.getPublicTeachers();
      } else if (_selectedType == 'Room') {
        data = await service.getPublicClassrooms();
      }

      setState(() {
        _entityList = data;
        _selectedId = null; // Reset selection
        _schedules = [];
      });
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _fetchSchedule() async {
    if (_selectedId == null) return;
    setState(() => _isLoading = true);
    try {
      final service = context.read<ScheduleService>();
      List<Schedule> data = [];
      int id = int.parse(_selectedId!);

      if (_selectedType == 'Class') {
        data = await service.getScheduleByClass(id);
      } else if (_selectedType == 'Teacher') {
        data = await service.getScheduleByTeacher(id);
      } else if (_selectedType == 'Room') {
        data = await service.getScheduleByClassroom(id);
      }

      setState(() {
        _schedules = data;
      });
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('Failed to load schedule: $e')));
      }
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _onTypeChanged(String? newValue) {
    if (newValue != null && newValue != _selectedType) {
      setState(() {
        _selectedType = newValue;
      });
      _fetchEntityList();
    }
  }

  void _onEntityChanged(String? newValue) {
    setState(() {
      _selectedId = newValue;
    });
    _fetchSchedule();
  }

  List<Schedule> get _filteredSchedules {
    return _schedules.where((s) {
      // API returns 1=Monday, DateTime 1=Monday
      return s.dayOfWeek == _selectedDate.weekday;
    }).toList()..sort((a, b) => a.startTime.compareTo(b.startTime));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      floatingActionButton: _canManageSchedule()
          ? FloatingActionButton(
              onPressed: _showManageScheduleDialog,
              backgroundColor: AppColors.primaryBlue,
              child: const Icon(Icons.add, color: Colors.white),
            )
          : null,
      appBar: AppBar(title: const Text('Schedule Viewer')),
      body: Column(
        children: [
          // Filter Bar
          Container(
            padding: const EdgeInsets.all(16),
            color: Theme.of(context).cardColor,
            child: Column(
              children: [
                // Type Selector
                Row(
                  children: [
                    Expanded(
                      child: DropdownButtonFormField<String>(
                        // ignore: deprecated_member_use
                        value: _selectedType,
                        decoration: const InputDecoration(
                          labelText: 'View By',
                          border: OutlineInputBorder(),
                          contentPadding: EdgeInsets.symmetric(
                            horizontal: 12,
                            vertical: 8,
                          ),
                        ),
                        items: ['Class', 'Teacher', 'Room']
                            .map(
                              (e) => DropdownMenuItem(value: e, child: Text(e)),
                            )
                            .toList(),
                        onChanged: _onTypeChanged,
                      ),
                    ),
                    const SizedBox(width: 16),
                    // Entity Selector
                    Expanded(
                      flex: 2,
                      child: DropdownButtonFormField<String>(
                        // ignore: deprecated_member_use
                        value: _selectedId,
                        decoration: InputDecoration(
                          labelText: 'Select $_selectedType',
                          border: const OutlineInputBorder(),
                          contentPadding: const EdgeInsets.symmetric(
                            horizontal: 12,
                            vertical: 8,
                          ),
                        ),
                        items: _entityList.map((e) {
                          String label = '';
                          String id = e['id'].toString();
                          if (_selectedType == 'Class') {
                            label = e['name'];
                          } else if (_selectedType == 'Teacher') {
                            label = e['name'];
                          } else if (_selectedType == 'Room') {
                            label = '${e['name']} (${e['roomNumber'] ?? ""})';
                          }
                          return DropdownMenuItem(
                            value: id,
                            child: Text(label),
                          );
                        }).toList(),
                        onChanged: _onEntityChanged,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),

          // Calendar View
          _buildCalendarStrip(),

          // Schedule List
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _filteredSchedules.isEmpty
                ? Center(
                    child: Text(
                      _selectedId == null
                          ? 'Select a $_selectedType to view.'
                          : 'No classes on this day.',
                      style: TextStyle(color: AppColors.textSecondary),
                    ),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.all(16),
                    itemCount: _filteredSchedules.length,
                    itemBuilder: (context, index) {
                      final schedule = _filteredSchedules[index];
                      return Card(
                        margin: const EdgeInsets.only(bottom: 12),
                        child: Padding(
                          padding: const EdgeInsets.all(16),
                          child: Row(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              // Time Column
                              SizedBox(
                                width: 60,
                                child: Column(
                                  children: [
                                    Text(
                                      schedule.startTime.toString().substring(
                                        0,
                                        5,
                                      ),
                                      style: const TextStyle(
                                        fontWeight: FontWeight.bold,
                                        fontSize: 16,
                                      ),
                                    ),
                                    Text(
                                      schedule.endTime.toString().substring(
                                        0,
                                        5,
                                      ),
                                      style: TextStyle(
                                        color: AppColors.textSecondary,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              const SizedBox(width: 16),
                              // Details Column
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      schedule
                                          .displaySubjectName, // Use short name or first 4 chars
                                      style: const TextStyle(
                                        fontWeight: FontWeight.bold,
                                        fontSize: 18,
                                        color: AppColors.primaryBlue,
                                      ),
                                    ),
                                    const SizedBox(height: 4),
                                    // Dynamic Content based on View Type
                                    if (_selectedType == 'Class') ...[
                                      Row(
                                        children: [
                                          const Icon(
                                            Icons.person,
                                            size: 16,
                                            color: Colors.grey,
                                          ),
                                          const SizedBox(width: 4),
                                          Text(schedule.teacherName),
                                        ],
                                      ),
                                      const SizedBox(height: 4),
                                      Row(
                                        children: [
                                          const Icon(
                                            Icons.room,
                                            size: 16,
                                            color: Colors.grey,
                                          ),
                                          const SizedBox(width: 4),
                                          Text(
                                            schedule.room.isEmpty
                                                ? 'No Room'
                                                : schedule.room,
                                          ),
                                        ],
                                      ),
                                    ] else if (_selectedType == 'Teacher') ...[
                                      Row(
                                        children: [
                                          const Icon(
                                            Icons.group,
                                            size: 16,
                                            color: Colors.grey,
                                          ),
                                          const SizedBox(width: 4),
                                          Text(schedule.classGroupName),
                                        ],
                                      ),
                                      const SizedBox(height: 4),
                                      Row(
                                        children: [
                                          const Icon(
                                            Icons.room,
                                            size: 16,
                                            color: Colors.grey,
                                          ),
                                          const SizedBox(width: 4),
                                          Text(
                                            schedule.room.isEmpty
                                                ? 'No Room'
                                                : schedule.room,
                                          ),
                                        ],
                                      ),
                                    ] else if (_selectedType == 'Room') ...[
                                      Row(
                                        children: [
                                          const Icon(
                                            Icons.group,
                                            size: 16,
                                            color: Colors.grey,
                                          ),
                                          const SizedBox(width: 4),
                                          Text(schedule.classGroupName),
                                        ],
                                      ),
                                      const SizedBox(height: 4),
                                      Row(
                                        children: [
                                          const Icon(
                                            Icons.person,
                                            size: 16,
                                            color: Colors.grey,
                                          ),
                                          const SizedBox(width: 4),
                                          Text(schedule.teacherName),
                                        ],
                                      ),
                                    ],
                                  ],
                                ),
                              ),
                              // Lesson Number Badge
                              Container(
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 8,
                                  vertical: 4,
                                ),
                                decoration: BoxDecoration(
                                  color: AppColors.primaryBlue.withValues(
                                    alpha: 0.1,
                                  ),
                                  borderRadius: BorderRadius.circular(4),
                                ),
                                child: Text(
                                  'Lesson ${schedule.lessonNumber}',
                                  style: const TextStyle(
                                    color: AppColors.primaryBlue,
                                    fontWeight: FontWeight.bold,
                                    fontSize: 12,
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }

  Widget _buildCalendarStrip() {
    return Container(
      height: 70,
      margin: const EdgeInsets.symmetric(vertical: 8),
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: _weekDays.length,
        padding: const EdgeInsets.symmetric(horizontal: 16),
        itemBuilder: (context, index) {
          // Adjust logic to match real dates if needed, for now just day indices
          // Assuming weekDays[0] = Mon = DateTime.monday (1)
          final dayIndex = index + 1;
          final isSelected = _selectedDate.weekday == dayIndex;

          return GestureDetector(
            onTap: () {
              setState(() {
                // Find next occurrence of this weekday from now?
                // Or just set the property for filtering:
                // For simplicity, we just manipulate a dummy processing date
                // In real app, we might select actual calendar dates.
                // Here we just toggle the "View Day"

                // Hack: adjusting _selectedDate to have the target weekday
                DateTime now = DateTime.now();
                int diff = dayIndex - now.weekday;
                _selectedDate = now.add(Duration(days: diff));
              });
            },
            child: Container(
              width: 60,
              margin: const EdgeInsets.only(right: 12),
              decoration: BoxDecoration(
                color: isSelected ? AppColors.primaryBlue : Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(
                  color: isSelected
                      ? AppColors.primaryBlue
                      : Colors.grey.shade300,
                ),
                boxShadow: isSelected
                    ? [
                        BoxShadow(
                          color: AppColors.primaryBlue.withValues(alpha: 0.3),
                          blurRadius: 8,
                          offset: const Offset(0, 4),
                        ),
                      ]
                    : null,
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    _weekDays[index],
                    style: TextStyle(
                      color: isSelected ? Colors.white : Colors.grey,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 4),
                  // Showing dynamic date number not implemented for brevity
                  // showing just a dot or icon
                  Icon(
                    Icons.calendar_today,
                    size: 14,
                    color: isSelected ? Colors.white : Colors.grey,
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  bool _canManageSchedule() {
    final user = context.read<AuthProvider>().user;
    if (user == null) return false;
    return user.role == UserRole.admin ||
        user.role == UserRole.teacher ||
        user.role == UserRole.operator;
  }

  void _showManageScheduleDialog() {
    showDialog(
      context: context,
      builder: (context) => ManageScheduleDialog(
        scheduleService: context.read<ScheduleService>(),
        onScheduleUpdated: () {
          // Refresh current view
          if (_selectedId != null) _fetchSchedule();
        },
      ),
    );
  }
}
