/// EduOps - Attendance Model
library;

enum AttendanceStatus { present, absent, late, excused }

class Attendance {
  final int id;
  final DateTime date;
  final AttendanceStatus status;
  final String? subjectName;
  final String? notes;

  Attendance({
    required this.id,
    required this.date,
    required this.status,
    this.subjectName,
    this.notes,
  });

  factory Attendance.fromJson(Map<String, dynamic> json) {
    return Attendance(
      id: json['id'] as int,
      date: DateTime.parse(json['date'] as String),
      status: _parseStatus(json['status'] as String),
      subjectName: json['subjectName'] as String?,
      notes: json['notes'] as String?,
    );
  }

  static AttendanceStatus _parseStatus(String status) {
    switch (status.toUpperCase()) {
      case 'PRESENT':
        return AttendanceStatus.present;
      case 'ABSENT':
        return AttendanceStatus.absent;
      case 'LATE':
        return AttendanceStatus.late;
      case 'EXCUSED':
        return AttendanceStatus.excused;
      default:
        return AttendanceStatus.present;
    }
  }
}

class AttendanceStats {
  final int total;
  final int present;
  final int absent;
  final int late;
  final int excused;

  AttendanceStats({
    required this.total,
    required this.present,
    required this.absent,
    required this.late,
    required this.excused,
  });

  double get attendancePercentage =>
      total > 0 ? ((present + late) / total) * 100 : 100;

  factory AttendanceStats.fromJson(Map<String, dynamic> json) {
    return AttendanceStats(
      total: json['total'] as int? ?? 0,
      present: json['present'] as int? ?? 0,
      absent: json['absent'] as int? ?? 0,
      late: json['late'] as int? ?? 0,
      excused: json['excused'] as int? ?? 0,
    );
  }
}
