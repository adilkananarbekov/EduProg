/// EduOps - Schedule Service
library;

import 'package:dio/dio.dart';
import '../core/constants/api_constants.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/schedule.dart';

class ScheduleService {
  final ApiClient _apiClient;

  ScheduleService(this._apiClient);

  Future<List<Schedule>> getWeekSchedule() async {
    try {
      final response = await _apiClient.get(ApiConstants.scheduleWeek);
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Schedule.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<Schedule>> getTodaySchedule() async {
    final weekSchedule = await getWeekSchedule();
    final today = DateTime.now().weekday;
    return weekSchedule.where((s) => s.dayOfWeek == today).toList()
      ..sort((a, b) => a.startTime.compareTo(b.startTime));
  }

  Future<List<Schedule>> getAllSchedules() async {
    try {
      final response = await _apiClient.get(ApiConstants.schedule);
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Schedule.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<Schedule>> getScheduleByClass(int classGroupId) async {
    try {
      final response = await _apiClient.get(
        '${ApiConstants.scheduleByClass}/$classGroupId',
      );
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Schedule.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<Schedule>> getScheduleByTeacher(int teacherId) async {
    try {
      final response = await _apiClient.get(
        '${ApiConstants.scheduleByTeacher}/$teacherId',
      );
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Schedule.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<Schedule>> getScheduleByClassroom(int classroomId) async {
    try {
      final response = await _apiClient.get(
        '${ApiConstants.scheduleByClassroom}/$classroomId',
      );
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Schedule.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  // Public Entity Fetchers for Dropdowns
  Future<List<Map<String, dynamic>>> getPublicTeachers() async {
    try {
      final response = await _apiClient.get(ApiConstants.commonTeachers);
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  Future<List<Map<String, dynamic>>> getPublicClasses() async {
    try {
      final response = await _apiClient.get(ApiConstants.commonClasses);
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  Future<List<Map<String, dynamic>>> getPublicClassrooms() async {
    try {
      // Note: Using the admin endpoint which we made public for GET
      final response = await _apiClient.get(ApiConstants.commonClassrooms);
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  Future<List<Map<String, dynamic>>> getPublicSubjects() async {
    try {
      final response = await _apiClient.get(ApiConstants.adminSubjects);
      return List<Map<String, dynamic>>.from(response.data as List);
    } on DioException {
      return [];
    }
  }

  Future<void> createSchedule(Map<String, dynamic> data) async {
    try {
      await _apiClient.post(ApiConstants.schedule, data: data);
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
