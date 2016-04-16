package com.star.stardict;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MyDict {

    public static final String[] KEY = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

    public static final int MAX_WORD_LENGTH = 256;

    public static final int END_SIZE_OF_BYTE = 1;
    public static final int OFFSET_SIZE_OF_BYTE = 4;
    public static final int LENGTH_SIZE_OF_BYTE = 4;

    public static final int BYTE_SIZE_OF_BIT = 8;

    private InputStream mIdxInputStream;
    private InputStream mDictInputStream;

    private Map<String, Long> mKeyOffset;

    private String mWordToTranslate;
    private String mWordTranslation;

    public MyDict(File idxFile, File dictFile) {

        try {
            mIdxInputStream = new MyBufferedInputStream(
                    new BufferedInputStream(new FileInputStream(idxFile)));
            mIdxInputStream.mark(mIdxInputStream.available() + 1);

            mDictInputStream = new MyBufferedInputStream(
                    new BufferedInputStream(new FileInputStream(dictFile)));
            mDictInputStream.mark(mDictInputStream.available() + 1);
        } catch (FileNotFoundException e) {
            System.out.println("Open files error!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("This stream do not support available and mark!");
            e.printStackTrace();
        }

        initKeyOffset();
    }

    public String getWordToTranslate() {
        return mWordToTranslate;
    }

    public void setWordToTranslate(String wordToTranslate) {
        this.mWordToTranslate = wordToTranslate;
    }

    public String getWordTranslation() {
        return mWordTranslation;
    }

    public void setWordTranslation(String wordTranslation) {
        this.mWordTranslation = wordTranslation;
    }

    public void destroy() {
        try {
            mIdxInputStream.close();
            mDictInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Close files error!");
        }
    }

    private void initKeyOffset() {

        mKeyOffset = new HashMap<>();

        try {
            mIdxInputStream.reset();
            for (String key : KEY) {
                MyWord myWord = searchTargetWord(key);
                if (myWord != null) {
                    mKeyOffset.put(key,
                            ((MyBufferedInputStream) mIdxInputStream).getCurrentPosition()
                                    - myWord.getWord().length()
                                    - END_SIZE_OF_BYTE
                                    - OFFSET_SIZE_OF_BYTE
                                    - LENGTH_SIZE_OF_BYTE);
                } else {
                    mKeyOffset.put(key, (long) 0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MyWord searchTargetWord(String word) {

        MyWord myWord;

        while ((myWord = getEachWord()) != null) {

            if (myWord.getWord().equalsIgnoreCase(word)) {
                return myWord;
            }
        }

        return null;
    }

    private MyWord getEachWord() {

        String word = null;
        int offset = 0;
        int length = 0;

        boolean found = true;

        int wordLength = 0;

        int byteValue;

        byte[] bytes = new byte[MAX_WORD_LENGTH];

        try {
            while (true) {
                byteValue = mIdxInputStream.read();

                if (byteValue == -1) {
                    found = false;
                    break;
                }

                if ((byteValue != 0) && (wordLength < MAX_WORD_LENGTH)) {
                    bytes[wordLength] = (byte) byteValue;
                    wordLength++;
                } else {
                    break;
                }
            }

            word = new String(bytes).substring(0, wordLength);

            offset = getValueFromNBytes(OFFSET_SIZE_OF_BYTE);
            length = getValueFromNBytes(LENGTH_SIZE_OF_BYTE);

            if ((offset == -1) || (length == -1)) {
                found = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("idx file read error!");
        }

        return found ? new MyWord(word, offset, length) : null;

    }

    private int getValueFromNBytes(int sizeOfByte) throws IOException {

        int value = 0;

        int byteValue;

        for (int i = (sizeOfByte - 1); i >= 0; i--){
            byteValue = mIdxInputStream.read();

            if (byteValue == -1) {
                return -1;
            }

            value += (byteValue << (BYTE_SIZE_OF_BIT * i));
        }

        return value;
    }

    public void translate() {
        String word = getWordToTranslate();
        MyWord myWord = null;

        if (word.length() > 0) {
            try {
                mIdxInputStream.reset();
                Long toSkip = mKeyOffset.get(word.toLowerCase().substring(0, 1));
                if (toSkip != null) {
                    long skip = mIdxInputStream.skip(toSkip);
                    System.out.println("skip: " + skip);
                    myWord = searchTargetWord(word);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (myWord != null) {
                setWordTranslation(getTranslation(myWord.getOffset(), myWord.getLength()));
            } else {
                setWordTranslation("Sorry, " + word + " cannot be found!");
            }
        }
    }

    public String getTranslation(int offset, int length) {
        byte bytes[] = new byte[length];
        String translation = null;

        try {
            mDictInputStream.reset();
            if (mDictInputStream.available() < ((long) offset + (long) length)) {
                System.out.println("No so much value data!");
                return null;
            }

            long skip = mDictInputStream.skip(offset);
            System.out.println("skip: " + skip);

            if (mDictInputStream.read(bytes) == -1) {
                System.out.println("Arrive at the end of file!");
            } else {
                translation = new String(bytes);
                translation = translation.replace('●', '•');
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Dict file read error!");
        }

        return translation;
    }

}
