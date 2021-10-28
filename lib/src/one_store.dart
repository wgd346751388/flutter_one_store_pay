import 'dart:async';

import 'package:flutter/services.dart';

import '../one_store.dart';
import 'model/enum_converter.dart';

class OneStore {
  static OneStore get instance => _getOneStoreInstance();
  static OneStore _instance;

  static _getOneStoreInstance() {
    if (_instance != null) {
      return _instance;
    }
    return _instance = OneStore._();
  }

  OneStore._() {
    _channel.setMethodCallHandler(_handleMethod);
  }

  static const String METHOD_ONE_STORE_ENABLE_PENDING = "enable_pending";
  static const String METHOD_ONE_STORE_QUERY = "query";
  static const String METHOD_ONE_STORE_BUY = "buy";
  static const String METHOD_ONE_STORE_PURCHASE_UPDATED = "purchaseUpdated";
  static const String METHOD_ONE_STORE_CONSUME = "consumeAsync";
  static const String METHOD_ONE_STORE_ACKNOWLEDGE = "acknowledgeAsync";

  static const String METHOD_ONE_STORE_QUERY_PURCHASES = "queryPurchasesAsync";

  static const String ARGUMENT_KEY_PRODUCTS_IDS = "product_ids";
  static const String ARGUMENT_KEY_PRODUCTS_ID = "product_id";
  static const String ARGUMENT_KEY_PRODUCTS_TYPE = "product_type";
  static const String ARGUMENT_KEY_PURCHASE_STRING = "purchaseString";
  static const String ARGUMENT_KEY_PURCHASE_signature = "purchaseSignature";
  static const String ARGUMENT_KEY_PURCHASE_BILLING_KEY = "purchaseBillingKey";

  static const MethodChannel _channel = const MethodChannel('one_store');

  final StreamController<PurchaseData> _purchaseDataStreamController = StreamController<PurchaseData>.broadcast();

  Stream<PurchaseData> purchaseUpdatedStream() => _purchaseDataStreamController.stream;

  Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case METHOD_ONE_STORE_PURCHASE_UPDATED:
        Map<String, dynamic> data = call.arguments.cast<String, dynamic>();
        _purchaseDataStreamController.add(PurchaseData.fromJson(data));
        break;
    }
  }

  ///初始化
  Future<void> enablePendingPurchases() async {
  String val =   await _channel.invokeMethod(METHOD_ONE_STORE_ENABLE_PENDING);
  }

  ///查询产品信息
  ///[identifiers]代表产品id集合例：['com.price.299']
  Future<ProductDetails> queryProductDetails(List<String> identifiers) async {
    dynamic data = await _channel.invokeMethod(METHOD_ONE_STORE_QUERY, {
      ARGUMENT_KEY_PRODUCTS_IDS: identifiers,
    });
    return ProductDetails.fromJson(data.cast<String, dynamic>());
  }

  ///调起内购
  ///[productId]代表产品id
  ///[productType]产品类型：[ProductType.all],[ProductType.inapp],[ProductType.auto]
  Future<ResultData> buyProduct(String productId, ProductType productType) async {
    dynamic data = await _channel.invokeMethod<dynamic>(METHOD_ONE_STORE_BUY, {
      ARGUMENT_KEY_PRODUCTS_ID: productId,
      ARGUMENT_KEY_PRODUCTS_TYPE: ProductTypeConverter().toJson(productType),
    });
    return ResultData.fromJson(data.cast<String, dynamic>());
  }

  ///消耗型产品必须先消耗，否则无法进行下次内购
  Future<ResultData> consumePurchase(Purchase purchase) async {
    dynamic data = await _channel.invokeMethod<dynamic>(METHOD_ONE_STORE_CONSUME, {
      ARGUMENT_KEY_PURCHASE_STRING: purchase.originalJson,
      ARGUMENT_KEY_PURCHASE_signature: purchase.signature,
      ARGUMENT_KEY_PURCHASE_BILLING_KEY: purchase.billingKey,
    });
    return ResultData.fromJson(data.cast<String, dynamic>());
  }

  ///非消耗性产品，请使用
  ///购买包月型商品时，仅对首次支付确认购买即可
  ///PurchaseData对象包含显示是否已确认购买的isAcknowledged
  Future<ResultData> acknowledgeAsync(Purchase purchase) async {
    dynamic data = await _channel.invokeMethod<dynamic>(METHOD_ONE_STORE_ACKNOWLEDGE, {
      ARGUMENT_KEY_PURCHASE_STRING: purchase.originalJson,
      ARGUMENT_KEY_PURCHASE_signature: purchase.signature,
      ARGUMENT_KEY_PURCHASE_BILLING_KEY: purchase.billingKey,
    });
    return ResultData.fromJson(data.cast<String, dynamic>());
  }

  ///获取未消费的管理型商品和正在使用的包月型商品的信息。
  ///需要调用queryPurchaseAsync（）时
  /// 每次启动应用软件时，都会调用queryPastPurchases()，以便恢复自上次停止应用软件以来用户购买的所有历史记录。
  /// 引用软件在后台时，用户可进行购买，因此从onResume Messude调用queryPastPurchases（）。
  ///[productType]产品类型：[ProductType.all],[ProductType.inapp],[ProductType.auto]
  Future<PurchaseData> queryPastPurchases(ProductType productType) async {
    dynamic data = await _channel.invokeMethod<dynamic>(METHOD_ONE_STORE_QUERY_PURCHASES, {
      ARGUMENT_KEY_PRODUCTS_TYPE: ProductTypeConverter().toJson(productType),
    });
    return PurchaseData.fromJson(data.cast<String, dynamic>());
  }
}
