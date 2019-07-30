#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "SogouSpeechDelegate.h"
#import "SogouSpeechErrorCode.h"
#import "SogoSpeechSDK.h"
#import "SogouSpeechManager.h"
#import "SogouSpeechPropertySetting.h"
#import "Status.pbobjc.h"
#import "Asr.pbobjc.h"
#import "Webhook.pbobjc.h"
#import "Operations.pbobjc.h"

FOUNDATION_EXPORT double SogouSpeechSDKVersionNumber;
FOUNDATION_EXPORT const unsigned char SogouSpeechSDKVersionString[];

