package com.zzy.onestore.one_store;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gaa.sdk.iap.ConsumeListener;
import com.gaa.sdk.iap.ConsumeParams;
import com.gaa.sdk.iap.IapResult;
import com.gaa.sdk.iap.IapResultListener;
import com.gaa.sdk.iap.ProductDetail;
import com.gaa.sdk.iap.ProductDetailsListener;
import com.gaa.sdk.iap.ProductDetailsParams;
import com.gaa.sdk.iap.PurchaseClient;
import com.gaa.sdk.iap.PurchaseClientStateListener;
import com.gaa.sdk.iap.PurchaseData;
import com.gaa.sdk.iap.PurchaseFlowParams;
import com.gaa.sdk.iap.PurchasesUpdatedListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * OneStorePlugin
 */
public class OneStorePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private static final String METHOD_ONE_STORE_QUERY = "query";
    private static final String METHOD_ONE_STORE_BUY = "buy";
    private static final String METHOD_ONE_STORE_CONSUME = "consumeAsync";


    private static final String ARGUMENT_KEY_PRODUCTS_IDS = "product_ids";
    private static final String ARGUMENT_KEY_PRODUCTS_ID = "product_id";
    private static final String ARGUMENT_KEY_PRODUCTS_TYPE = "product_type";
    private static final String ARGUMENT_KEY_PURCHASE_DATA_STRING = "purchaseDataString";
    private MethodChannel channel;
    private PurchaseClient mPurchaseClient;
    private boolean isServiceConnected;
    private Activity mActivity;
    private PurchasesUpdatedListener purchasesUpdatedListener;
    private Set<String> mTokenToBe;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "one_store");

        mPurchaseClient = PurchaseClient.newBuilder(flutterPluginBinding.getApplicationContext()).setBase64PublicKey(AppSecurity.getPublicKey()).setListener(purchasesUpdatedListener).build();
        channel.setMethodCallHandler(this);
    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case METHOD_ONE_STORE_QUERY:
                List<String> productIdList = call.argument(ARGUMENT_KEY_PRODUCTS_IDS);
                queryProduct(productIdList, result);
                break;
            case METHOD_ONE_STORE_BUY:
                String productId = call.argument(ARGUMENT_KEY_PRODUCTS_ID);
                String productType = call.argument(ARGUMENT_KEY_PRODUCTS_TYPE);
                buyProduct(result, productId, productType);
                break;
            case METHOD_ONE_STORE_CONSUME:
                String PurchaseDataString =  call.argument(ARGUMENT_KEY_PURCHASE_DATA_STRING);
                try {
                    PurchaseData  purchaseData = new PurchaseData(PurchaseDataString);
                    consumeAsync(purchaseData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            default:
                result.notImplemented();
                break;
        }
    }


    public void startConnection(final Runnable executeOnSuccess) {
        mPurchaseClient.startConnection(new PurchaseClientStateListener() {
            @Override
            public void onSetupFinished(final IapResult iapResult) {
                if (iapResult.isSuccess()) {
                    isServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                    return;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleErrorCode(iapResult);
                    }
                }, 300);
            }

            @Override
            public void onServiceDisconnected() {
                isServiceConnected = false;
            }
        });
    }

    private void queryProductDetailAsync(final List<String> productIdList, final ProductDetailsListener listener) {
        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                ProductDetailsParams params = ProductDetailsParams.newBuilder()
                        .setProductIdList(productIdList)
                        .setProductType(PurchaseClient.ProductType.INAPP)
                        .build();
                mPurchaseClient.queryProductDetailsAsync(params, listener);
            }
        });
    }

    private void buyProduct(final Result result, final String productId, @PurchaseClient.ProductType String productType) {
        Log.d("buy-product", "buyProduct() - productId:" + productId + " productType: " + productType);

        /*
         * PurchaseClient의 launchPurchaseFlowAsync API를 이용하여 구매요청을 진행합니다.
         * 상품명을 공백("")으로 요청할 경우 개발자센터에 등록된 상품명을 결제화면에 노출됩니다. 구매시 지정할 경우 해당 문자열이 결제화면에 노출됩니다.
         */
        PurchaseFlowParams params = PurchaseFlowParams.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .setProductName("")
                .setGameUserId("")
                .setPromotionApplicable(false)
                .build();

        launchPurchaseFlow(params);
        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(IapResult iapResult, List<PurchaseData> purchases) {
                if (iapResult.isSuccess()) {
                    for (PurchaseData purchase : purchases) {
                        if (AppSecurity.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature())) {
                            ///TODO（wgd）:
                            ///充值成功
                            Map<String, Object> maps = new HashMap<>();
                            maps.put("success", true);
                            maps.put("msg", iapResult.getMessage());
                            List<String> purchasesString = new ArrayList<>();
                            for (PurchaseData purchaseData : purchases) {
                                purchasesString.add(purchaseData.getOriginalJson());
                            }
                            maps.put("purchases", purchasesString);
                            result.success(maps);

//                            consumeAsync(purchase);
                        } else {
                            //Signature information is invalid.
                        }
                    }
                    return;
                }
                handleErrorCode(iapResult);
            }
        };
    }

    private void consumeAsync(final PurchaseData data) {
        if (mTokenToBe == null) {
            mTokenToBe = new HashSet<>();
        } else if (mTokenToBe.contains(data.getPurchaseToken())) {
            Log.i("fail", "Token was already scheduled to be consumed - skipping...");
            return;
        }

        mTokenToBe.add(data.getPurchaseToken());

        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                ConsumeParams params = ConsumeParams.newBuilder().setPurchaseData(data).build();
                mPurchaseClient.consumeAsync(params, new ConsumeListener() {
                    @Override
                    public void onConsumeResponse(IapResult iapResult, PurchaseData purchaseData) {
                        if (iapResult.isSuccess()) {
                            if (purchaseData.getPurchaseToken().equals(data.getPurchaseToken())) {
                                mTokenToBe.remove(data.getPurchaseToken());
                                //产品已被消耗

                            } else {
                                //purchaseToken not equal

                            }
                        } else {
                            handleErrorCode(iapResult);
                        }
                    }
                });
            }
        });
    }

    private void queryProduct(List<String> productIdList, final Result result) {
        queryProductDetailAsync(productIdList, new ProductDetailsListener() {
            @Override
            public void onProductDetailsResponse(IapResult iapResult, List<ProductDetail> productDetails) {
                Map<String, Object> map = new HashMap<>();
                if (iapResult.isSuccess()) {
                    if (productDetails.isEmpty()) {
                        ///未找到商品
                        map.put("success", false);
                        map.put("msg", "Item not found");
                        result.success(map);
                        return;
                    }
                    map.put("success", true);
                    map.put("msg", iapResult.getMessage());
                    List<String> ProductDetailJsonStrings = new ArrayList<>();
                    for (ProductDetail detail : productDetails) {
                        ProductDetailJsonStrings.add(detail.getOriginalJson());
                    }
                    map.put("productDetails", ProductDetailJsonStrings);
                    result.success(map);
                } else {
                    map.put("success", false);
                    map.put("msg", iapResult.getMessage());
                    result.success(map);
                }
            }
        });
    }

    public void launchLoginFlow(IapResultListener listener) {
        mPurchaseClient.launchLoginFlowAsync(mActivity, listener);
    }

    private void updateOrInstallPaymentModule() {
        mPurchaseClient.launchUpdateOrInstallFlow(mActivity, new IapResultListener() {
            @Override
            public void onResponse(IapResult iapResult) {
                if (iapResult.isSuccess()) {
                    mPurchaseClient.startConnection(new PurchaseClientStateListener() {
                        @Override
                        public void onSetupFinished(IapResult iapResult) {

                        }

                        @Override
                        public void onServiceDisconnected() {

                        }
                    });
                } else {
                    Log.w("fail", "launchUpdateOrInstall() got an error response code: " + iapResult.getResponseCode());
                }
            }
        });
    }


    private void handleErrorCode(IapResult iapResult) {
        if (iapResult.getResponseCode() == PurchaseClient.ResponseCode.RESULT_NEED_LOGIN) {
            Log.w("fail", "handleErrorCode() RESULT_NEED_LOGIN");
            launchLoginFlow(new IapResultListener() {
                @Override
                public void onResponse(IapResult iapResult) {
                    if (!iapResult.isSuccess()) {
                        Log.w("fail", "launchLoginFlow() got an error response code: " + iapResult.getResponseCode());
                    }
                }
            });
        } else if (iapResult.getResponseCode() == PurchaseClient.ResponseCode.RESULT_NEED_UPDATE) {
            Log.w("fail", "handleErrorCode() RESULT_NEED_UPDATE");
            updateOrInstallPaymentModule();
        } else {
            String message = iapResult.getMessage() + "(" + iapResult.getResponseCode() + ")";
            Log.d("fail", "handleErrorCode() error: " + message);

        }
    }

    private void launchPurchaseFlow(final PurchaseFlowParams params) {
        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                mPurchaseClient.launchPurchaseFlow(mActivity, params);
            }
        });
    }

    private void executeServiceRequest(Runnable runnable) {
        if (isServiceConnected) {
            runnable.run();
        } else {
            startConnection(runnable);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        if (mPurchaseClient != null) {
            mPurchaseClient.endConnection();
            mPurchaseClient = null;
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {

    }
}
