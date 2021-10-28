import 'package:json_annotation/json_annotation.dart';
import 'package:one_store/one_store.dart';

import 'enum_converter.dart';

part 'purchase_data.g.dart';

@JsonSerializable(fieldRename: FieldRename.none)
class PurchaseData {
  PurchaseData(
    this.success,
    this.msg,
    this.resultCode,
    this.purchases,
  );

  factory PurchaseData.fromJson(Map<String, dynamic> json) => _$PurchaseDataFromJson(json);

  Map<String, dynamic> toJson() => _$PurchaseDataToJson(this);

  bool success;
  String msg;
  int resultCode;
  List<Purchase> purchases;
}

@JsonSerializable(fieldRename: FieldRename.none)
class Purchase {
  Purchase(
    this.productId,
    this.purchaseToken,
    this.acknowledgeState,
    this.billingKey,
    this.developerPayload,
    this.orderId,
    this.originalJson,
    this.packageName,
    this.purchaseState,
    this.purchaseTime,
    this.recurringState,
    this.signature,
  );

  factory Purchase.fromJson(dynamic json) => _$PurchaseFromJson(Map<String, dynamic>.from(json));

  Map<String, dynamic> toJson() => _$PurchaseToJson(this);
  String orderId;
  String packageName;
  String productId;
  int purchaseTime;
  bool acknowledgeState;
  String developerPayload;
  String purchaseToken;
  String billingKey;
  @PurchaseStateConverter()
  PurchaseState purchaseState;
  int recurringState;
  String signature;
  String originalJson;
  @Deprecated('')
  String purchaseId;
}
