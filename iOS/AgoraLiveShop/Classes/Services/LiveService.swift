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
    
    // init AgoraRtcEngineKit
    // then enable video capturing and set the channel profile for broadcasting
    // Note: audio recording and playing is enabled by default
    private lazy var rtcKit: AgoraRtcEngineKit = {
        let kit = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.AgoraAppId, delegate: self)
        kit.enableVideo()
        kit.setChannelProfile(.liveBroadcasting)
        return kit
    }()
    // init AgoraRtmKit
    private lazy var rtmKit: AgoraRtmKit! = AgoraRtmKit(appId: KeyCenter.AgoraAppId, delegate: self)
    
    private var channel: AgoraRtmChannel?
    
    public var isAnswering = false
}

extension LiveService {
    
    public static func shared() -> LiveService {
        return service
    }
    
    // login to agora rtm service
    public func login() {
        rtmKit.login(byToken: nil, user: currentUser, completion: { errorCode in
            if (errorCode != .ok) {
                print("RTM login failed \(errorCode)")
            } else {
                print("RTM login success")
            }
        })
    }
    
    // join video broadcasting and messaging channel use the same channelId
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
            if (errorCode != .channelErrorOk) {
                print("Join rtm channel failed \(errorCode)")
            } else {
                print("Join rtm channel success")
            }
        })
    }
    
    // set the local video view for broadcasters
    public func setupLocalVideo(withUid uid: UInt, toView view: UIView) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = view
        canvas.uid = uid
        canvas.renderMode = .hidden
        rtcKit.setupLocalVideo(canvas)
    }
    
    // set the remote video view for audiences
    public func setupRemoteVideo(withUid uid: UInt, toView view: UIView) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = view
        canvas.uid = uid
        canvas.renderMode = .hidden
        rtcKit.setupRemoteVideo(canvas)
        rtcKit.setRemoteVideoStream(uid, type: .high)
    }
    
    // send messages with different types
    public func send(text: String, type: MessageType) {
        let message = LiveService.buildMessage(text, type: type)
        channel?.send(message, completion: { errorCode in
            if (errorCode == .errorOk) {
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
    
    // leave both the video broadcasting and messaging channel
    public func leaveChannel() {
        rtcKit.leaveChannel(nil)
        channel?.leave(completion: nil)
        
        UIApplication.shared.isIdleTimerDisabled = false
    }
}

extension LiveService {
    // add some special characters for different message types so we can parse it
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
    
    // parse the received message
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
