/// EduOps - Attendance Service
library;

import 'package:dio/dio.dart';
import '../core/constants/api_constants.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/attendance.dart';

class AttendanceService {
  final ApiClient _apiClient;

  AttendanceService(this._apiClient);

  Future<List<Attendance>> getAttendance() async {
    try {
      final response = await _apiClient.get(ApiConstants.attendance);
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Attendance.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<AttendanceStats> getAttendanceStats() async {
    try {
      final response = await _apiClient.get(ApiConstants.attendanceStats);
      return AttendanceStats.fromJson(response.data as Map<String, dynamic>);
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<Map<DateTime, AttendanceStatus>> getAttendanceCalendar() async {
    final attendance = await getAttendance();
    return {
      for (final a in attendance)
        DateTime(a.date.year, a.date.month, a.date.day): a.status,
    };
  }

  // Teacher methods
  Future<void> markAttendance({
    required int studentId,
    required int scheduleId,
    required AttendanceStatus status,
    String? notes,
  }) async {
    try {
      await _apiClient.post(
        ApiConstants.attendance,
        data: {
          'studentId': studentId,
          'scheduleId': scheduleId,
          'status': status.name.toUpperCase(),
          'notes': notes,
        },
      );
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
