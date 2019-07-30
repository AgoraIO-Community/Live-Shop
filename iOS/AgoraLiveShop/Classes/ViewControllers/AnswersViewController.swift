//
//  AnswersViewController.swift
//  AgoraLiveShop
//
//  Created by 翁军 on 2019/7/15.
//  Copyright © 2019 WeaponJ. All rights reserved.
//

import UIKit

class AnswersViewController: UITableViewController {
    
    @IBOutlet weak var questionLabel: UILabel!
    
    @IBOutlet weak var statisticsLabel: UILabel!
    private var answers = [(text: String, sender: String)]()

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func showQuestion(_ question: String) {
        DispatchQueue.main.async {
            self.questionLabel.text = NSLocalizedString("Question: ", comment: "")  + question
        }
    }
    
    func addAnswer(_ answer: (String, String)) {
        DispatchQueue.main.async {
            self.answers.append(answer)
            self.tableView.reloadData()
            
            let correctAnswers = self.answers.map({ $0.text == "YES" })
            self.statisticsLabel.text =
                
                NSLocalizedString("Total: ", comment: "") + String(self.answers.count) + NSLocalizedString("answered, ", comment: "") + String(correctAnswers.count) + NSLocalizedString("answered YES, ", comment: "") + String(self.answers.count - correctAnswers.count) + NSLocalizedString("answered NO. ", comment: "")
        }
    }
    
    func clearAnswers() {
        answers.removeAll()
        self.questionLabel.text = ""
        tableView.reloadData()
    }

    // MARK: - Table view data source

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return answers.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Answer Cell", for: indexPath)

        cell.textLabel?.textColor = UIColor.white
        cell.textLabel?.font = UIFont.systemFont(ofSize: 11)
        cell.textLabel?.text = answers[indexPath.row].sender + NSLocalizedString("    answered: ", comment: "") + answers[indexPath.row].text

        return cell
    }
}
