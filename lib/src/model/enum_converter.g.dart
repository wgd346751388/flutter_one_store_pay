// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'enum_converter.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProductTypeEnum _$ProductTypeEnumFromJson(Map<String, dynamic> json) {
  return ProductTypeEnum()
    ..productType =
        _$enumDecodeNullable(_$ProductTypeEnumMap, json['productType']);
}

Map<String, dynamic> _$ProductTypeEnumToJson(ProductTypeEnum instance) =>
    <String, dynamic>{
      'productType': _$ProductTypeEnumMap[instance.productType],
    };

T _$enumDecode<T>(
  Map<T, dynamic> enumValues,
  dynamic source, {
  T unknownValue,
}) {
  if (source == null) {
    throw ArgumentError('A value must be provided. Supported values: '
        '${enumValues.values.join(', ')}');
  }

  final value = enumValues.entries
      .singleWhere((e) => e.value == source, orElse: () => null)
      ?.key;

  if (value == null && unknownValue == null) {
    throw ArgumentError('`$source` is not one of the supported values: '
        '${enumValues.values.join(', ')}');
  }
  return value ?? unknownValue;
}

T _$enumDecodeNullable<T>(
  Map<T, dynamic> enumValues,
  dynamic source, {
  T unknownValue,
}) {
  if (source == null) {
    return null;
  }
  return _$enumDecode<T>(enumValues, source, unknownValue: unknownValue);
}

const _$ProductTypeEnumMap = {
  ProductType.all: 'all',
  ProductType.inapp: 'inapp',
  ProductType.auto: 'auto',
};

PurchaseStateEnum _$PurchaseStateEnumFromJson(Map<String, dynamic> json) {
  return PurchaseStateEnum()
    ..purchaseState =
        _$enumDecodeNullable(_$PurchaseStateEnumMap, json['purchaseState']);
}

Map<String, dynamic> _$PurchaseStateEnumToJson(PurchaseStateEnum instance) =>
    <String, dynamic>{
      'purchaseState': _$PurchaseStateEnumMap[instance.purchaseState],
    };

const _$PurchaseStateEnumMap = {
  PurchaseState.none: -1,
  PurchaseState.purchased: 0,
  PurchaseState.cancel: 1,
  PurchaseState.refund: 2,
};
