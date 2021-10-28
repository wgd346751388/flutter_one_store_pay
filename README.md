# one_store

one store 内购插件

### 官方文档: https://dev.onestore.co.kr/devpoc/reference/view/Member#none

## 前提
    在onestore商店必须先上传带sdk的apk才可以实现内购

### 在AndroidManifest.xml中添加下方代码

```
<queries>
    <intent>
        <action android:name="com.onestore.ipc.iap.IapService.ACTION" />
    </intent>
    <intent>
        <action android:name="android.intent.action.VIEW" />
        <data android:scheme="onestore" />
    </intent>
</queries>
<uses-permission android:name="android.permission.WAKE_LOCK" />
```



### 设置ONE store支付界面 

ONE store支付界面提供全屏界面（竖向固定）和弹窗界面（竖向&横向）。

如果需要弹窗界面支付时，添加mita-data，在"iap:view_option"中设置"popup"值即可。

如不设置任何值，则是默认值状态，呈现与全屏模式（android:value="full"）相同效果。

```
< meta-data  android:name= "iap:view_option"  android:value = "full">
<meta-data  android:name = "iap:view_option"   android:value = "popup">
```

## 加密文件

​	请到example/android/app/src/main 目录下 把jni整个文件夹拷贝到自己的android/src/main 与java同级，并且修改public_keys.c文件 

```
return (*env)->NewStringUTF(env, "BASE_64_PUBLIC_KEY");
```

将BASE_64_PUBLIC_KEY换成自己的License key值

在build.gradle 加入

```
externalNativeBuild {
    ndkBuild {
        path 'src/main/jni/Android.mk'
    }
}
```

### 注意

 	需要引入ndk，如果新版的ndk报错：No toolchains found in the NDK toolchains folder for ABI with prefix: mips64el-linux-android，可以查看这位大神的博客：https://blog.csdn.net/qq_24118527/article/details/82867864。

## 用法

​	初始化：

```
await OneStore.instance.enablePendingPurchases();
```

​	调起内购：

```
 await OneStore.instance.buyProduct(productId, ProductType.inapp);

```

​	完成购买:

```
OneStore.instance.purchaseUpdatedStream().listen((PurchaseData purchaseData) async {
  if(purchaseData.success){
  //Payment successful
  }
});
```

​	消耗产品：

```
///消耗型产品必须先消耗，否则无法进行下次内购
ResultData resultData = await OneStore.instance.consumePurchase(purchase);
if (resultData.success) {
  print('Consumption successful-${resultData.msg}');
} else {
  print('Consumption failed-${resultData.msg}');
}
```

​	正在加载待售产品：

```
ProductDetails productDetails = await OneStore.instance.queryProductDetails(['productIds']);
```

​	查询过去订单：获取未消费的管理型商品和正在使用的包月型商品的信息

```
PurchaseData purchaseData = await OneStore.instance.queryPastPurchases(ProductType.inapp);
```

​	支付确认：

```
  ///非消耗性产品，请使用
  ///购买包月型商品时，仅对首次支付确认购买即可
  ///PurchaseData对象包含显示是否已确认购买的isAcknowledged
ResultData resultData = await OneStore.instance.acknowledgeAsync(purchase);
```

## 为这个插件做贡献

这个插件对底层平台层和 Dart 之间传递的许多数据结构使用 [json_serializable](https://pub.flutter-io.cn/packages/json_serializable)。编辑任何序列化数据结构后，通过运行 `flutter packages pub run build_runner build --delete-conflicting-outputs`. `flutter packages pub run build_runner watch --delete-conflicting-outputs`将观察文件系统的变化。

如果您想为插件做出贡献，请查看我们的 [贡献指南]()。
