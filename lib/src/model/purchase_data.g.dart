// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'purchase_data.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PurchaseData _$PurchaseDataFromJson(Map<String, dynamic> json) {
  return PurchaseData(
    json['success'] as bool,
    json['msg'] as String,
    json['resultCode'] as int,
    (json['purchases'] as List)
        ?.map((e) => e == null ? null : Purchase.fromJson(e))
        ?.toList(),
  );
}

Map<String, dynamic> _$PurchaseDataToJson(PurchaseData instance) =>
    <String, dynamic>{
      'success': instance.success,
      'msg': instance.msg,
      'resultCode': instance.resultCode,
      'purchases': instance.purchases,
    };

Purchase _$PurchaseFromJson(Map<String, dynamic> json) {
  return Purchase(
    json['productId'] as String,
    json['purchaseToken'] as String,
    json['acknowledgeState'] as bool,
    json['billingKey'] as String,
    json['developerPayload'] as String,
    json['orderId'] as String,
    json['originalJson'] as String,
    json['packageName'] as String,
    const PurchaseStateConverter().fromJson(json['purchaseState'] as int),
    json['purchaseTime'] as int,
    json['recurringState'] as int,
    json['signature'] as String,
  )..purchaseId = json['purchaseId'] as String;
}

Map<String, dynamic> _$PurchaseToJson(Purchase instance) => <String, dynamic>{
      'orderId': instance.orderId,
      'packageName': instance.packageName,
      'productId': instance.productId,
      'purchaseTime': instance.purchaseTime,
      'acknowledgeState': instance.acknowledgeState,
      'developerPayload': instance.developerPayload,
      'purchaseToken': instance.purchaseToken,
      'billingKey': instance.billingKey,
      'purchaseState':
          const PurchaseStateConverter().toJson(instance.purchaseState),
      'recurringState': instance.recurringState,
      'signature': instance.signature,
      'originalJson': instance.originalJson,
      'purchaseId': instance.purchaseId,
    };
