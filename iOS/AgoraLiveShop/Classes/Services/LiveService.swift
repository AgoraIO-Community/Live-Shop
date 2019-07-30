//
//  LiveService.swift
//  AgoraLiveShop
//
//  Created by 翁军 on 2019/7/14.
//  Copyright © 2019 WeaponJ. All rights reserved.
//

import UIKit
import AgoraRtcEngineKit
import AgoraRtmKit

enum MessageType {
    case Chat
    case Quiz
    case Answer
}

protocol LiveServiceDelegate: NSObjectProtocol {
    func liveService(_ service: LiveService, didJoinedOfUid uid: UInt)
    func liveService(_ service: LiveService, didReceiveMessage text: String, type: MessageType, from member: AgoraRtmMember)
}

class LiveService: NSObject {
    
    public weak var delegate: LiveServiceDelegate?
    public var clientRole = AgoraClientRole.broadcaster {
        didSet {
            rtcKit.setClientRole(clientRole)
        }
    }
    
    private static let service = LiveService()
    
    private lazy var rtcKit: AgoraRtcEngineKit = {
        let kit = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.AgoraAppId, delegate: self)
        kit.enableVideo()
        kit.setChannelProfile(.liveBroadcasting)
        return kit
    }()
    private lazy var rtmKit: AgoraRtmKit! = AgoraRtmKit(appId: KeyCenter.AgoraAppId, delegate: self)
    
    private var channel: AgoraRtmChannel?
    
    public var isAnswering = false
}

extension LiveService {
    
    public static func shared() -> LiveService {
        return service
    }
    
    public func login() {
        rtmKit.login(byToken: nil, user: currentUser, completion: { errorCode in
            if (errorCode != .ok) {
                print("RTM login failed \(errorCode)")
            } else {
                print("RTM login success")
            }
        })
    }
    
    public func joinChannel(_ channelId: String) {
        let code = rtcKit.joinChannel(byToken: nil, channelId: channelId, info: nil, uid: 0, joinSuccess: nil)
        if code == 0 {
            UIApplication.shared.isIdleTimerDisabled = true
            rtcKit.setEnableSpeakerphone(true)
            print("Join rtc channel success")
        } else {
            print("Join rtc channel failed \(code)")
        }
        
        channel = rtmKit.createChannel(withId: channelId, delegate: self)
        channel?.join(completion: { errorCode in
            if (errorCode != .ok) {
                print("Join rtm channel failed \(errorCode)")
            } else {
                print("Join rtm channel success")
            }
        })
    }
    
    public func setupLocalVideo(withUid uid: UInt, toView view: UIView) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = view
        canvas.uid = uid
        canvas.renderMode = .hidden
        rtcKit.setupLocalVideo(canvas)
    }
    
    public func setupRemoteVideo(withUid uid: UInt, toView view: UIView) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = view
        canvas.uid = uid
        canvas.renderMode = .hidden
        rtcKit.setupRemoteVideo(canvas)
        rtcKit.setRemoteVideoStream(uid, type: .high)
    }
    
    public func send(text: String, type: MessageType) {
        let message = LiveService.buildMessage(text, type: type)
        channel?.send(message, completion: { errorCode in
            if (errorCode == .ok) {
                print("Send message success")
                if(type == .Quiz) {
                    self.isAnswering = true
                    DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 10, execute: {
                        self.isAnswering = false
                    })
                }
            } else {
                print("Send message failed \(errorCode)")
            }
        })
    }
    
    public func leaveChannel() {
        rtcKit.leaveChannel(nil)
        channel?.leave(completion: nil)
        
        UIApplication.shared.isIdleTimerDisabled = false
    }
}

extension LiveService {
    static private func buildMessage(_ text: String, type: MessageType) -> AgoraRtmMessage {
        var message = ""
        
        switch type {
        case .Chat:
            message = "|chat|" + text
        case .Answer:
            message = "|answer|" + text
        default:
            message = "|quiz|" + text
        }
 
        return AgoraRtmMessage(text: message)
    }
    
    static private func extractMessage(message: AgoraRtmMessage) -> (String, MessageType) {
        if (message.text.hasPrefix("|chat|")) {
            return (String(message.text.dropFirst(6)), .Chat)
        } else if (message.text.hasPrefix("|answer|")) {
            return (String(message.text.dropFirst(8)), .Answer)
        } else {
            return (String(message.text.dropFirst(6)), .Quiz)
        }
    }
}

extension LiveService: AgoraRtcEngineDelegate {
    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinedOfUid uid: UInt, elapsed: Int) {
        delegate?.liveService(self, didJoinedOfUid: uid)
    }
}

extension LiveService: AgoraRtmDelegate {
    
}

extension LiveService: AgoraRtmChannelDelegate {

    func channel(_ channel: AgoraRtmChannel, messageReceived message: AgoraRtmMessage, from member: AgoraRtmMember) {
        print("Message received \(message.text) withMember: \(member.userId)")
        let (text, type) = LiveService.extractMessage(message: message)
        delegate?.liveService(self, didReceiveMessage: text, type: type, from: member)
    }
    
    func channel(_ channel: AgoraRtmChannel, memberJoined member: AgoraRtmMember) {
        print("Member joined \(member.userId)")
    }
    
    func channel(_ channel: AgoraRtmChannel, memberLeft member: AgoraRtmMember) {
        print("Member left \(member.userId)")
    }
}
