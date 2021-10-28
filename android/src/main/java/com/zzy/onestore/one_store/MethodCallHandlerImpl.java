package com.zzy.onestore.one_store;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gaa.sdk.iap.AcknowledgeListener;
import com.gaa.sdk.iap.AcknowledgeParams;
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
import com.gaa.sdk.iap.PurchasesListener;
import com.gaa.sdk.iap.PurchasesUpdatedListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MethodCallHandlerImpl implements MethodChannel.MethodCallHandler, PurchasesUpdatedListener {
    private final String TAG = "MethodCallHandlerImpl";

    private static final String METHOD_ONE_STORE_ENABLE_PENDING = "enable_pending";
    private static final String METHOD_ONE_STORE_QUERY = "query";
    private static final String METHOD_ONE_STORE_BUY = "buy";
    private static final String METHOD_ONE_STORE_CONSUME = "consumeAsync";
    private static final String METHOD_ONE_STORE_PURCHASE_UPDATED = "purchaseUpdated";
    private static final String METHOD_ONE_STORE_ACKNOWLEDGE = "acknowledgeAsync";
    private static final String METHOD_ONE_STORE_QUERY_PURCHASES = "queryPurchasesAsync";


    private static final String ARGUMENT_KEY_PRODUCTS_IDS = "product_ids";
    private static final String ARGUMENT_KEY_PRODUCTS_ID = "product_id";
    private static final String ARGUMENT_KEY_PRODUCTS_TYPE = "product_type";
    private static final String ARGUMENT_KEY_PURCHASE_STRING = "purchaseString";
    private static final String ARGUMENT_KEY_PURCHASE_SIGNATURE = "purchaseSignature";
    private static final String ARGUMENT_KEY_PURCHASE_BILLING_KEY = "purchaseBillingKey";

    private static final int RESULT_NO_FOUND_ITEM = -1;//未找到商品
    private static final int RESULT_PRODUCT_ID_NULL = -2;//产品id不可唯恐
    private static final int RESULT_VALIDATION_FAILED = 1111; //base64验证失败
    private PurchaseClient mPurchaseClient;
    private boolean isServiceConnected;
    private Activity mActivity;
    private Set<String> mTokenToBe;
    private MethodChannel channel;

    void onAttachedToActivity(Activity activity) {
        mActivity = activity;
        channel.setMethodCallHandler(this);
    }

    void onReattachedToActivityForConfigChanges(Activity activity) {
        mActivity = activity;
    }

    void startListening(BinaryMessenger messenger) {
        if (channel != null) {
            stopListening();
        }
        channel = new MethodChannel(messenger, "one_store");

    }

    void stopListening() {
        if (channel == null) {
            return;
        }

        channel.setMethodCallHandler(null);
        channel = null;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case METHOD_ONE_STORE_ENABLE_PENDING:
                enablePending(result);
                break;
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
                String purchaseDataString = call.argument(ARGUMENT_KEY_PURCHASE_STRING);
                String signature = call.argument(ARGUMENT_KEY_PURCHASE_SIGNATURE);
                String billingKey = call.argument(ARGUMENT_KEY_PURCHASE_BILLING_KEY);
                try {
                    PurchaseData purchaseData = new PurchaseData(purchaseDataString, signature, billingKey);
                    consumeAsync(result, purchaseData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case METHOD_ONE_STORE_QUERY_PURCHASES:
                queryPurchasesAsync(result, (String) call.argument(ARGUMENT_KEY_PRODUCTS_TYPE));
                break;
            case METHOD_ONE_STORE_ACKNOWLEDGE:
                purchaseDataString = call.argument(ARGUMENT_KEY_PURCHASE_STRING);
                signature = call.argument(ARGUMENT_KEY_PURCHASE_SIGNATURE);
                billingKey = call.argument(ARGUMENT_KEY_PURCHASE_BILLING_KEY);
                try {
                    PurchaseData purchaseData = new PurchaseData(purchaseDataString, signature, billingKey);
                    acknowledgeAsync(result, purchaseData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void enablePending(MethodChannel.Result result) {
        mPurchaseClient = PurchaseClient.newBuilder(mActivity).setBase64PublicKey(AppSecurity.getPublicKey()).setListener(this).build();
        result.success(AppSecurity.getPublicKey());
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

    private void buyProduct(final MethodChannel.Result result, final String productId, @PurchaseClient.ProductType String productType) {
        Map<String, Object> map = new HashMap<>();
        if (productId == null) {
            map.put("success", false);
            map.put("msg", "productId cannot be empty");
            map.put("resultCode", RESULT_PRODUCT_ID_NULL);
            result.success(map);
        } else {
            PurchaseFlowParams params = PurchaseFlowParams.newBuilder()
                    .setProductId(productId)
                    .setProductType(productType)
                    .setProductName("")
                    .setGameUserId("")
                    .setPromotionApplicable(false)
                    .build();
            launchPurchaseFlow(params);

        }
    }

    //非消耗性产品或订阅确认
    private void acknowledgeAsync(final MethodChannel.Result result, final PurchaseData data) {
        if (mTokenToBe == null) {
            mTokenToBe = new HashSet<>();
        } else if (mTokenToBe.contains(data.getPurchaseToken())) {
            Log.i(TAG, "Token was already scheduled to be acknowledged - skipping...");
            return;
        }
        mTokenToBe.add(data.getPurchaseToken());
        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                AcknowledgeParams params = AcknowledgeParams.newBuilder().setPurchaseData(data).build();
                mPurchaseClient.acknowledgeAsync(params, new AcknowledgeListener() {
                    @Override
                    public void onAcknowledgeResponse(IapResult iapResult, PurchaseData purchaseData) {
                        Map<String, Object> map = new HashMap<>();

                        if (iapResult.isSuccess()) {
                            if (data.getPurchaseToken().equals(purchaseData.getPurchaseToken())) {
                                mTokenToBe.remove(data.getPurchaseToken());
                                map.put("success", true);
                                map.put("msg", iapResult.getMessage());
                            } else {
                                map.put("success", false);
                                map.put("msg", "The product has been consumed");
                            }
                        } else {
                            map.put("success", false);
                            map.put("msg", iapResult.getMessage());
                            handleErrorCode(iapResult);
                        }
                        map.put("resultCode", iapResult.getResponseCode());
                        result.success(map);
                    }
                });
            }
        });
    }

    private void consumeAsync(final MethodChannel.Result result, final PurchaseData data) {
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
                        Map<String, Object> map = new HashMap<>();
                        if (iapResult.isSuccess()) {
                            if (purchaseData.getPurchaseToken().equals(data.getPurchaseToken())) {
                                mTokenToBe.remove(data.getPurchaseToken());
                                map.put("success", true);
                                map.put("msg", iapResult.getMessage());
                            } else {
                                map.put("success", false);
                                map.put("msg", "The product has been consumed");
                            }

                        } else {
                            map.put("success", false);
                            map.put("msg", iapResult.getMessage());
                            handleErrorCode(iapResult);
                        }
                        map.put("resultCode", iapResult.getResponseCode());
                        result.success(map);
                    }
                });
            }
        });
    }

    private void queryProduct(List<String> productIdList, final MethodChannel.Result result) {
        queryProductDetailAsync(productIdList, new ProductDetailsListener() {
            @Override
            public void onProductDetailsResponse(IapResult iapResult, List<ProductDetail> productDetails) {
                Map<String, Object> map = new HashMap<>();
                if (iapResult.isSuccess()) {
                    if (productDetails.isEmpty()) {
                        ///未找到商品
                        map.put("success", false);
                        map.put("msg", "Item not found");
                        map.put("resultCode", RESULT_NO_FOUND_ITEM);
                        result.success(map);
                        return;
                    }
                    map.put("success", true);
                    map.put("msg", iapResult.getMessage());
                    map.put("resultCode", 0);
                    map.put("productDetails", getProductDetailsMapList(productDetails));
                } else {
                    map.put("success", false);
                    map.put("msg", iapResult.getMessage());
                    map.put("resultCode", iapResult.getResponseCode());
                }
                result.success(map);
            }
        });
    }

    private ArrayList<HashMap<String, Object>> getProductDetailsMapList(List<ProductDetail> productDetails) {
        ArrayList<HashMap<String, Object>> productDetailList = new ArrayList<>();
        for (ProductDetail detail : productDetails) {
            HashMap<String, Object> detailMap = new HashMap<>();
            detailMap.put("productId", detail.getProductId());
            detailMap.put("type", detail.getType());
            detailMap.put("title", detail.getTitle());
            detailMap.put("price", detail.getPrice());
            detailMap.put("priceCurrencyCode", detail.getPriceCurrencyCode());
            detailMap.put("priceAmountMicros", detail.getPriceAmountMicros());
            detailMap.put("originalJson", detail.getOriginalJson());
            productDetailList.add(detailMap);
        }
        return productDetailList;
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
            Log.w("fail", "handleErrorCode() RESULT_NEED_UPDATE" + iapResult.getMessage());
            updateOrInstallPaymentModule();
        } else {
            String message = iapResult.getMessage() + "(" + iapResult.getResponseCode() + ")";
            Log.d("fail", "handleErrorCode() error: " + message);
        }
    }

    private void launchPurchaseFlow(final PurchaseFlowParams params) {

        executeServiceRequest(new Runnable() {
            @Override
            public void run() { mPurchaseClient.launchPurchaseFlow(mActivity, params);
            }
        });
    }

    private void queryPurchasesAsync(final MethodChannel.Result result, @PurchaseClient.ProductType final String productType) {
        final long time = System.currentTimeMillis();

        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                mPurchaseClient.queryPurchasesAsync(productType, new PurchasesListener() {
                    @Override
                    public void onPurchasesResponse(IapResult iapResult, @Nullable List<PurchaseData> purchases) {
                        Log.i(TAG, productType + " - Querying purchases elapsed time: " + (System.currentTimeMillis() - time + "ms"));
                        Map<String, Object> maps = new HashMap<>();
                        List<PurchaseData> purchaseList = new ArrayList<>();
                        if (iapResult.isSuccess() && purchases != null) {
                            for (PurchaseData purchase : purchases) {
                                if (AppSecurity.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature())) {
                                    purchaseList.add(purchase);
                                }
                            }
                            maps.put("success", true);
                            maps.put("purchases", getPurchasesMapList(purchaseList));
                        } else {
                            Log.w(TAG, productType + " - queryPurchasesAsync() got an error response code: " + iapResult.getResponseCode());
                            maps.put("success", false);
                            handleErrorCode(iapResult);
                        }
                        maps.put("msg", iapResult.getMessage());
                        maps.put("resultCode", iapResult.getResponseCode());
                        result.success(maps);
                    }
                });
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
    public void onPurchasesUpdated(IapResult iapResult, @Nullable List<PurchaseData> purchases) {
        Map<String, Object> maps = new HashMap<>();
        List<PurchaseData> purchaseList = new ArrayList<>();
        if (iapResult.isSuccess() && purchases != null) {
            for (PurchaseData purchase : purchases) {
                if (AppSecurity.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature())) {
                    purchaseList.add(purchase);
                }
            }
            maps.put("success", true);
            maps.put("purchases", getPurchasesMapList(purchaseList));
        } else {
            maps.put("success", false);
            handleErrorCode(iapResult);
        }
        maps.put("resultCode", iapResult.getResponseCode());
        maps.put("msg", iapResult.getMessage());
        channel.invokeMethod(METHOD_ONE_STORE_PURCHASE_UPDATED, maps);

    }

    private ArrayList<HashMap<String, Object>> getPurchasesMapList(List<PurchaseData> purchases) {
        ArrayList<HashMap<String, Object>> purchasesList = new ArrayList<>();
        for (PurchaseData purchaseData : purchases) {
            HashMap<String, Object> purchaseMap = new HashMap<>();
            purchaseMap.put("orderId", purchaseData.getOrderId());
            purchaseMap.put("packageName", purchaseData.getPackageName());
            purchaseMap.put("productId", purchaseData.getProductId());
            purchaseMap.put("purchaseTime", purchaseData.getPurchaseTime());
            purchaseMap.put("acknowledgeState", purchaseData.isAcknowledged());
            purchaseMap.put("developerPayload", purchaseData.getDeveloperPayload());
            purchaseMap.put("billingKey", purchaseData.getBillingKey());
            purchaseMap.put("purchaseState", purchaseData.getPurchaseState());
            purchaseMap.put("recurringState", purchaseData.getRecurringState());
            purchaseMap.put("signature", purchaseData.getSignature());
            purchaseMap.put("originalJson", purchaseData.getOriginalJson());
            purchaseMap.put("purchaseId",purchaseData.getPurchaseId());
            purchaseMap.put("purchaseToken",purchaseData.getPurchaseToken());
            purchasesList.add(purchaseMap);
            
        }
        return purchasesList;
    }
}
