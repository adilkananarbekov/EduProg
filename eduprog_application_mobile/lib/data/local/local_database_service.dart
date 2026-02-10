/// EduOps - Local Database Service
library;

import 'package:hive_flutter/hive_flutter.dart';
import '../../models/schedule.dart';
import '../../models/grade.dart';
import '../../models/user.dart';

class LocalDatabaseService {
  static final LocalDatabaseService _instance = LocalDatabaseService._internal();

  factory LocalDatabaseService() {
    return _instance;
  }

  LocalDatabaseService._internal();

  static const String _userBox = 'user_box';
  static const String userKey = 'current_user';

  // Box names for other services
  static const String scheduleBox = 'schedule_box';
  static const String gradesBox = 'grades_box';

  Future<void> init() async {
    // Initialize Hive
    await Hive.initFlutter();

    // Register Adapters
    Hive.registerAdapter(ScheduleAdapter());
    Hive.registerAdapter(GradeAdapter());
    Hive.registerAdapter(UserAdapter());
    Hive.registerAdapter(UserRoleAdapter());

    // Open User Box immediately as it's needed for startup
    await Hive.openBox<User>(_userBox);
  }

  Future<Box<T>> openBox<T>(String boxName) async {
    if (Hive.isBoxOpen(boxName)) {
      return Hive.box<T>(boxName);
    }
    return await Hive.openBox<T>(boxName);
  }

  Future<void> clearAll() async {
    // Clear all data from known boxes
    await _clearBox(_userBox);
    await _clearBox(scheduleBox);
    await _clearBox(gradesBox);

    // Or delete everything from disk if we want to be thorough
    // await Hive.deleteFromDisk();
    // Ideally we just clear content so boxes remain usable without re-opening
  }

  Future<void> _clearBox(String boxName) async {
    if (Hive.isBoxOpen(boxName)) {
      await Hive.box(boxName).clear();
    } else {
      // If not open, open then clear
      // However, if we don't know the type, it's tricky.
      // But we know types for these boxes.
      // We can try opening as dynamic if generic
      try {
         final box = await Hive.openBox(boxName);
         await box.clear();
      } catch (e) {
        // Ignore if box doesn't exist or other error
      }
    }
  }

  Future<void> cacheUser(User user) async {
    final box = await openBox<User>(_userBox);
    await box.put(userKey, user);
  }

  User? getUser() {
    if (!Hive.isBoxOpen(_userBox)) return null;
    final box = Hive.box<User>(_userBox);
    return box.get(userKey);
  }
}
