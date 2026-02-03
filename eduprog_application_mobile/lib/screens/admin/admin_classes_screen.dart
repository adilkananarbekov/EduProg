/// EduOps - Admin Classes Screen
library;

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../core/constants/colors.dart';
import '../../core/network/api_client.dart';
import '../../services/admin_service.dart';

class AdminClassesScreen extends StatefulWidget {
  const AdminClassesScreen({super.key});

  @override
  State<AdminClassesScreen> createState() => _AdminClassesScreenState();
}

class _AdminClassesScreenState extends State<AdminClassesScreen> {
  late final AdminService _adminService;
  List<Map<String, dynamic>> _classes = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _adminService = AdminService(ApiClient());
    _loadClasses();
  }

  Future<void> _loadClasses() async {
    setState(() => _isLoading = true);
    try {
      final classes = await _adminService.getAllClassGroups();
      if (mounted) {
        setState(() {
          _classes = classes;
          // Sort by grade then name
          _classes.sort((a, b) {
            final gradeA = a['grade'] as int? ?? 0;
            final gradeB = b['grade'] as int? ?? 0;
            if (gradeA != gradeB) return gradeA.compareTo(gradeB);
            return (a['name'] as String? ?? '').compareTo(
              b['name'] as String? ?? '',
            );
          });
          _isLoading = false;
        });
      }
    } catch (e) {
      debugPrint('Error loading classes: $e');
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _showStudentsInClass(Map<String, dynamic> classGroup) async {
    final classId = classGroup['id'];
    final className = classGroup['name'] as String? ?? 'Class';

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) => DraggableScrollableSheet(
        initialChildSize: 0.6,
        minChildSize: 0.3,
        maxChildSize: 0.9,
        expand: false,
        builder: (context, scrollController) => _ClassStudentsSheet(
          classId: classId,
          className: className,
          scrollController: scrollController,
          adminService: _adminService,
        ),
      ),
    );
  }

  Color _getGradeColor(int? grade) {
    if (grade == null) return AppColors.mediumGray;
    final colors = [
      AppColors.primaryBlue,
      AppColors.successGreen,
      AppColors.warningAmber,
      AppColors.accentRed,
    ];
    return colors[grade % colors.length];
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.backgroundGray,
      appBar: AppBar(
        backgroundColor: AppColors.deepNavy,
        foregroundColor: Colors.white,
        title: const Text('Classes'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/admin'),
        ),
        actions: [
          IconButton(icon: const Icon(Icons.refresh), onPressed: _loadClasses),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _classes.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.class_outlined,
                    size: 64,
                    color: AppColors.lightGray,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'No classes found',
                    style: TextStyle(color: AppColors.mediumGray, fontSize: 16),
                  ),
                ],
              ),
            )
          : RefreshIndicator(
              onRefresh: _loadClasses,
              child: GridView.builder(
                padding: const EdgeInsets.all(16),
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  childAspectRatio: 1.2,
                  crossAxisSpacing: 12,
                  mainAxisSpacing: 12,
                ),
                itemCount: _classes.length,
                itemBuilder: (context, index) {
                  final classGroup = _classes[index];
                  final grade = classGroup['grade'] as int?;
                  final name = classGroup['name'] as String? ?? 'Unknown';
                  final studentCount = classGroup['studentCount'] ?? 0;
                  final gradeColor = _getGradeColor(grade);

                  return Card(
                    elevation: 2,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: InkWell(
                      borderRadius: BorderRadius.circular(16),
                      onTap: () => _showStudentsInClass(classGroup),
                      child: Container(
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(16),
                          gradient: LinearGradient(
                            begin: Alignment.topLeft,
                            end: Alignment.bottomRight,
                            colors: [
                              gradeColor.withValues(alpha: 0.1),
                              Colors.white,
                            ],
                          ),
                        ),
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              children: [
                                Container(
                                  padding: const EdgeInsets.all(8),
                                  decoration: BoxDecoration(
                                    color: gradeColor.withValues(alpha: 0.2),
                                    borderRadius: BorderRadius.circular(10),
                                  ),
                                  child: Icon(
                                    Icons.class_,
                                    color: gradeColor,
                                    size: 20,
                                  ),
                                ),
                                const Spacer(),
                                Container(
                                  padding: const EdgeInsets.symmetric(
                                    horizontal: 8,
                                    vertical: 4,
                                  ),
                                  decoration: BoxDecoration(
                                    color: gradeColor.withValues(alpha: 0.15),
                                    borderRadius: BorderRadius.circular(12),
                                  ),
                                  child: Text(
                                    'Grade $grade',
                                    style: TextStyle(
                                      color: gradeColor,
                                      fontSize: 11,
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            const Spacer(),
                            Text(
                              name,
                              style: const TextStyle(
                                fontSize: 22,
                                fontWeight: FontWeight.bold,
                                color: AppColors.deepNavy,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Row(
                              children: [
                                Icon(
                                  Icons.people_outline,
                                  size: 14,
                                  color: AppColors.mediumGray,
                                ),
                                const SizedBox(width: 4),
                                Text(
                                  '$studentCount students',
                                  style: TextStyle(
                                    color: AppColors.mediumGray,
                                    fontSize: 12,
                                  ),
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ),
                  );
                },
              ),
            ),
    );
  }
}

class _ClassStudentsSheet extends StatefulWidget {
  final dynamic classId;
  final String className;
  final ScrollController scrollController;
  final AdminService adminService;

  const _ClassStudentsSheet({
    required this.classId,
    required this.className,
    required this.scrollController,
    required this.adminService,
  });

  @override
  State<_ClassStudentsSheet> createState() => _ClassStudentsSheetState();
}

class _ClassStudentsSheetState extends State<_ClassStudentsSheet> {
  List<Map<String, dynamic>> _students = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadStudents();
  }

  Future<void> _loadStudents() async {
    try {
      final apiClient = ApiClient();
      final response = await apiClient.get(
        '/api/admin/students/class/${widget.classId}',
      );
      if (mounted) {
        setState(() {
          _students = List<Map<String, dynamic>>.from(response.data as List);
          _isLoading = false;
        });
      }
    } catch (e) {
      debugPrint('Error loading students: $e');
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Handle bar
        Container(
          margin: const EdgeInsets.only(top: 12),
          width: 40,
          height: 4,
          decoration: BoxDecoration(
            color: AppColors.lightGray,
            borderRadius: BorderRadius.circular(2),
          ),
        ),
        // Header
        Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Icon(Icons.class_, color: AppColors.primaryBlue),
              const SizedBox(width: 8),
              Text(
                'Class ${widget.className}',
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const Spacer(),
              Text(
                '${_students.length} students',
                style: TextStyle(color: AppColors.mediumGray),
              ),
            ],
          ),
        ),
        const Divider(height: 1),
        // Student list
        Expanded(
          child: _isLoading
              ? const Center(child: CircularProgressIndicator())
              : _students.isEmpty
              ? Center(
                  child: Text(
                    'No students in this class',
                    style: TextStyle(color: AppColors.mediumGray),
                  ),
                )
              : ListView.separated(
                  controller: widget.scrollController,
                  padding: const EdgeInsets.symmetric(vertical: 8),
                  itemCount: _students.length,
                  separatorBuilder: (_, __) => const Divider(height: 1),
                  itemBuilder: (context, index) {
                    final student = _students[index];
                    return ListTile(
                      leading: CircleAvatar(
                        backgroundColor: AppColors.successGreen.withValues(
                          alpha: 0.2,
                        ),
                        child: Text(
                          (student['name'] as String?)
                                  ?.substring(0, 1)
                                  .toUpperCase() ??
                              'S',
                          style: const TextStyle(
                            color: AppColors.successGreen,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      title: Text(
                        student['name'] ?? 'Unknown',
                        style: const TextStyle(fontWeight: FontWeight.w500),
                      ),
                      subtitle: Text(
                        student['email'] ?? '',
                        style: TextStyle(
                          color: AppColors.mediumGray,
                          fontSize: 13,
                        ),
                      ),
                      trailing: Text(
                        student['studentNumber'] ?? '',
                        style: TextStyle(
                          color: AppColors.mediumGray,
                          fontSize: 12,
                        ),
                      ),
                    );
                  },
                ),
        ),
      ],
    );
  }
}
