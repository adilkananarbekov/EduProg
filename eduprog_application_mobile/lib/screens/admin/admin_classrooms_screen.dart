/// EduOps - Admin Classrooms Screen
library;

import 'package:flutter/material.dart';
import '../../core/constants/colors.dart';
import '../../core/network/api_client.dart';
import '../../services/admin_service.dart';

class AdminClassroomsScreen extends StatefulWidget {
  const AdminClassroomsScreen({super.key});

  @override
  State<AdminClassroomsScreen> createState() => _AdminClassroomsScreenState();
}

class _AdminClassroomsScreenState extends State<AdminClassroomsScreen> {
  late final AdminService _adminService;
  List<Map<String, dynamic>> _classrooms = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _adminService = AdminService(ApiClient());
    _loadClassrooms();
  }

  Future<void> _loadClassrooms() async {
    setState(() => _isLoading = true);
    try {
      final classrooms = await _adminService.getClassrooms();
      if (mounted) {
        setState(() {
          _classrooms = classrooms;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('Error loading classrooms: $e')));
      }
    }
  }

  Future<void> _editClassroom(Map<String, dynamic> classroom) async {
    final nameController = TextEditingController(text: classroom['name'] ?? '');
    final formKey = GlobalKey<FormState>();

    await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Edit ${classroom['roomNumber']}'),
        content: Form(
          key: formKey,
          child: TextFormField(
            controller: nameController,
            decoration: const InputDecoration(labelText: 'Room Name (Alias)'),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () async {
              if (formKey.currentState!.validate()) {
                final newName = nameController.text.trim();
                Navigator.pop(context);
                try {
                  await _adminService.updateClassroom(classroom['id'], {
                    'name': newName.isEmpty ? null : newName,
                  });
                  _loadClassrooms();
                  if (mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Classroom updated')),
                    );
                  }
                } catch (e) {
                  if (mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Update failed: $e')),
                    );
                  }
                }
              }
            },
            child: const Text('Save'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    // Group by floor
    final floor1 = _classrooms.where((c) => c['floor'] == 1).toList();
    final floor2 = _classrooms.where((c) => c['floor'] == 2).toList();
    // Sort by room number
    floor1.sort(
      (a, b) => (a['roomNumber'] as String).compareTo(b['roomNumber']),
    );
    floor2.sort(
      (a, b) => (a['roomNumber'] as String).compareTo(b['roomNumber']),
    );

    return Scaffold(
      backgroundColor: AppColors.softGray,
      appBar: AppBar(
        title: const Text('Classrooms'),
        backgroundColor: AppColors.deepNavy,
        foregroundColor: AppColors.white,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadClassrooms,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  _buildFloorSection('Floor 1', floor1),
                  const SizedBox(height: 24),
                  _buildFloorSection('Floor 2', floor2),
                ],
              ),
            ),
    );
  }

  Widget _buildFloorSection(String title, List<Map<String, dynamic>> rooms) {
    if (rooms.isEmpty) return const SizedBox.shrink();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 8, bottom: 8),
          child: Text(
            title,
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: AppColors.deepNavy,
            ),
          ),
        ),
        GridView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            childAspectRatio: 2.5,
            crossAxisSpacing: 12,
            mainAxisSpacing: 12,
          ),
          itemCount: rooms.length,
          itemBuilder: (context, index) {
            final room = rooms[index];
            final hasName =
                room['name'] != null &&
                (room['name'] as String).isNotEmpty &&
                (room['name'] as String).isNotEmpty &&
                room['name'] != 'Room ${room['roomNumber']}';

            return GestureDetector(
              onTap: () => _editClassroom(room),
              child: Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 8,
                ),
                decoration: BoxDecoration(
                  color: AppColors.white,
                  borderRadius: BorderRadius.circular(12),
                  boxShadow: [
                    BoxShadow(
                      color: AppColors.deepNavy.withValues(alpha: 0.06),
                      blurRadius: 5,
                      offset: const Offset(0, 2),
                    ),
                  ],
                  border: Border.all(
                    color: AppColors.deepNavy.withValues(alpha: 0.1),
                  ),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      room['roomNumber'],
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: AppColors.deepNavy,
                      ),
                    ),
                    if (hasName)
                      Text(
                        room['name'],
                        style: const TextStyle(
                          fontSize: 12,
                          color: AppColors.primaryBlue,
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                  ],
                ),
              ),
            );
          },
        ),
      ],
    );
  }
}
