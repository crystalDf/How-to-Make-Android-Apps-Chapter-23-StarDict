package com.star.stardict;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;


public class StarDictFragment extends Fragment {

    private MyDict mMyDict;

    private File mIdxFile;
    private File mDictFile;

    private EditText mWordToTranslateEditText;
    private Button mTranslateButton;
    private TextView mWordTranslatedTextView;

    public static StarDictFragment newInstance() {
        return new StarDictFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_star_dict, container, false);

        File externalFilesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

        if (externalFilesDir == null) {
            return view;
        }

        mIdxFile =  new File(externalFilesDir, "Mydict.idx");
        mDictFile = new File(externalFilesDir, "Mydict.dict");

        mMyDict = new MyDict(mIdxFile, mDictFile);

        mWordToTranslateEditText = (EditText) view.findViewById(R.id.word_to_translate);
        mTranslateButton = (Button) view.findViewById(R.id.translate);
        mWordTranslatedTextView = (TextView) view.findViewById(R.id.word_translation);

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

        Intent intent = getActivity().getIntent();

        if (intent != null) {
            CharSequence content = intent.getCharSequenceExtra(getString(R.string.content));
            if (!TextUtils.isEmpty(content)) {
                mWordToTranslateEditText.setText(content);
                mTranslateButton.performClick();
            }
        }

        ListenClipboardService.start(getActivity());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mMyDict != null) {
            mMyDict.destroy();
        }

        mMyDict = null;
    }
}
