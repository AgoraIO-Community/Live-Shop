//
//  VoiceRecordView.swift
//  AgoraLiveShop
//
//  Created by 翁军 on 2019/7/14.
//  Copyright © 2019 WeaponJ. All rights reserved.
//

import UIKit

class VoiceRecordView: UIView {
    
    var textLabel: UILabel
    
    override init(frame: CGRect) {
        
        textLabel = UILabel()
        textLabel.textColor = UIColor.white
        
        super.init(frame: frame)
        
        self.addSubview(textLabel)
        self.layer.cornerRadius = 10
        self.backgroundColor = UIColor(white: 0, alpha: 0.6)
        textLabel.textAlignment = .center
        textLabel.snp.makeConstraints { make in
            make.center.equalTo(self)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func showInView(_ view: UIView) {
        view.addSubview(self)
        self.snp.makeConstraints { make in
            make.height.equalTo(80)
            make.width.equalTo(300)
            make.center.equalTo(view)
        }
    }
    
    func dismiss() {
        self.removeFromSuperview()
    }
}
