//
//  SpeechService.swift
//  AgoraLiveShop
//
//  Created by 翁军 on 2019/7/13.
//  Copyright © 2019 WeaponJ. All rights reserved.
//

import UIKit
import SogouSpeechSDK

protocol SpeechServiceDelegate: NSObjectProtocol {
    func speechService(_ service: SpeechService, didReceiveResult result: String)
}

class SpeechService: NSObject {
    
    public weak var delegate: SpeechServiceDelegate?
    
    private static let service = SpeechService()
    
    private lazy var speechManager: SogouSpeechManager = {
        SogoSpeechSDK.sdkVersion()
        
        let filepathSpeex = (NSTemporaryDirectory() as NSString).appendingPathComponent("speex.data")
        let filepathVad = (NSTemporaryDirectory() as NSString).appendingPathComponent("vad.pcm")
        let filepathWav = (NSTemporaryDirectory() as NSString).appendingPathComponent("audio.wav")
        
        SogouSpeechPropertySetting.setProperty(filepathSpeex, forKey: ASR_ONLINE_DEBUG_SAVE_SPEEX_PATH)
        SogouSpeechPropertySetting.setProperty(filepathVad, forKey: ASR_ONLINE_DEBUG_SAVE_VAD_PATH)
        SogouSpeechPropertySetting.setProperty(filepathWav, forKey: ASR_ONLINE_DEBUG_SAVE_WAV_PATH)
        SogouSpeechPropertySetting.setProperty(false, forKey: ASR_ONLINE_ENABLE_DEBUG_LOG_BOOL)
        SogouSpeechPropertySetting.setProperty("default", forKey: ASR_ONLINE_MODEL)
        SogouSpeechPropertySetting.setProperty("canary.speech.sogou.com:443", forKey:ASR_ONLINE_HOST_ADDRESS)
        if (NSLocale.current.identifier != "zh_CN") {
            SogouSpeechPropertySetting.setProperty(SGASR_LANGUAGE_ENGLISH, forKey:ASR_ONLINE_LANGUAGE_STRING)
        }
        
        let manager = SogouSpeechManager()
        manager.setDelegate(self)
        
        return manager
    }()
}

extension SpeechService {
    
    public static func shared() -> SpeechService {
        return service
    }
    
    public func requestToken() {
        let uuid = UIDevice.current.identifierForVendor?.uuidString
        GenerateToken.request(withAppid: KeyCenter.SogouAppId, appkey: KeyCenter.SogouAppKey, uuid: uuid, durationHours: 10) { (token, error, success) in
            SogouSpeechPropertySetting.setProperty(token, forKey:SOGOU_SPEECH_TOKEN)
            SogouSpeechPropertySetting.setProperty(KeyCenter.SogouAppId, forKey:SOGOU_SPEECH_APPID)
            SogouSpeechPropertySetting.setProperty(uuid, forKey:SOGOU_SPEECH_UUID)
        }
    }
    
    public func startRecord() {
        speechManager.sendCommand(ASR_ONLINE_START, withData: nil)
    }
    
    public func stopRecord() {
        speechManager.sendCommand(ASR_ONLINE_STOP, withData: nil)
    }
}

extension SpeechService: SogouSpeechDelegate {
    
    func onEvent(_ eventName: String!, param: NSObject!) {
        print("On sogou speech event \(eventName ?? "") withParam: \(param ?? "" as NSObject)")
        if (eventName == MSG_ASR_ONLINE_ALL_CONTENT) {
            guard let result = param as? SPBStreamingRecognitionResult, result.isFinal else { return }
            guard let alternative = result.alternativesArray.firstObject as? SPBSpeechRecognitionAlternative else { return }
            delegate?.speechService(self, didReceiveResult: alternative.transcript)
        }
    }
    
    
    func onError(_ error: Error!) {
        print("On sogou speech error \(error ?? "" as! Error)")
    }
}
