/// EduOps - Notification Model
library;

enum NotificationType {
  info,
  grade,
  homework,
  attendance,
  announcement,
  event,
  message,
  system,
  warning,
}

class AppNotification {
  final int id;
  final String title;
  final String message;
  final NotificationType type;
  final bool isRead;
  final String? referenceType;
  final int? referenceId;
  final DateTime? readAt;
  final DateTime createdAt;

  AppNotification({
    required this.id,
    required this.title,
    required this.message,
    required this.type,
    required this.isRead,
    this.referenceType,
    this.referenceId,
    this.readAt,
    required this.createdAt,
  });

  factory AppNotification.fromJson(Map<String, dynamic> json) {
    return AppNotification(
      id: json['id'] as int,
      title: json['title'] as String,
      message: json['message'] as String,
      type: _parseType(json['type'] as String),
      isRead: json['isRead'] as bool? ?? false,
      referenceType: json['referenceType'] as String?,
      referenceId: json['referenceId'] as int?,
      readAt: json['readAt'] != null ? DateTime.parse(json['readAt']) : null,
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  static NotificationType _parseType(String type) {
    switch (type.toUpperCase()) {
      case 'GRADE':
        return NotificationType.grade;
      case 'HOMEWORK':
        return NotificationType.homework;
      case 'ATTENDANCE':
        return NotificationType.attendance;
      case 'ANNOUNCEMENT':
        return NotificationType.announcement;
      case 'EVENT':
        return NotificationType.event;
      case 'MESSAGE':
        return NotificationType.message;
      case 'SYSTEM':
        return NotificationType.system;
      case 'WARNING':
        return NotificationType.warning;
      default:
        return NotificationType.info;
    }
  }

  String get timeAgo {
    final now = DateTime.now();
    final difference = now.difference(createdAt);

    if (difference.inDays > 7) {
      return '${createdAt.day}/${createdAt.month}/${createdAt.year}';
    } else if (difference.inDays > 0) {
      return '${difference.inDays}d ago';
    } else if (difference.inHours > 0) {
      return '${difference.inHours}h ago';
    } else if (difference.inMinutes > 0) {
      return '${difference.inMinutes}m ago';
    } else {
      return 'Just now';
    }
  }
}
