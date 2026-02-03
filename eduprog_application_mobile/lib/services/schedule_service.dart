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
}
