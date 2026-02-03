/// EduOps - Announcement Model
library;

class Announcement {
  final int id;
  final String title;
  final String content;
  final String authorName;
  final DateTime createdAt;
  final bool important;
  final String? targetAudience; // ALL, STUDENTS, TEACHERS

  Announcement({
    required this.id,
    required this.title,
    required this.content,
    required this.authorName,
    required this.createdAt,
    this.important = false,
    this.targetAudience,
  });

  factory Announcement.fromJson(Map<String, dynamic> json) {
    return Announcement(
      id: json['id'] as int,
      title: json['title'] as String,
      content: json['content'] as String,
      authorName: json['authorName'] as String? ?? 'Admin',
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : DateTime.now(),
      important: json['important'] as bool? ?? false,
      targetAudience: json['targetAudience'] as String?,
    );
  }
}
