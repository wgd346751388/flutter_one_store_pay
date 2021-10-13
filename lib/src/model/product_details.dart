import 'package:json_annotation/json_annotation.dart';

part 'product_details.g.dart';

@JsonSerializable(fieldRename: FieldRename.snake)
class ProductDetails {
  ProductDetails(
    this.success,
    this.msg,
    this.productDetails,
  );
  factory ProductDetails.fromJson(Map<String, dynamic> json) => _$ProductDetailsFromJson(json);
  Map<String, dynamic> toJson() => _$ProductDetailsToJson(this);

  bool success;
  String msg;

  List<ProductDetail> productDetails;

}

@JsonSerializable(fieldRename: FieldRename.snake)
class ProductDetail {
  ProductDetail(
    this.title,
    this.type,
    this.price,
    this.priceCurrencyCode,
    this.priceAmountMicros,
    this.productId,
  );
  factory ProductDetail.fromJson(Map<String, dynamic> json) => _$ProductDetailFromJson(json);
  Map<String, dynamic> toJson() => _$ProductDetailToJson(this);

  String productId;
  String type;
  String price;
  String priceCurrencyCode;
  String priceAmountMicros;
  String title;
}
