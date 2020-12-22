package com.app.youcheng.utils;


import android.widget.EditText;

/**
 * 判断金额输入框工具类
 */
public class NumEditTextUtils {

    /**
     * 限制输入框规则
     */
    public static boolean isVaildText(EditText editText, int weishu) {
        String charSequence = editText.getText().toString();
        // 如果"."在起始位置,则起始位置自动补0
        if (charSequence.equals(".")) {
            charSequence = "0" + charSequence;
            editText.setText(charSequence);
            editText.setSelection(2);
            return false;
        }

        // 如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (charSequence.startsWith("0") && charSequence.trim().length() > 1) {
            if (!charSequence.substring(1, 2).equals(".")) {
                editText.setText(charSequence.subSequence(0, 1));
                editText.setSelection(1);
                return false;
            }
        }

        // 如果"."后超过weishu位,则无法后续输入
        String[] strs = charSequence.split("\\.");
        if (strs.length > 1) {
            if (strs[1].length() > weishu) {
                editText.setText(charSequence.subSequence(0, charSequence.length() - 1));
                editText.setSelection(charSequence.length() - 1);
                return false;
            }
        }

        return true;
    }




}
