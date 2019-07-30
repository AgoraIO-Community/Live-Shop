//
//  VoiceButton.swift
//  AgoraLiveShop
//
//  Created by 翁军 on 2019/7/15.
//  Copyright © 2019 WeaponJ. All rights reserved.
//

import UIKit
import SVProgressHUD

class VoiceButton: UIButton {
    
    private static let deltaY: CGFloat = -40.0
    
    private lazy var recordView = VoiceRecordView(frame: CGRect.zero)
    
    var handleRecordBegan: (() -> Void)?
    var handleRecordComplete: (() -> Void)?
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
    }
}

extension VoiceButton {
    
    override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        if (isSelected) {
            return true
        }
        return super.point(inside: point, with: event)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if (LiveService.shared().isAnswering) {
            SVProgressHUD.showError(withStatus: NSLocalizedString("Please wait for last quiz to finish", comment: ""))
            return
        }
        
        isSelected = true
        
        recordView.textLabel.text = NSLocalizedString("Release to send, swipe to cancel", comment: "")
        recordView.showInView(superview!)
        handleRecordBegan?()
    }
    
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if (isSelected) {
            guard let touch = touches.first else { return }
            let point = touch.location(in: self)
            if (point.y > VoiceButton.deltaY) {
                recordView.textLabel.text = NSLocalizedString("Release to send, swipe to cancel", comment: "")
            } else {
                recordView.textLabel.text = NSLocalizedString("Release to cancel", comment: "")
            }
        }
    }
    
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if (isSelected) {
            handleRecordComplete?()
        }
        isSelected = false
        recordView.dismiss()
    }
    
    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        isSelected = false
        
        recordView.dismiss()
    }
}
