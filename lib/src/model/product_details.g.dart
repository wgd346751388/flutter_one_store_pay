// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'product_details.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProductDetails _$ProductDetailsFromJson(Map<String, dynamic> json) {
  return ProductDetails(
    json['success'] as bool,
    json['msg'] as String,
    json['resultCode'] as int,
    (json['productDetails'] as List)
        ?.map((e) => e == null ? null : ProductDetail.fromJson(e))
        ?.toList(),
  );
}

Map<String, dynamic> _$ProductDetailsToJson(ProductDetails instance) =>
    <String, dynamic>{
      'success': instance.success,
      'msg': instance.msg,
      'resultCode': instance.resultCode,
      'productDetails': instance.productDetails,
    };

ProductDetail _$ProductDetailFromJson(Map<String, dynamic> json) {
  return ProductDetail(
    json['title'] as String,
    const ProductTypeConverter().fromJson(json['type'] as String) ??
        ProductType.all,
    json['price'] as String,
    json['priceCurrencyCode'] as String,
    json['priceAmountMicros'] as String,
    json['productId'] as String,
  );
}

Map<String, dynamic> _$ProductDetailToJson(ProductDetail instance) =>
    <String, dynamic>{
      'productId': instance.productId,
      'type': const ProductTypeConverter().toJson(instance.type),
      'price': instance.price,
      'priceCurrencyCode': instance.priceCurrencyCode,
      'priceAmountMicros': instance.priceAmountMicros,
      'title': instance.title,
    };
