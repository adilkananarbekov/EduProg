/// EduOps - Base Repository
library;

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/foundation.dart';
import '../local/local_database_service.dart';

abstract class BaseRepository<T> {
  final LocalDatabaseService _localDb = LocalDatabaseService();

  /// Generic method to implement "Cache-then-Update" strategy
  /// [boxName] - Name of the Hive box
  /// [key] - Key to store/retrieve the data
  /// [apiCall] - Function that performs the API request
  /// [transform] - Optional function to transform cached data (e.g. List<dynamic> to List<T>)
  Stream<List<T>> getListStream({
    required String boxName,
    required String key,
    required Future<List<T>> Function() apiCall,
  }) async* {
    // 1. Open Box
    // We open as dynamic because we store Lists, and generic Box<List<T>> is hard to express in Hive openBox
    final box = await _localDb.openBox(boxName);

    // 2. Emit cached data immediately
    if (box.containsKey(key)) {
      try {
        final dynamic data = box.get(key);
        if (data is List) {
          // Hive returns List<dynamic>, we need to cast elements
          final List<T> typedList = data.cast<T>().toList();
          if (typedList.isNotEmpty) {
             debugPrint('BaseRepository: Emitting cached data for $key');
             yield typedList;
          }
        }
      } catch (e) {
        debugPrint('BaseRepository: Error reading cache for $key: $e');
      }
    }

    // 3. Check connectivity
    final connectivityResult = await Connectivity().checkConnectivity();
    final isOffline = connectivityResult.contains(ConnectivityResult.none);

    if (isOffline) {
      debugPrint('BaseRepository: Offline, skipping API call for $key');
      return;
    }

    // 4. Fetch from API
    try {
      debugPrint('BaseRepository: Fetching fresh data for $key');
      final freshData = await apiCall();

      // 5. Update Cache
      await box.put(key, freshData);

      // 6. Emit new data
      yield freshData;
    } catch (e) {
      debugPrint('BaseRepository: API Error for $key: $e');
      // We don't yield error here to keep showing cached data
      // If the caller wants to handle errors, they might need a different return type (e.g. Stream<Result<T>>)
      // But for this task, we just swallow error and keep cache visible
    }
  }
}
