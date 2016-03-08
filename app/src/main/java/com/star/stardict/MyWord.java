package com.star.stardict;


public class MyWord {

    private String mWord;
    private int mOffset;
    private int mLength;

    public MyWord(String word, int offset, int length) {
        mWord = word;
        mOffset = offset;
        mLength = length;
    }

    public String getWord() {
        return mWord;
    }

    public int getOffset() {
        return mOffset;
    }

    public int getLength() {
        return mLength;
    }
}
