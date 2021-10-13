// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'product_details.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProductDetails _$ProductDetailsFromJson(Map<String, dynamic> json) {
  return ProductDetails(
    json['success'] as bool,
    json['msg'] as String,
    (json['product_details'] as List)
        ?.map((e) => e == null
            ? null
            : ProductDetail.fromJson(e as Map<String, dynamic>))
        ?.toList(),
  );
}

Map<String, dynamic> _$ProductDetailsToJson(ProductDetails instance) =>
    <String, dynamic>{
      'success': instance.success,
      'msg': instance.msg,
      'product_details': instance.productDetails,
    };

ProductDetail _$ProductDetailFromJson(Map<String, dynamic> json) {
  return ProductDetail(
    json['title'] as String,
    json['type'] as String,
    json['price'] as String,
    json['price_currency_code'] as String,
    json['price_amount_micros'] as String,
    json['product_id'] as String,
  );
}

Map<String, dynamic> _$ProductDetailToJson(ProductDetail instance) =>
    <String, dynamic>{
      'product_id': instance.productId,
      'type': instance.type,
      'price': instance.price,
      'price_currency_code': instance.priceCurrencyCode,
      'price_amount_micros': instance.priceAmountMicros,
      'title': instance.title,
    };
