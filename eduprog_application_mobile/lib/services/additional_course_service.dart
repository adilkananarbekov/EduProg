/// EduOps - Additional Course Service
library;

import 'package:dio/dio.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/additional_course.dart';

class AdditionalCourseService {
  final ApiClient _apiClient;

  AdditionalCourseService(this._apiClient);

  Future<List<AdditionalCourse>> getAllCourses() async {
    try {
      final response = await _apiClient.get('/api/courses');
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map(
            (json) => AdditionalCourse.fromJson(json as Map<String, dynamic>),
          )
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<AdditionalCourse>> getMyCourses() async {
    try {
      final response = await _apiClient.get('/api/courses/my');
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map(
            (json) => AdditionalCourse.fromJson(json as Map<String, dynamic>),
          )
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<void> enrollInCourse(int courseId) async {
    try {
      await _apiClient.post('/api/courses/$courseId/enroll');
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<void> unenrollFromCourse(int courseId) async {
    try {
      await _apiClient.delete('/api/courses/$courseId/enroll');
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
