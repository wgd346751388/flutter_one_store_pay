import 'package:json_annotation/json_annotation.dart';


import 'enum_converter.dart';
import 'product_enum.dart';
part 'product_details.g.dart';

@JsonSerializable(fieldRename: FieldRename.none)
class ProductDetails {
  ProductDetails(
    this.success,
    this.msg,
    this.resultCode,
    this.productDetails,
  );
  factory ProductDetails.fromJson(Map<String, dynamic> json) => _$ProductDetailsFromJson(json);
  Map<String, dynamic> toJson() => _$ProductDetailsToJson(this);

  bool success;
  String msg;
  int resultCode;

  List<ProductDetail> productDetails;

}

@JsonSerializable(fieldRename: FieldRename.none)
@ProductTypeConverter()
class ProductDetail {
  ProductDetail(
    this.title,
    this.type,
    this.price,
    this.priceCurrencyCode,
    this.priceAmountMicros,
    this.productId,
  );
  factory ProductDetail.fromJson(dynamic json) => _$ProductDetailFromJson(Map<String,dynamic>.from(json));
  Map<String, dynamic> toJson() => _$ProductDetailToJson(this);

  String productId;
  @JsonKey(defaultValue: ProductType.all)
  ProductType type;
  String price;
  String priceCurrencyCode;
  String priceAmountMicros;
  String title;
}
