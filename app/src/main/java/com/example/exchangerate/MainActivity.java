package com.example.exchangerate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import android.view.View;

import java.math.RoundingMode;
import java.text.DecimalFormat;
public class MainActivity extends AppCompatActivity {

    private TextView tvCNY, tvUSD;
    private LastInputEditText etCNY, etUSD;
    private FormatNumTextWatch textWatch1, textWatch2;
    private double rate = 9.130515;
    private final int beforePot = 7;//小数点前保留7位
    private final int afterPot = 2;//小数点后保留2位
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCNY = findViewById(R.id.tv_cny);
        tvUSD = findViewById(R.id.tv_usd);
        etCNY = findViewById(R.id.et_cny);
        etUSD = findViewById(R.id.et_usd);
        textWatch1 = new FormatNumTextWatch(etCNY, etUSD, true);
        textWatch2 = new FormatNumTextWatch(etUSD, etCNY, false);

        etCNY.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Log.e(TAG, "etCNY获得了焦点");
                    changeColor(1);
                    etCNY.setText("");
                    etUSD.setText("");
                    etCNY.addTextChangedListener(textWatch1);
                } else {
                    Log.e(TAG, "etCNY失去了焦点");
                    changeColor(2);
                    etCNY.removeTextChangedListener(textWatch1);
                }
            }
        });
        etUSD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Log.e(TAG, "etUSD获得了焦点");
                    changeColor(2);
                    etUSD.setText("");
                    etCNY.setText("");
                    etUSD.addTextChangedListener(textWatch2);
                } else {
                    Log.e(TAG, "etUSD失去了焦点");
                    changeColor(1);
                    etUSD.removeTextChangedListener(textWatch2);
                }
            }
        });

        etCNY.requestFocus();
    }


    /**
     * 根据焦点所在位置实时修改选中位置的颜色
     *
     * @param i 1，表示etCNY获得焦点；2，表示etUSD获得焦点
     */
    private void changeColor(int i) {
        if (1 == i) {
            tvCNY.setTextColor(getResources().getColor(R.color.colorAccent));
            etCNY.setTextColor(getResources().getColor(R.color.colorAccent));
            tvUSD.setTextColor(getResources().getColor(R.color.colorPrimary));
            etUSD.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tvUSD.setTextColor(getResources().getColor(R.color.colorAccent));
            etUSD.setTextColor(getResources().getColor(R.color.colorAccent));
            tvCNY.setTextColor(getResources().getColor(R.color.colorPrimary));
            etCNY.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    /**
     * 自定义的TextWatch
     */
    public class FormatNumTextWatch implements TextWatcher {
        private EditText currentEt;
        private EditText changeEt;
        private boolean isCNY;

        public FormatNumTextWatch(EditText curretnEt, EditText changeEt, boolean isCNY) {
            this.currentEt = curretnEt;
            this.changeEt = changeEt;
            this.isCNY = isCNY;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//            Log.e(TAG, "onTextChanged ==> start = " + start + ", before = " + before + ", count = " + count);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String currentStr = editable.toString().trim().replace(",", "");
            if (!TextUtils.equals(temp, currentStr)) {
                String changeStr = formatCurrentNum(currentStr);
                currentStr = changeStr.replace(",", "");
                currentEt.setText(changeStr);
                currentEt.setSelection(changeStr.length());
                if (!TextUtils.isEmpty(currentStr) && !TextUtils.equals("", currentStr)) {
                    double current = Double.valueOf(currentStr);
                    double change;
                    if (isCNY) {
                        change = current * rate;
                    } else {
                        change = current / rate;
                    }
                    changeEt.setText(formatChangeNum(change));
                } else {
                    changeEt.setText("");
                }
            }
        }
    }

    private String temp = "";

    /**
     * 格式化当前输入的数据满足一下要求
     * 1.第一位是0时，接下来只能接小数点
     * 2.第一位是小数点时，直接前面补0
     * 3.可以千分位
     * 4.整数最大7位，小数点后最大2位
     *
     * @param value
     * @return
     */
    private String formatCurrentNum(String value) {
        temp = value;
        //处理负数
        boolean neg = false;
        if (value.startsWith("-")) {
            value = value.substring(1);
            neg = true;
        }
        //处理数据前面的0
        if (value.indexOf('0') == 0 && value.indexOf('.') != 1) {
            value = "0";
        }
        //处理小数点之前
        String before = "";
        String after = "";
        if (value.indexOf('.') != -1) {
            before = value.substring(0, value.indexOf('.'));
            after = value.substring(value.indexOf('.'));
        } else {
            before = value;
        }
        if (before.length() > beforePot) {
            before = before.substring(0, beforePot);
        }
        value = before + after;

        //处理小数之后
        String tail = null;
        if (value.indexOf('.') == 0) {
            value = "0" + value;
        }
        if (value.indexOf('.') != -1) {
            tail = value.substring(value.indexOf('.'));
            value = value.substring(0, value.indexOf('.'));
        }
        StringBuilder sb = new StringBuilder(value);
        sb.reverse();
        for (int i = 3; i < sb.length(); i += 4) {
            sb.insert(i, ',');
        }
        sb.reverse();
        if (neg) {
            sb.insert(0, '-');
        }
        if (tail != null) {
            if (tail.length() > afterPot + 1) {
                tail = tail.substring(0, afterPot + 1);
            }
            sb.append(tail);
        }
        return sb.toString();
    }

    /**
     * 格式化数据，涉及到金额的数据不能四合五入，直接省去后面的金额
     *
     * @param value
     * @return
     */
    private String formatChangeNum(Double value) {
        DecimalFormat formater = new DecimalFormat("#,##0.##");
        formater.setRoundingMode(RoundingMode.FLOOR);
        String format = formater.format(value);
        return format;
    }
}
