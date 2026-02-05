/// EduOps - Subject Model
library;

class Subject {
  final int id;
  final String name;
  final String? description;
  final int? hoursPerWeek;

  Subject({
    required this.id,
    required this.name,
    this.description,
    this.hoursPerWeek,
  });

  factory Subject.fromJson(Map<String, dynamic> json) {
    return Subject(
      id: json['id'] as int,
      name: json['name'] as String,
      description: json['description'] as String?,
      hoursPerWeek: json['hoursPerWeek'] as int?,
    );
  }
}
