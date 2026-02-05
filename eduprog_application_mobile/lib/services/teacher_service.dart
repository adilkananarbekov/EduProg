/// EduOps - Teacher Service
library;

import 'package:dio/dio.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/teacher.dart';
import '../models/schedule.dart';

class TeacherService {
  final ApiClient _apiClient;

  TeacherService(this._apiClient);

  Future<List<Teacher>> getAllTeachers() async {
    try {
      final response = await _apiClient.get('/api/teachers');
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Teacher.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<Teacher> getTeacherById(int id) async {
    try {
      final response = await _apiClient.get('/api/teachers/$id');
      return Teacher.fromJson(response.data as Map<String, dynamic>);
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<Schedule>> getTeacherSchedule(int teacherId) async {
    try {
      final response = await _apiClient.get(
        '/api/teachers/$teacherId/schedule',
      );
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Schedule.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<Schedule>> getClassSchedule(int classId) async {
    try {
      final response = await _apiClient.get('/api/schedule/class/$classId');
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Schedule.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
