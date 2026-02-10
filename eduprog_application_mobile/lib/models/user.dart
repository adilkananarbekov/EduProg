/// EduOps - User Model
library;

import 'package:hive/hive.dart';

part 'user.g.dart';

@HiveType(typeId: 3)
enum UserRole {
  @HiveField(0)
  student,
  @HiveField(1)
  teacher,
  @HiveField(2)
  admin,
  @HiveField(3)
  operator
}

@HiveType(typeId: 2)
class User {
  @HiveField(0)
  final int id;
  @HiveField(1)
  final String email;
  @HiveField(2)
  final String firstName;
  @HiveField(3)
  final String lastName;
  @HiveField(4)
  final UserRole role;
  @HiveField(5)
  final bool enabled;

  User({
    required this.id,
    required this.email,
    required this.firstName,
    required this.lastName,
    required this.role,
    this.enabled = true,
  });

  String get fullName => '$firstName $lastName';

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      // Handle both 'id' and 'userId' from API
      id: (json['id'] ?? json['userId']) as int,
      email: json['email'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      role: _parseRole(json['role'] as String),
      enabled: json['enabled'] as bool? ?? true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'firstName': firstName,
      'lastName': lastName,
      'role': role.name.toUpperCase(),
      'enabled': enabled,
    };
  }

  static UserRole _parseRole(String role) {
    switch (role.toUpperCase()) {
      case 'ADMIN':
        return UserRole.admin;
      case 'TEACHER':
        return UserRole.teacher;
      case 'OPERATOR':
        return UserRole.operator;
      case 'STUDENT':
      default:
        return UserRole.student;
    }
  }
}

class AuthResponse {
  final String token;
  final String? refreshToken;
  final User user;

  AuthResponse({required this.token, this.refreshToken, required this.user});

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    // Handle both nested 'user' object and flat response structure
    final userData = json['user'] as Map<String, dynamic>? ?? json;

    return AuthResponse(
      token: json['token'] as String,
      refreshToken: json['refreshToken'] as String?,
      user: User.fromJson(userData),
    );
  }
}

class LoginRequest {
  final String email;
  final String password;

  LoginRequest({required this.email, required this.password});

  Map<String, dynamic> toJson() {
    return {'email': email, 'password': password};
  }
}
