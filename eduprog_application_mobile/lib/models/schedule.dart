/// EduOps - Schedule Model
library;

class Schedule {
  final int id;
  final String subjectName;
  final String teacherName;
  final String className;
  final String room;
  final int dayOfWeek; // 1 = Monday, 7 = Sunday
  final String startTime;
  final String endTime;

  Schedule({
    required this.id,
    required this.subjectName,
    required this.teacherName,
    required this.className,
    required this.room,
    required this.dayOfWeek,
    required this.startTime,
    required this.endTime,
  });

  factory Schedule.fromJson(Map<String, dynamic> json) {
    return Schedule(
      id: json['id'] as int,
      subjectName:
          json['subjectName'] as String? ??
          json['subject']?['name'] as String? ??
          '',
      teacherName: json['teacherName'] as String? ?? '',
      className:
          json['className'] as String? ??
          json['classGroup']?['name'] as String? ??
          '',
      room: json['room'] as String? ?? '',
      dayOfWeek: json['dayOfWeek'] as int? ?? 1,
      startTime: json['startTime'] as String? ?? '',
      endTime: json['endTime'] as String? ?? '',
    );
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
