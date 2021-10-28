import 'package:json_annotation/json_annotation.dart';

part 'result_data.g.dart';

@JsonSerializable(fieldRename: FieldRename.none)
class ResultData {
  ResultData(this.success, this.msg, this.resultCode);

  factory ResultData.fromJson(Map<String, dynamic> json) => _$ResultDataFromJson(json);

  Map<String, dynamic> toJson() => _$ResultDataToJson(this);
  bool success;
  String msg;
  int resultCode;
}

class ResultCode {
  static const int RESULT_NO_FOUND_ITEM = -1;//未找到商品
  static const int RESULT_PRODUCT_ID_NULL = -2;//产品id不可唯恐
  static const int RESULT_VALIDATION_FAILED = 1111; //base64验证失败
  ///以上都是新增的
  ///下面是系统返回的
  static const int RESULT_OK = 0; //成功
  static const int RESULT_USER_CANCELED = 1; //已取消支付。
  static const int RESULT_SERVICE_UNAVAILABLE = 2; //终端或服务器网络发生错误。
  static const int RESULT_BILLING_UNAVAILABLE = 3; //购买处理过程中发生错误。
  static const int RESULT_ITEM_UNAVAILABLE = 4; //商品不在销售中或处于无法购买的状态。
  static const int RESULT_DEVELOPER_ERROR = 5; //是不正确的请求。
  static const int RESULT_ERROR = 6; //发生未定义的其他错误。
  static const int RESULT_ITEM_ALREADY_OWNED = 7; //已经拥有了item。
  static const int RESULT_ITEM_NOT_OWNED = 8; //因为不拥有item，所以不能消费。
  static const int RESULT_FAIL = 9; //支付失败。 请确认能否支付及确认支付方式后重新支付。
  static const int RESULT_NEED_LOGIN = 10; //需要登录store应用软件。
  static const int RESULT_NEED_UPDATE = 11; //需要更新支付模块。
  static const int RESULT_SECURITY_ERROR = 12; //非正常应用软件请求支付。
  static const int RESULT_BLOCKED_APP = 13; //请求已被阻止。
  static const int RESULT_NOT_SUPPORT_SANDBOX = 14; //在测试环境下不支持的功能。
  static const int ERROR_DATA_PARSING = 1001; //出现了响应数据解析错误。
  static const int ERROR_SIGNATURE_VERIFICATION = 1002; //购买信息的签名验证出错。
  static const int ERROR_ILLEGAL_ARGUMENT = 1003; //已输入不正常参数。
  static const int ERROR_UNDEFINED_CODE = 1004; //发生了未定义的错误。
  static const int ERROR_SIGNATURE_NOT_VALIDATION = 1005; //输入的许可证密钥无效。
  static const int ERROR_UPDATE_OR_INSTALL = 1006; //安装支付模块失败。
  static const int ERROR_SERVICE_DISCONNECTED = 1007; //支付模块的连接被断开。
  static const int ERROR_FEATURE_NOT_SUPPORTED = 1008; //	不支持的功能。
  static const int ERROR_SERVICE_TIMEOUT = 1008; //	与服务通信超时。
  static const int RESULT_EMERGENCY_ERROR = 99999; //	正在检查服务器。

}
