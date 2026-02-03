/// EduOps - Announcement Service
library;

import 'package:dio/dio.dart';
import '../core/constants/api_constants.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/announcement.dart';

class AnnouncementService {
  final ApiClient _apiClient;

  AnnouncementService(this._apiClient);

  Future<List<Announcement>> getAnnouncements() async {
    try {
      final response = await _apiClient.get(ApiConstants.announcements);
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => Announcement.fromJson(json as Map<String, dynamic>))
          .toList()
        ..sort((a, b) => b.createdAt.compareTo(a.createdAt)); // Newest first
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<Announcement>> getImportantAnnouncements() async {
    final announcements = await getAnnouncements();
    return announcements.where((a) => a.important).toList();
  }

  // Admin/Teacher methods
  Future<void> createAnnouncement({
    required String title,
    required String content,
    bool important = false,
    String? targetAudience,
  }) async {
    try {
      await _apiClient.post(
        ApiConstants.announcements,
        data: {
          'title': title,
          'content': content,
          'important': important,
          'targetAudience': targetAudience ?? 'ALL',
        },
      );
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
