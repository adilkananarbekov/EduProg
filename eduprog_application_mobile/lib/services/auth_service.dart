/// EduOps - Auth Service
library;

import 'package:dio/dio.dart';
import '../core/constants/api_constants.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/user.dart';
import '../data/local/local_database_service.dart';

class AuthService {
  final ApiClient _apiClient;
  final LocalDatabaseService _localDb = LocalDatabaseService();

  AuthService(this._apiClient);

  Future<AuthResponse> login(String email, String password) async {
    try {
      final response = await _apiClient.post(
        ApiConstants.login,
        data: LoginRequest(email: email, password: password).toJson(),
      );

      final authResponse = AuthResponse.fromJson(
        response.data as Map<String, dynamic>,
      );

      // Save tokens
      await _apiClient.saveToken(authResponse.token);
      if (authResponse.refreshToken != null) {
        await _apiClient.saveRefreshToken(authResponse.refreshToken!);
      }

      // Cache user
      await _localDb.cacheUser(authResponse.user);

      return authResponse;
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<void> logout() async {
    await _apiClient.clearTokens();
    await _localDb.clearAll();
  }

  Future<bool> isLoggedIn() async {
    return await _apiClient.hasToken();
  }

  Future<User?> getCurrentUser() async {
    // Try cache first
    final cachedUser = _localDb.getUser();

    // We can return cached user immediately, but we should also check API if possible to keep it fresh.
    // However, existing signature returns Future<User?>.
    // If we have cache, we return it.

    if (cachedUser != null) {
      // Fire and forget update
      _updateUserCache();
      return cachedUser;
    }

    try {
      final response = await _apiClient.get(ApiConstants.profile);
      final user = User.fromJson(response.data as Map<String, dynamic>);
      await _localDb.cacheUser(user);
      return user;
    } on DioException {
      return null;
    }
  }

  Future<void> _updateUserCache() async {
    try {
      final response = await _apiClient.get(ApiConstants.profile);
      final user = User.fromJson(response.data as Map<String, dynamic>);
      await _localDb.cacheUser(user);
    } catch (e) {
      // Ignore errors in background update
    }
  }

  Future<void> refreshToken() async {
    try {
      final refreshToken = await _apiClient.getRefreshToken();
      if (refreshToken == null) throw UnauthorizedException();

      final response = await _apiClient.post(
        ApiConstants.refresh,
        data: {'refreshToken': refreshToken},
      );

      final newToken = response.data['token'] as String;
      await _apiClient.saveToken(newToken);
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
