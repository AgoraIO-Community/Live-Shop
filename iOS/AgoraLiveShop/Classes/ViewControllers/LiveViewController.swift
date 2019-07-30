//
//  LiveViewController.swift
//  AgoraLiveShop
//
//  Created by 翁军 on 2019/7/13.
//  Copyright © 2019 WeaponJ. All rights reserved.
//

import UIKit
import AgoraRtcEngineKit
import AgoraRtmKit

class LiveViewController: UIViewController {
    
    @IBOutlet weak var liveView: UIView!
    @IBOutlet weak var closeButton: UIButton!
    @IBOutlet weak var chatButton: DesignableButton!
    @IBOutlet weak var voiceButton: VoiceButton!
    @IBOutlet weak var answersButton: UIButton!
    
    private static let quizDuration = 10.0
    
    private lazy var chatInputView: ChatInputView = {
        let input = ChatInputView(frame: CGRect.zero)
        input.handleSend = { [unowned self] text in
            self.liveService.send(text: text, type: .Chat)
            self.chatViewController.addMessage((text, nil))
        }
        return input
    }()
    
    private var chatViewController: ChatViewController!
    private var answersViewController: AnswersViewController!
    
    var clientRole: AgoraClientRole!
    
    private lazy var liveService: LiveService = {
        let service = LiveService.shared()
        service.delegate = self
        return service
    }()
    
    private lazy var speechService: SpeechService = {
        let service = SpeechService.shared()
        service.delegate = self
        return service
    }()
    
    // MARK: - View Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupViewsVisiablity()
        setupVoiceButtonActions()
        
        liveService.clientRole = clientRole
        liveService.joinChannel("gwfwef123")
        if (clientRole == .broadcaster) {
            liveService.setupLocalVideo(withUid: 0, toView: liveView)
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        liveService.leaveChannel()
    }
    
    // MARK: Setup
    
    private func setupViewsVisiablity() {
        if (clientRole == .audience) {
            voiceButton.isHidden = true
            answersButton.isHidden = true
            answersViewController.view.isHidden = true
        }
    }
    
    private func setupVoiceButtonActions() {
        voiceButton.handleRecordBegan = { [unowned self] in
            self.speechService.startRecord()
        }
        
        voiceButton.handleRecordComplete = { [unowned self] in
            self.speechService.stopRecord()
        }
    }

    // MARK: - Actions
    
    @IBAction func onCloseButtonTapped(_ sender: UIButton) {
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func onChatButtonTapped(_ sender: UIButton) {
        chatInputView.showInView(self.view)
    }
    
    @IBAction func onAnswersButtonTapped(_ sender: UIButton) {
        answersViewController.view.isHidden = !answersViewController.view.isHidden
    }
    
    @IBAction func onVoiceButtonTapped(_ sender: UIButton) {
        
    }
    
    // MARK: - Navigation
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "Embed Answers") {
            answersViewController = (segue.destination as! AnswersViewController)
        } else if (segue.identifier == "Embed Chat") {
            chatViewController = (segue.destination as! ChatViewController)
        }
    }
}

extension LiveViewController: LiveServiceDelegate {
    func liveService(_ service: LiveService, didJoinedOfUid uid: UInt) {
        service.setupRemoteVideo(withUid: uid, toView: liveView)
    }
    
    func liveService(_ service: LiveService, didReceiveMessage text: String, type: MessageType, from member: AgoraRtmMember) {
        let start = member.userId.index(member.userId.startIndex, offsetBy: 4)
        let end = member.userId.index(member.userId.endIndex, offsetBy: -4)
        member.userId.replaceSubrange(start...end, with: "...")
        switch type {
        case .Quiz:
            let alert = UIAlertController(title: "", message: text, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: NSLocalizedString("YES", comment: ""), style: .default, handler: { _ in
                self.liveService.send(text: NSLocalizedString("YES", comment: ""), type: .Answer)
            }))
            alert.addAction(UIAlertAction(title: NSLocalizedString("NO", comment: ""), style: .default, handler: { _ in
                self.liveService.send(text: NSLocalizedString("NO", comment: ""), type: .Answer)
            }))
            self.present(alert, animated: true, completion: nil)
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + LiveViewController.quizDuration) {
                alert.dismiss(animated: true, completion: nil)
            }
        case .Chat:
            let message = (text, member.userId as String?)
            chatViewController.addMessage(message)
        case .Answer:
            let answer = (text, member.userId)
            answersViewController.addAnswer(answer)
        }
    }
}

extension LiveViewController: SpeechServiceDelegate {
    func speechService(_ service: SpeechService, didReceiveResult result: String) {
        let alert = UIAlertController(title: NSLocalizedString("Your Question", comment: ""), message: nil, preferredStyle: .alert)
        alert.addTextField { textField in
            textField.text = result
        }
        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: ""), style: .default, handler: { _ in
            guard let text = alert.textFields?.first?.text, text.count > 0 else { return }
            
            self.answersViewController.showQuestion(text)
            self.answersViewController.clearAnswers()
            self.liveService.send(text: text, type: .Quiz)
        }))
        alert.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: ""), style: .cancel, handler: { _ in
        }))
        self.present(alert, animated: true, completion: nil)
    }
}
