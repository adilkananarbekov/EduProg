/// EduOps - Grade Model
library;

class Grade {
  final int id;
  final String subjectName;
  final double value;
  final double maxValue;
  final String type; // QUIZ, EXAM, HOMEWORK, PROJECT
  final String? description;
  final String teacherName;
  final DateTime date;

  Grade({
    required this.id,
    required this.subjectName,
    required this.value,
    required this.maxValue,
    required this.type,
    this.description,
    required this.teacherName,
    required this.date,
  });

  double get percentage => maxValue > 0 ? (value / maxValue) * 100 : 0;

  String get grade {
    final pct = percentage;
    if (pct >= 90) return 'A';
    if (pct >= 80) return 'B';
    if (pct >= 70) return 'C';
    if (pct >= 60) return 'D';
    return 'F';
  }

  factory Grade.fromJson(Map<String, dynamic> json) {
    return Grade(
      id: json['id'] as int,
      subjectName:
          json['subjectName'] as String? ??
          json['subject']?['name'] as String? ??
          '',
      value: (json['value'] as num?)?.toDouble() ?? 0,
      maxValue: (json['maxValue'] as num?)?.toDouble() ?? 100,
      type: json['type'] as String? ?? 'UNKNOWN',
      description: json['description'] as String?,
      teacherName: json['teacherName'] as String? ?? '',
      date: json['date'] != null
          ? DateTime.parse(json['date'] as String)
          : DateTime.now(),
    );
  }
}

class SubjectGrades {
  final String subjectName;
  final List<Grade> grades;

  SubjectGrades({required this.subjectName, required this.grades});

  double get average {
    if (grades.isEmpty) return 0;
    final sum = grades.fold<double>(0, (sum, g) => sum + g.percentage);
    return sum / grades.length;
  }
}
