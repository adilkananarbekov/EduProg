/// EduOps - Admin Parents Screen
library;

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../core/constants/colors.dart';
import '../../core/network/api_client.dart';
import '../../services/admin_service.dart';

class AdminParentsScreen extends StatefulWidget {
  const AdminParentsScreen({super.key});

  @override
  State<AdminParentsScreen> createState() => _AdminParentsScreenState();
}

class _AdminParentsScreenState extends State<AdminParentsScreen> {
  late final AdminService _adminService;
  List<Map<String, dynamic>> _parents = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _adminService = AdminService(ApiClient());
    _loadParents();
  }

  Future<void> _loadParents() async {
    setState(() => _isLoading = true);
    try {
      final parents = await _adminService.getAllParents();
      if (mounted) {
        setState(() {
          _parents = parents;
          _isLoading = false;
        });
      }
    } catch (e) {
      debugPrint('Error loading parents: $e');
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.backgroundGray,
      appBar: AppBar(
        title: const Text('Manage Parents'),
        backgroundColor: AppColors.deepNavy,
        foregroundColor: Colors.white,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/admin'),
        ),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _parents.isEmpty
          ? const Center(child: Text('No parents found'))
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: _parents.length,
              itemBuilder: (context, index) {
                final parent = _parents[index];
                final studentIds = (parent['studentIds'] as List?)?.length ?? 0;

                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: ListTile(
                    contentPadding: const EdgeInsets.all(16),
                    leading: CircleAvatar(
                      backgroundColor: AppColors.primaryBlue.withValues(
                        alpha: 0.1,
                      ),
                      child: const Icon(
                        Icons.family_restroom,
                        color: AppColors.primaryBlue,
                      ),
                    ),
                    title: Text(
                      parent['name'] ?? 'Unknown Parent',
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const SizedBox(height: 4),
                        Text(parent['email'] ?? ''),
                        Text(parent['phoneNumber'] ?? 'No phone'),
                        const SizedBox(height: 8),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 4,
                          ),
                          decoration: BoxDecoration(
                            color: AppColors.successGreen.withValues(
                              alpha: 0.1,
                            ),
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            '$studentIds Linked Student(s)',
                            style: const TextStyle(
                              color: AppColors.successGreen,
                              fontSize: 12,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ],
                    ),
                    isThreeLine: true,
                  ),
                );
              },
            ),
    );
  }
}
