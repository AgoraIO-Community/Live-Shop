//
//  StreamSetupViewController.swift
//  AgoraLiveShop
//
//  Created by 翁军 on 2019/7/13.
//  Copyright © 2019 WeaponJ. All rights reserved.
//

import UIKit
import AgoraRtcEngineKit
import WSTagsField

class StreamSetupViewController: UIViewController {

    @IBOutlet weak var imageButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        let tagsField = WSTagsField()
        tagsField.placeholder = NSLocalizedString("Add product tags", comment: "")
        tagsField.layoutMargins = UIEdgeInsets(top: 2, left: 6, bottom: 2, right: 6)
        tagsField.contentInset = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        tagsField.spaceBetweenLines = 5.0
        tagsField.spaceBetweenTags = 10.0
        tagsField.font = .systemFont(ofSize: 12.0)
        tagsField.backgroundColor = .white
        tagsField.acceptTagOption = .space
        self.view.addSubview(tagsField)
        tagsField.snp.makeConstraints { make in
            make.top.equalTo(imageButton.snp_bottomMargin).offset(20)
            make.height.equalTo(100)
            make.left.equalTo(40)
            make.right.equalTo(-40)
        }
    }
    
    @IBAction func onImageButtonTapped(_ sender: UIButton) {
        let alert = UIAlertController(title: NSLocalizedString("Pick Product Cover", comment: ""), message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "Camera", style: .default, handler: { _ in
            self.pickImage(sourceType: .camera)
        }))
        alert.addAction(UIAlertAction(title: NSLocalizedString("Photo Library", comment: ""), style: .default, handler: { _ in
            self.pickImage(sourceType: .photoLibrary)
        }))
        alert.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: ""), style: .cancel, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
    
    private func pickImage(sourceType: UIImagePickerController.SourceType) {
        let pickerController = UIImagePickerController()
        pickerController.delegate = self
        pickerController.allowsEditing = true
        pickerController.mediaTypes = ["public.image"]
        pickerController.sourceType = sourceType
        self.present(pickerController, animated: true, completion: nil)
    }

    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "To Live") {
            let liveViewController = segue.destination as! LiveViewController
            liveViewController.clientRole = .broadcaster
        }
    }
}

extension StreamSetupViewController: UIImagePickerControllerDelegate {
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        picker.dismiss(animated: true, completion: nil)
        guard let image = info[.editedImage] as? UIImage else { return }
        imageButton.setBackgroundImage(image, for: .normal)
        imageButton.setTitle("", for: .normal)
    }
}

extension StreamSetupViewController: UINavigationControllerDelegate {
    
}
