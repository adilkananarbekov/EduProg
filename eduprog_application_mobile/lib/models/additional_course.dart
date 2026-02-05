/// EduOps - Additional Course Model
library;

class AdditionalCourse {
  final int id;
  final String name;
  final String? description;
  final String? instructor;
  final String? dayOfWeek;
  final String? startTime;
  final String? endTime;
  final String? room;
  final int? maxCapacity;
  final int? enrolledCount;

  AdditionalCourse({
    required this.id,
    required this.name,
    this.description,
    this.instructor,
    this.dayOfWeek,
    this.startTime,
    this.endTime,
    this.room,
    this.maxCapacity,
    this.enrolledCount,
  });

  factory AdditionalCourse.fromJson(Map<String, dynamic> json) {
    return AdditionalCourse(
      id: json['id'] as int,
      name: json['name'] as String,
      description: json['description'] as String?,
      instructor: json['instructor'] as String?,
      dayOfWeek: json['dayOfWeek'] as String?,
      startTime: json['startTime'] as String?,
      endTime: json['endTime'] as String?,
      room: json['room'] as String?,
      maxCapacity: json['maxCapacity'] as int?,
      enrolledCount: json['enrolledCount'] as int?,
    );
  }

  String get schedule {
    if (dayOfWeek != null && startTime != null && endTime != null) {
      return '$dayOfWeek ${startTime!.substring(0, 5)} - ${endTime!.substring(0, 5)}';
    }
    return 'Schedule TBD';
  }
}
