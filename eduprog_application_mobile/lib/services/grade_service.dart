/// EduOps - Grade Service
library;

import 'package:dio/dio.dart';
import '../core/constants/api_constants.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/grade.dart';

class GradeService {
  final ApiClient _apiClient;

  GradeService(this._apiClient);

  Future<List<Grade>> getGrades() async {
    try {
      final response = await _apiClient.get(ApiConstants.grades);
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Grade.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<SubjectGrades>> getGradesBySubject() async {
    final grades = await getGrades();

    // Group by subject
    final Map<String, List<Grade>> grouped = {};
    for (final grade in grades) {
      grouped.putIfAbsent(grade.subjectName, () => []).add(grade);
    }

    return grouped.entries
        .map((e) => SubjectGrades(subjectName: e.key, grades: e.value))
        .toList();
  }

  Future<double> getOverallAverage() async {
    final grades = await getGrades();
    if (grades.isEmpty) return 0;

    final sum = grades.fold<double>(0, (sum, g) => sum + g.percentage);
    return sum / grades.length;
  }

  // Teacher methods
  Future<void> addGrade({
    required int studentId,
    required int subjectId,
    required double value,
    required double maxValue,
    required String type,
    String? description,
  }) async {
    try {
      await _apiClient.post(
        ApiConstants.grades,
        data: {
          'studentId': studentId,
          'subjectId': subjectId,
          'value': value,
          'maxValue': maxValue,
          'type': type,
          'description': description,
        },
      );
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
