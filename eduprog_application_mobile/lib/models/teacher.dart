/// EduOps - Teacher Model
library;

import 'subject.dart';

class Teacher {
  final int id;
  final int userId;
  final String firstName;
  final String lastName;
  final String fullName;
  final String email;
  final String? employeeNumber;
  final List<Subject> subjects;

  Teacher({
    required this.id,
    required this.userId,
    required this.firstName,
    required this.lastName,
    required this.fullName,
    required this.email,
    this.employeeNumber,
    required this.subjects,
  });

  factory Teacher.fromJson(Map<String, dynamic> json) {
    return Teacher(
      id: json['id'] as int,
      userId: json['userId'] as int,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      fullName:
          json['fullName'] as String? ??
          '${json['firstName']} ${json['lastName']}',
      email: json['email'] as String,
      employeeNumber: json['employeeNumber'] as String?,
      subjects:
          (json['subjects'] as List<dynamic>?)
              ?.map((s) => Subject.fromJson(s as Map<String, dynamic>))
              .toList() ??
          [],
    );
  }

  String get subjectsString => subjects.map((s) => s.name).join(', ');
}
