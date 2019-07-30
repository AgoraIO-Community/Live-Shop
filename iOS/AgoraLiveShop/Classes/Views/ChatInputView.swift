//
//  ChatInputView.swift
//  AgoraLiveShop
//
//  Created by 翁军 on 2019/7/15.
//  Copyright © 2019 WeaponJ. All rights reserved.
//

import UIKit
import SnapKit

class ChatInputView: UIView {
    
    var handleSend: ((String) -> Void)?

    var inputField: UITextField
    var keyboardLauncher: UITextField
    
    override init(frame: CGRect) {
        
        let inputAccessoryView = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 60))
        inputAccessoryView.backgroundColor = UIColor(white: 0.8, alpha: 1.0)
        inputField = UITextField()
        inputField.layer.cornerRadius = 6
        inputField.layer.borderColor = UIColor(white: 0.97, alpha: 1.0).cgColor
        inputField.layer.borderWidth = 0.5
        inputField.placeholder = NSLocalizedString("Chat...", comment: "")
        inputAccessoryView.addSubview(inputField)
        inputField.snp.makeConstraints { make in
            make.edges.equalTo(inputAccessoryView).inset(UIEdgeInsets(top: 8, left: 15, bottom: 8, right: 80))
        }
        
        let paddingView = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 60))
        inputField.leftView = paddingView
        inputField.leftViewMode = .always
        inputField.backgroundColor = UIColor.white
        
        let sendBtn = UIButton(type: .custom)
        sendBtn.frame = CGRect(x: 0, y: 0, width: 80, height: 30)
        sendBtn.setTitleColor(UIColor.darkGray, for: .normal)
        sendBtn.setTitle(NSLocalizedString("Send", comment: ""), for: .normal)

        inputAccessoryView.addSubview(sendBtn)
        sendBtn.snp.makeConstraints { make in
            make.right.top.bottom.equalTo(0)
            make.width.equalTo(80)
        }
        
        keyboardLauncher = UITextField()
        keyboardLauncher.isHidden = true
        keyboardLauncher.inputAccessoryView = inputAccessoryView
        
        super.init(frame: frame)
        
        self.addSubview(keyboardLauncher)
        sendBtn.addTarget(self, action: #selector(onSendButtonTapped(sender:)), for: .touchUpInside)
        
        let gesture = UITapGestureRecognizer(target: self, action: #selector(onBackgroundTapped(sender:)))
        self.addGestureRecognizer(gesture)
    }
    
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    @objc func onSendButtonTapped(sender: UIButton) {
        guard let text = inputField.text, text.count > 0 else {
            return
        }
        handleSend?(text)
        inputField.text = ""
        dismiss()
    }
    
    @objc func onBackgroundTapped(sender: UIGestureRecognizer) {
        self.dismiss()
    }
    
}

extension ChatInputView {
    func showInView(_ view: UIView) {
        view.addSubview(self)
        self.snp.makeConstraints { make in
            make.edges.equalTo(view)
        }
        keyboardLauncher.becomeFirstResponder()
        inputField.becomeFirstResponder()
    }
    
    func dismiss() {
        self.inputField.resignFirstResponder()
        self.keyboardLauncher.resignFirstResponder()
        self .removeFromSuperview()
    }
}

