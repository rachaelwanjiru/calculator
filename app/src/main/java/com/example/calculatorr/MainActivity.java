package com.example.calculatorr;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView expressionTv, resultTv;
    private String currentInput = "";
    private String lastOperator = "";
    private double firstOperand = Double.NaN;
    private boolean isNewOp = true;

    private DecimalFormat decimalFormat = new DecimalFormat("###.#######");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        expressionTv = findViewById(R.id.expression_tv);
        resultTv = findViewById(R.id.result_tv);

        expressionTv.setText("");
        resultTv.setText("0");

        setClickListeners();
    }

    private void setClickListeners() {
        int[] buttonIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
                R.id.btn_dot
        };

        View.OnClickListener numberListener = v -> {
            MaterialButton button = (MaterialButton) v;
            if (isNewOp) {
                currentInput = "";
                isNewOp = false;
            }
            if (button.getText().toString().equals(".") && currentInput.contains(".")) {
                return;
            }
            currentInput += button.getText().toString();
            resultTv.setText(currentInput);
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        findViewById(R.id.btn_add).setOnClickListener(v -> prepareOperator("+"));
        findViewById(R.id.btn_subtract).setOnClickListener(v -> prepareOperator("-"));
        findViewById(R.id.btn_multiply).setOnClickListener(v -> prepareOperator("×"));
        findViewById(R.id.btn_divide).setOnClickListener(v -> prepareOperator("÷"));
        findViewById(R.id.btn_pow).setOnClickListener(v -> prepareOperator("^"));

        findViewById(R.id.btn_equal).setOnClickListener(v -> calculate());
        findViewById(R.id.btn_ac).setOnClickListener(v -> clear());
        findViewById(R.id.btn_percent).setOnClickListener(v -> applyPercent());
    }

    private void prepareOperator(String operator) {
        if (!Double.isNaN(firstOperand) && !currentInput.isEmpty()) {
            calculate();
        } else if (!currentInput.isEmpty()) {
            firstOperand = Double.parseDouble(currentInput);
        } else if (Double.isNaN(firstOperand)) {
             return;
        }
        
        lastOperator = operator;
        expressionTv.setText(decimalFormat.format(firstOperand) + " " + lastOperator);
        isNewOp = true;
    }

    private void calculate() {
        if (Double.isNaN(firstOperand) || currentInput.isEmpty()) return;

        double secondOperand = Double.parseDouble(currentInput);
        double result = 0;

        switch (lastOperator) {
            case "+": result = firstOperand + secondOperand; break;
            case "-": result = firstOperand - secondOperand; break;
            case "×": result = firstOperand * secondOperand; break;
            case "÷": 
                if (secondOperand != 0) result = firstOperand / secondOperand;
                else {
                    resultTv.setText("Error");
                    clearVars();
                    return;
                }
                break;
            case "^": result = Math.pow(firstOperand, secondOperand); break;
        }

        expressionTv.setText(decimalFormat.format(firstOperand) + " " + lastOperator + " " + decimalFormat.format(secondOperand) + " =");
        resultTv.setText(decimalFormat.format(result));
        firstOperand = result;
        currentInput = decimalFormat.format(result);
        isNewOp = true;
    }

    private void clear() {
        clearVars();
        expressionTv.setText("");
        resultTv.setText("0");
    }

    private void clearVars() {
        currentInput = "";
        lastOperator = "";
        firstOperand = Double.NaN;
        isNewOp = true;
    }

    private void applyPercent() {
        if (!currentInput.isEmpty()) {
            double value = Double.parseDouble(currentInput);
            value = value / 100.0;
            currentInput = decimalFormat.format(value);
            resultTv.setText(currentInput);
        }
    }
}
