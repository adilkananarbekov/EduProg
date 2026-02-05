/// EduOps - Notification Service
library;

import 'package:dio/dio.dart';
import '../core/constants/api_constants.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/notification.dart';

class NotificationService {
  final ApiClient _apiClient;

  NotificationService(this._apiClient);

  Future<List<AppNotification>> getNotifications() async {
    try {
      final response = await _apiClient.get(ApiConstants.notifications);
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => AppNotification.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<List<AppNotification>> getUnreadNotifications() async {
    try {
      final response = await _apiClient.get(
        '${ApiConstants.notifications}/unread',
      );
      final List<dynamic> data = response.data as List<dynamic>;
      return data
          .map((json) => AppNotification.fromJson(json as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<int> getUnreadCount() async {
    try {
      final response = await _apiClient.get(
        '${ApiConstants.notifications}/unread/count',
      );
      return response.data as int;
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<void> markAsRead(int notificationId) async {
    try {
      await _apiClient.put(
        '${ApiConstants.notifications}/$notificationId/read',
      );
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<void> markAllAsRead() async {
    try {
      await _apiClient.put('${ApiConstants.notifications}/read-all');
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<void> deleteNotification(int notificationId) async {
    try {
      await _apiClient.delete('${ApiConstants.notifications}/$notificationId');
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
