/// EduOps - Schedule Service
library;

import 'package:dio/dio.dart';
import '../core/constants/api_constants.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/schedule.dart';
import '../data/repositories/base_repository.dart';
import '../data/local/local_database_service.dart';

class ScheduleService extends BaseRepository<Schedule> {
  final ApiClient _apiClient;

  ScheduleService(this._apiClient);

  // --- Private API Fetchers ---
  Future<List<Schedule>> _fetchWeekSchedule() async {
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

  // --- Stream Methods (Cache + Live) ---

  Stream<List<Schedule>> getWeekScheduleStream() {
    return getListStream(
      boxName: LocalDatabaseService.scheduleBox,
      key: 'week_schedule',
      apiCall: _fetchWeekSchedule,
    );
  }

  Stream<List<Schedule>> getTodayScheduleStream() {
    return getWeekScheduleStream().map((schedules) {
      final today = DateTime.now().weekday;
      return schedules.where((s) => s.dayOfWeek == today).toList()
        ..sort((a, b) => a.startTime.compareTo(b.startTime));
    });
  }

  // --- Future Methods (Backward Compatibility) ---
  // Returns the final result of the stream (fresh data if online, cache if offline)

  Future<List<Schedule>> getWeekSchedule() async {
    try {
      final stream = getWeekScheduleStream();
      if (await stream.isEmpty) return [];
      return await stream.last;
    } catch (e) {
      // If error occurs, try to return what we have or empty
      return [];
    }
  }

  Future<List<Schedule>> getTodaySchedule() async {
    try {
       final stream = getTodayScheduleStream();
       if (await stream.isEmpty) return [];
       return await stream.last;
    } catch (e) {
       return [];
    }
  }

  // --- Other Methods (Direct API for now, or could be cached similarly) ---

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
