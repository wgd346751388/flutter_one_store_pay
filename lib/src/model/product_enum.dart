import 'package:json_annotation/json_annotation.dart';

enum ProductType {
  @JsonValue('all')
  all,
  @JsonValue('inapp')
  inapp,
  @JsonValue('auto')
  auto,
}
enum PurchaseState {
  @JsonValue(-1)
  none,
  @JsonValue(0)
  purchased,
  @JsonValue(1)
  cancel,
  @JsonValue(2)
  refund,
}
