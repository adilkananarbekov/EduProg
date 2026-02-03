/// EduOps - Auth Service
library;

import 'package:dio/dio.dart';
import '../core/constants/api_constants.dart';
import '../core/network/api_client.dart';
import '../core/network/api_exceptions.dart';
import '../models/user.dart';

class AuthService {
  final ApiClient _apiClient;

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

      return authResponse;
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  Future<void> logout() async {
    await _apiClient.clearTokens();
  }

  Future<bool> isLoggedIn() async {
    return await _apiClient.hasToken();
  }

  Future<User?> getCurrentUser() async {
    try {
      final response = await _apiClient.get(ApiConstants.profile);
      return User.fromJson(response.data as Map<String, dynamic>);
    } on DioException {
      return null;
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
