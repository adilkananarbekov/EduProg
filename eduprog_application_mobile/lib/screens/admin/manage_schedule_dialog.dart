/// EduOps - Manage Schedule Dialog
library;

import 'package:flutter/material.dart';
import '../../core/constants/colors.dart';
import '../../services/schedule_service.dart';

class ManageScheduleDialog extends StatefulWidget {
  final ScheduleService scheduleService;
  final VoidCallback onScheduleUpdated;

  const ManageScheduleDialog({
    super.key,
    required this.scheduleService,
    required this.onScheduleUpdated,
  });

  @override
  State<ManageScheduleDialog> createState() => _ManageScheduleDialogState();
}

class _ManageScheduleDialogState extends State<ManageScheduleDialog> {
  final _formKey = GlobalKey<FormState>();

  // Form Values
  Map<String, dynamic>? _selectedClass;
  Map<String, dynamic>? _selectedSubject;
  Map<String, dynamic>? _selectedTeacher;
  Map<String, dynamic>? _selectedRoom;

  // For dropdowns
  List<Map<String, dynamic>> _classes = [];
  List<Map<String, dynamic>> _subjects = [];
  List<Map<String, dynamic>> _teachers = [];
  List<Map<String, dynamic>> _rooms = [];

  // Dropdown Labels
  List<String> _days = [
    'Monday',
    'Tuesday',
    'Wednesday',
    'Thursday',
    'Friday',
    'Saturday',
  ];
  String _selectedDay = 'Monday';

  // Time
  TimeOfDay _startTime = const TimeOfDay(hour: 8, minute: 0);
  TimeOfDay _endTime = const TimeOfDay(hour: 9, minute: 30);

  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final classes = await widget.scheduleService.getPublicClasses();
      final teachers = await widget.scheduleService.getPublicTeachers();
      final rooms = await widget.scheduleService.getPublicClassrooms();
      final subjects = await widget.scheduleService.getPublicSubjects();

      if (mounted) {
        setState(() {
          _classes = classes;
          _teachers = teachers;
          _rooms = rooms;
          _subjects = subjects;
          _isLoading = false;
        });
      }
    } catch (e) {
      debugPrint('Error loading form data: $e');
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _selectTime(bool isStart) async {
    final picked = await showTimePicker(
      context: context,
      initialTime: isStart ? _startTime : _endTime,
    );
    if (picked != null) {
      setState(() {
        if (isStart) {
          _startTime = picked;
        } else {
          _endTime = picked;
        }
      });
    }
  }

  Future<void> _submit() async {
    if (_formKey.currentState!.validate()) {
      if (_selectedClass == null ||
          _selectedTeacher == null ||
          _selectedSubject == null ||
          _selectedRoom == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Please select all fields')),
        );
        return;
      }

      setState(() => _isLoading = true);

      try {
        final data = {
          'classGroupId': _selectedClass!['id'],
          'teacherId': _selectedTeacher!['id'],
          'subjectId': _selectedSubject!['id'],
          'dayOfWeek': _selectedDay.toUpperCase(),
          'startTime': _formatTime(_startTime),
          'endTime': _formatTime(_endTime),
          'room':
              _selectedRoom!['name'], // Using room name as backend expects String
          'lessonNumber': 1, // Defaulting for now
        };

        await widget.scheduleService.createSchedule(data);

        if (mounted) {
          Navigator.pop(context);
          widget.onScheduleUpdated();
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Class added successfully')),
          );
        }
      } catch (e) {
        if (mounted) {
          setState(() => _isLoading = false);
          ScaffoldMessenger.of(
            context,
          ).showSnackBar(SnackBar(content: Text('Failed to add class: $e')));
        }
      }
    }
  }

  String _formatTime(TimeOfDay time) {
    final hour = time.hour.toString().padLeft(2, '0');
    final minute = time.minute.toString().padLeft(2, '0');
    return '$hour:$minute:00';
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Add Class to Schedule'),
      content: _isLoading
          ? const SizedBox(
              height: 200,
              child: Center(child: CircularProgressIndicator()),
            )
          : SingleChildScrollView(
              child: Form(
                key: _formKey,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    // Class Selection
                    DropdownButtonFormField<Map<String, dynamic>>(
                      value: _selectedClass,
                      decoration: const InputDecoration(
                        labelText: 'Class Group',
                      ),
                      items: _classes.map((c) {
                        return DropdownMenuItem(
                          value: c,
                          child: Text(c['name']),
                        );
                      }).toList(),
                      onChanged: (v) => setState(() => _selectedClass = v),
                      validator: (v) => v == null ? 'Required' : null,
                    ),
                    const SizedBox(height: 16),
                    // Subject Selection
                    DropdownButtonFormField<Map<String, dynamic>>(
                      value: _selectedSubject,
                      decoration: const InputDecoration(labelText: 'Subject'),
                      items: _subjects.map((s) {
                        return DropdownMenuItem(
                          value: s,
                          child: Text(s['name']),
                        );
                      }).toList(),
                      onChanged: (v) => setState(() => _selectedSubject = v),
                      validator: (v) => v == null ? 'Required' : null,
                    ),
                    const SizedBox(height: 16),
                    // Teacher Selection
                    DropdownButtonFormField<Map<String, dynamic>>(
                      value: _selectedTeacher,
                      decoration: const InputDecoration(labelText: 'Teacher'),
                      items: _teachers.map((t) {
                        return DropdownMenuItem(
                          value: t,
                          child: Text(t['name']),
                        );
                      }).toList(),
                      onChanged: (v) => setState(() => _selectedTeacher = v),
                      validator: (v) => v == null ? 'Required' : null,
                    ),
                    const SizedBox(height: 16),
                    // Room Selection
                    DropdownButtonFormField<Map<String, dynamic>>(
                      value: _selectedRoom,
                      decoration: const InputDecoration(labelText: 'Room'),
                      items: _rooms.map((r) {
                        return DropdownMenuItem(
                          value: r,
                          child: Text('${r['name']} ${r['roomNumber'] ?? ""}'),
                        );
                      }).toList(),
                      onChanged: (v) => setState(() => _selectedRoom = v),
                      validator: (v) => v == null ? 'Required' : null,
                    ),
                    const SizedBox(height: 16),
                    // Day Selection
                    DropdownButtonFormField<String>(
                      value: _selectedDay,
                      decoration: const InputDecoration(labelText: 'Day'),
                      items: _days.map((d) {
                        return DropdownMenuItem(value: d, child: Text(d));
                      }).toList(),
                      onChanged: (v) => setState(() => _selectedDay = v!),
                    ),
                    const SizedBox(height: 16),
                    // Time Selection
                    Row(
                      children: [
                        Expanded(
                          child: TextButton.icon(
                            icon: const Icon(Icons.access_time),
                            label: Text(_startTime.format(context)),
                            onPressed: () => _selectTime(true),
                          ),
                        ),
                        const Text('-'),
                        Expanded(
                          child: TextButton.icon(
                            icon: const Icon(Icons.access_time),
                            label: Text(_endTime.format(context)),
                            onPressed: () => _selectTime(false),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Cancel'),
        ),
        ElevatedButton(
          onPressed: _submit,
          style: ElevatedButton.styleFrom(
            backgroundColor: AppColors.primaryBlue,
            foregroundColor: Colors.white,
          ),
          child: const Text('Add'),
        ),
      ],
    );
  }
}
