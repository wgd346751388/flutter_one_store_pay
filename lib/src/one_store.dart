import 'dart:async';

import 'package:flutter/services.dart';

import 'model/product_details.dart';

class OneStore {
  static const String ALL = "all";
  static const String INAPP = "inapp";
  static const String AUTO = "auto";

  static const String METHOD_ONE_STORE_QUERY = "query";
  static const String METHOD_ONE_STORE_BUY = "buy";

  static const String ARGUMENT_KEY_PRODUCTS_IDS = "product_ids";
  static const String ARGUMENT_KEY_PRODUCTS_ID = "product_id";
  static const String ARGUMENT_KEY_PRODUCTS_TYPE = "product_type";

  static const MethodChannel _channel = const MethodChannel('one_store');

  OneStore() {
    _channel.setMethodCallHandler(_handleMethod);
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
    }
  }

  Future<ProductDetails> queryProductDetails(List<String> identifiers) async {
    Map<String, dynamic> data = await _channel.invokeMethod<Map<String, dynamic>>(METHOD_ONE_STORE_QUERY, {
      ARGUMENT_KEY_PRODUCTS_IDS: identifiers,
    });
    return ProductDetails.fromJson(data);
  }

  Future<void> buyConsumable(String productId,String productType) async {
    Map<String, dynamic> data = await _channel.invokeMethod<Map<String, dynamic>>(METHOD_ONE_STORE_BUY, {
      ARGUMENT_KEY_PRODUCTS_ID: productId,
      ARGUMENT_KEY_PRODUCTS_TYPE: productType,
    });
  }




}
