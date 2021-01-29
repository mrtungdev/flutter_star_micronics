#import "FlutterStarMicronicsPlugin.h"
#if __has_include(<flutter_star_micronics/flutter_star_micronics-Swift.h>)
#import <flutter_star_micronics/flutter_star_micronics-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_star_micronics-Swift.h"
#endif

@implementation FlutterStarMicronicsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterStarMicronicsPlugin registerWithRegistrar:registrar];
}
@end
