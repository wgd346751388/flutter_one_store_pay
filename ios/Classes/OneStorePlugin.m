#import "OneStorePlugin.h"
#if __has_include(<one_store/one_store-Swift.h>)
#import <one_store/one_store-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "one_store-Swift.h"
#endif

@implementation OneStorePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftOneStorePlugin registerWithRegistrar:registrar];
}
@end
