// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'result_data.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ResultData _$ResultDataFromJson(Map<String, dynamic> json) {
  return ResultData(
    json['success'] as bool,
    json['msg'] as String,
    json['resultCode'] as int,
  );
}

Map<String, dynamic> _$ResultDataToJson(ResultData instance) =>
    <String, dynamic>{
      'success': instance.success,
      'msg': instance.msg,
      'resultCode': instance.resultCode,
    };
