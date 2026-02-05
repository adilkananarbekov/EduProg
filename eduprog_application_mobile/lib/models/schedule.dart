/// EduOps - Schedule Model
library;

class Schedule {
  final int id;
  final String subjectName;
  final String? subjectShortName; // Short name for compact UI (e.g., "Math")
  final String teacherName;
  final String className;
  final String room;
  final int dayOfWeek; // 1 = Monday, 7 = Sunday
  final String startTime;
  final String endTime;
  final int lessonNumber;

  Schedule({
    required this.id,
    required this.subjectName,
    this.subjectShortName,
    required this.teacherName,
    required this.className,
    required this.room,
    required this.dayOfWeek,
    required this.startTime,
    required this.endTime,
    required this.lessonNumber,
  });

  factory Schedule.fromJson(Map<String, dynamic> json) {
    return Schedule(
      id: json['id'] as int,
      subjectName:
          json['subjectName'] as String? ??
          json['subject']?['name'] as String? ??
          '',
      subjectShortName: json['subjectShortName'] as String?,
      teacherName: json['teacherName'] as String? ?? '',
      className:
          json['className'] as String? ??
          json['classGroup']?['name'] as String? ??
          '',
      room: json['room'] as String? ?? '',
      dayOfWeek: _parseDayOfWeek(json['dayOfWeek']),
      startTime: json['startTime'] as String? ?? '',
      endTime: json['endTime'] as String? ?? '',
      lessonNumber: json['lessonNumber'] as int? ?? 0,
    );
  }

  static int _parseDayOfWeek(dynamic value) {
    if (value is int) return value;
    if (value is String) {
      switch (value.toUpperCase()) {
        case 'MONDAY':
          return 1;
        case 'TUESDAY':
          return 2;
        case 'WEDNESDAY':
          return 3;
        case 'THURSDAY':
          return 4;
        case 'FRIDAY':
          return 5;
        case 'SATURDAY':
          return 6;
        case 'SUNDAY':
          return 7;
      }
    }
    return 1; // Default to Monday
  }

  // Alias for compatibility
  String get classGroupName => className;

  /// Display name for UI - uses shortName if available, otherwise first 4 chars
  String get displaySubjectName {
    if (subjectShortName != null && subjectShortName!.isNotEmpty) {
      return subjectShortName!;
    }
    // Fallback: use first 4 characters if no short name
    if (subjectName.length <= 4) {
      return subjectName;
    }
    return subjectName.substring(0, 4);
  }

  String get dayName {
    const days = [
      'Monday',
      'Tuesday',
      'Wednesday',
      'Thursday',
      'Friday',
      'Saturday',
      'Sunday',
    ];
    return days[(dayOfWeek - 1) % 7];
  }

  String get timeRange => '$startTime - $endTime';
}

class WeekSchedule {
  final List<Schedule> monday;
  final List<Schedule> tuesday;
  final List<Schedule> wednesday;
  final List<Schedule> thursday;
  final List<Schedule> friday;
  final List<Schedule> saturday;
  final List<Schedule> sunday;

  WeekSchedule({
    this.monday = const [],
    this.tuesday = const [],
    this.wednesday = const [],
    this.thursday = const [],
    this.friday = const [],
    this.saturday = const [],
    this.sunday = const [],
  });

  factory WeekSchedule.fromList(List<Schedule> schedules) {
    return WeekSchedule(
      monday: schedules.where((s) => s.dayOfWeek == 1).toList(),
      tuesday: schedules.where((s) => s.dayOfWeek == 2).toList(),
      wednesday: schedules.where((s) => s.dayOfWeek == 3).toList(),
      thursday: schedules.where((s) => s.dayOfWeek == 4).toList(),
      friday: schedules.where((s) => s.dayOfWeek == 5).toList(),
      saturday: schedules.where((s) => s.dayOfWeek == 6).toList(),
      sunday: schedules.where((s) => s.dayOfWeek == 7).toList(),
    );
  }

  List<Schedule> getForDay(int dayOfWeek) {
    switch (dayOfWeek) {
      case 1:
        return monday;
      case 2:
        return tuesday;
      case 3:
        return wednesday;
      case 4:
        return thursday;
      case 5:
        return friday;
      case 6:
        return saturday;
      case 7:
        return sunday;
      default:
        return [];
    }
  }
}
