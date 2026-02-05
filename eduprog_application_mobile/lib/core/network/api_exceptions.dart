/// EduOps - API Exceptions
library;

class ApiException implements Exception {
  final String message;
  final int? statusCode;
  final dynamic data;

  ApiException({required this.message, this.statusCode, this.data});

  @override
  String toString() => 'ApiException: $message (status: $statusCode)';

  factory ApiException.fromDioError(dynamic error) {
    if (error.response != null) {
      final data = error.response?.data;
      String message = 'Something went wrong';

      if (data is Map) {
        message = data['message'] ?? data['error'] ?? message;
      }

      return ApiException(
        message: message,
        statusCode: error.response?.statusCode,
        data: data,
      );
    }

    if (error.type.toString().contains('connectionTimeout')) {
      return ApiException(message: 'Connection timeout');
    }

    if (error.type.toString().contains('receiveTimeout')) {
      return ApiException(message: 'Server took too long to respond');
    }

    return ApiException(message: error.message ?? 'Network error');
  }
}

class UnauthorizedException extends ApiException {
  UnauthorizedException()
    : super(message: 'Unauthorized - please login again', statusCode: 401);
}

class NotFoundException extends ApiException {
  NotFoundException(String resource)
    : super(message: '$resource not found', statusCode: 404);
}

class ValidationException extends ApiException {
  final Map<String, List<String>>? errors;

  ValidationException({super.message = 'Validation failed', this.errors})
    : super(statusCode: 422);
}
