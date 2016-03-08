package com.star.stardict;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private MyDict mMyDict;

    private File mIdxFile;
    private File mDictFile;

    private EditText mWordToTranslateEditText;
    private Button mTranslateButton;
    private TextView mWordTranslatedTextView;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File externalFilesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

        if (externalFilesDir == null) {
            return;
        }

        mIdxFile =  new File(externalFilesDir, "Mydict.idx");
        mDictFile = new File(externalFilesDir, "Mydict.dict");

        mMyDict = new MyDict(mIdxFile, mDictFile);

        mWordToTranslateEditText = (EditText) findViewById(R.id.word_to_translate);
        mTranslateButton = (Button) findViewById(R.id.translate);
        mWordTranslatedTextView = (TextView) findViewById(R.id.word_translation);

        mTranslateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = mWordToTranslateEditText.getText().toString();
                if (!word.equals("")) {
                    mMyDict.setWordToTranslate(word);
                    mMyDict.translate();
                    mWordTranslatedTextView.setText(mMyDict.getWordTranslation());
                }
            }
        });

        final ClipboardManager clipboardManager = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);

        clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""));
        clipboardManager.addPrimaryClipChangedListener(
                new ClipboardManager.OnPrimaryClipChangedListener() {

                    @Override
                    public void onPrimaryClipChanged() {
                        mWordToTranslateEditText.setText(clipboardManager.getText());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMyDict != null) {
            mMyDict.destroy();
        }

        mMyDict = null;
    }
}
