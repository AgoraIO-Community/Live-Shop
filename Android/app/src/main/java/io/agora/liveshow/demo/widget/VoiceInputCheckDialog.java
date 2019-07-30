package io.agora.liveshow.demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import demo.liveshow.agora.io.R;

public class VoiceInputCheckDialog extends Dialog {
    private EditText etQuestMsg;
    private String mMessage = "";

    public VoiceInputCheckDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    public String getFinalQuestionInfo() {
        return etQuestMsg.getText().toString();
    }

    public void setMessage(String info) {
        mMessage  = info;
        if (null != etQuestMsg){
            etQuestMsg.setText(info);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_question_info);
        etQuestMsg = findViewById(R.id.check_question_info_et);
        etQuestMsg.setText(mMessage);
        findViewById(R.id.check_question_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnDismissListener(null);
                dismiss();
            }
        });
        findViewById(R.id.check_question_send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
