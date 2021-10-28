import 'package:json_annotation/json_annotation.dart';

import 'product_enum.dart';

part 'enum_converter.g.dart';

class ProductTypeConverter implements JsonConverter<ProductType, String> {
  const ProductTypeConverter();

  @override
  ProductType fromJson(String json) {
    if (json == null) {
      return ProductType.all;
    }
    return _$enumDecode<ProductType>(_$ProductTypeEnumMap.cast<ProductType, dynamic>(), json);
  }

  @override
  String toJson(ProductType object) => _$ProductTypeEnumMap[object];
}

@JsonSerializable()
class ProductTypeEnum {
  ProductType productType;

}


class PurchaseStateConverter implements JsonConverter<PurchaseState, int>{
  const PurchaseStateConverter();
  @override
  PurchaseState fromJson(int json) {
    if (json == null) {
      return PurchaseState.none;
    }
    return _$enumDecode<PurchaseState>(_$PurchaseStateEnumMap.cast<PurchaseState, dynamic>(), json);
  }
  @override
  int toJson(PurchaseState object) => _$PurchaseStateEnumMap[object];
}

@JsonSerializable()
class PurchaseStateEnum {
  PurchaseState purchaseState;
}

