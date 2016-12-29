package wisdomitsol.com.limitareachart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private LimitLineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChart = (LimitLineChart) findViewById(R.id.chart1);
        final EditText medtNormalVal = (EditText) findViewById(R.id.edtNormalVal);

        mChart.upperLimit = 70.5f; // Start of upper limit
        mChart.lowerLimit = 50f;    // End of lower limit
        mChart.currentValue = 0f;
        mChart.drawGraph();

        medtNormalVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                float value = 0;
                if(TextUtils.isEmpty(medtNormalVal.getText().toString()) == false){
                    value = Float.parseFloat(medtNormalVal.getText().toString());
                }
                mChart.currentValue = value;
                mChart.drawGraph();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}
