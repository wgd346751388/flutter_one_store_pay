package com.zzy.onestore.one_store;

import com.gaa.sdk.iap.IapResult;
import com.gaa.sdk.iap.PurchaseData;

import java.util.List;

public interface PurchasesCallback {
    void onPurchaseUpdated(List<PurchaseData> purchases);
}
