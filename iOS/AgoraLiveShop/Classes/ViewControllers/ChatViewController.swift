//
//  ChatViewController.swift
//  AgoraLiveShop
//
//  Created by 翁军 on 2019/7/15.
//  Copyright © 2019 WeaponJ. All rights reserved.
//

import UIKit

class ChatViewController: UITableViewController {
    
    private var messages = [(text: String, sender: String?)]()

    override func viewDidLoad() {
        super.viewDidLoad()

    }

    func addMessage(_ message: (String, String?)) {
        messages.append(message)
        tableView.reloadData()
    }
    
    // MARK: - Table view data source

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messages.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Chat Cell", for: indexPath)
        
        let message = messages[indexPath.row]
        let isSender = message.sender == nil
        cell.textLabel?.font = UIFont.systemFont(ofSize: 11)
        cell.textLabel?.textColor = isSender ? UIColor.orange : UIColor.white
        cell.textLabel?.text = (message.sender ?? NSLocalizedString("me", comment: "")) + ": " +  messages[indexPath.row].text

        return cell
    }
}
