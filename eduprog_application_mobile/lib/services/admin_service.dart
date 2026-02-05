/// EduOps - Admin Service
library;

import 'package:dio/dio.dart';
import '../core/network/api_client.dart';

class AdminService {
  final ApiClient _apiClient;

  AdminService(this._apiClient);

  // Get all users
  Future<List<Map<String, dynamic>>> getAllUsers() async {
    try {
      final response = await _apiClient.get('/api/admin/users');
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  // Get all students
  Future<List<Map<String, dynamic>>> getAllStudents() async {
    try {
      final response = await _apiClient.get('/api/admin/students');
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  // Get all teachers
  Future<List<Map<String, dynamic>>> getAllTeachers() async {
    try {
      final response = await _apiClient.get('/api/admin/teachers');
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  // Get all class groups
  Future<List<Map<String, dynamic>>> getAllClassGroups() async {
    try {
      final response = await _apiClient.get('/api/admin/class-groups');
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  // Get all subjects
  Future<List<Map<String, dynamic>>> getAllSubjects() async {
    try {
      final response = await _apiClient.get('/api/admin/subjects');
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  // Get dashboard stats
  Future<Map<String, int>> getDashboardStats() async {
    final users = await getAllUsers();
    final students = await getAllStudents();
    final teachers = await getAllTeachers();
    final classes = await getAllClassGroups();

    return {
      'Total Users': users.length,
      'Students': students.length,
      'Teachers': teachers.length,
      'Classes': classes.length,
    };
  }

  // Change user password
  Future<void> changeUserPassword(
    int userId,
    String newPassword,
    String adminPassword,
    int adminId,
  ) async {
    await _apiClient.put(
      '/api/admin/users/$userId/password',
      data: {
        'newPassword': newPassword,
        'adminPassword': adminPassword,
        'adminId': adminId,
      },
    );
  }

  // Change user role
  Future<void> changeUserRole(int userId, String role) async {
    await _apiClient.put('/api/admin/users/$userId/role', data: {'role': role});
  }

  // Get all parents
  Future<List<Map<String, dynamic>>> getAllParents() async {
    try {
      final response = await _apiClient.get('/api/admin/parents');
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  // Simulate schedule
  Future<void> simulateSchedule(Map<String, dynamic> request) async {
    await _apiClient.post('/api/admin/simulation/schedule', data: request);
  }

  // Get all classrooms
  Future<List<Map<String, dynamic>>> getClassrooms() async {
    try {
      final response = await _apiClient.get('/api/admin/classrooms');
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  // Update classroom
  Future<void> updateClassroom(int id, Map<String, dynamic> data) async {
    await _apiClient.put('/api/admin/classrooms/$id', data: data);
  }
}
