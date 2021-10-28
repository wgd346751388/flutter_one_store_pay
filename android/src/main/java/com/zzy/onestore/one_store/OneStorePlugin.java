package com.zzy.onestore.one_store;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * OneStorePlugin
 */
public class OneStorePlugin implements FlutterPlugin, ActivityAware {
    
    MethodCallHandlerImpl methodCallHandler;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        onAttachedToEngine(flutterPluginBinding.getBinaryMessenger());
    }

    private void onAttachedToEngine(BinaryMessenger messenger) {
        methodCallHandler = new MethodCallHandlerImpl();
        methodCallHandler.startListening(messenger);
    }
    


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodCallHandler.stopListening();
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        methodCallHandler.onAttachedToActivity(binding.getActivity());
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        methodCallHandler.onReattachedToActivityForConfigChanges(binding.getActivity());
    }

    @Override
    public void onDetachedFromActivity() {

    }
}
