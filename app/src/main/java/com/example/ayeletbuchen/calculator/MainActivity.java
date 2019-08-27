package com.example.ayeletbuchen.calculator;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private StringBuilder equation;
    private StringBuilder strNumber;
    double total;
    boolean divideClicked;
    boolean multiplyClicked;
    boolean addClicked;
    boolean subtractClicked;
    TextView tv_screen;
    TextView tv_problem;
    Button buttonClear;
    boolean blueChecked;

    private final String mKEYEQUATION = "equation";
    private final String mKEYSTRNUMBER = "number";
    private final String mKEYTOTAL = "total";
    private final String mKEYDIVIDE = "divide";
    private final String mKEYMULTIPLY = "multiply";
    private final String mKEYADD = "add";
    private final String mKEYSUBTRACT = "subtract";
    private final String mKEYCLEAR = "clear";
    private final String mKEYSCREEN = "screen";
    private final String mKEYBLUE = "blue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        total = 0;
        tv_screen = findViewById(R.id.tv_display);
        tv_problem = findViewById(R.id.tv_equation);
        strNumber = new StringBuilder();
        equation = new StringBuilder();
        buttonClear = findViewById(R.id.clear);
        buttonClear.setText("AC");
        Snackbar snackbar = Snackbar.make(tv_screen, "Welcome to Calculator!", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void numberClicked(View view) {
        final int CHAR_CHECK = 2;
        if ((strNumber.length() == 0 || (equation.length() >= CHAR_CHECK && equation.charAt(equation.length() - CHAR_CHECK) == '%')) && !actionClicked()) {
            clearEquation();
            updateProblem();
            clearNumber();
            updateScreen();
        }
        Button button = (Button) view;
        strNumber.append(button.getText());
        buttonClear.setText("C");
        updateScreen();
    }

    public void operatorClicked(View view) {
        Button operator = (Button) view;
        if (strNumber.length() > 0) {
            double num = Double.parseDouble(strNumber.toString());
            modifyViewsAndTotal(num);
        }

        setOperator(operator.getId());
        clearNumber();
        updateProblem();
        updateScreen();
        tv_screen.setText(Double.toString(total));
    }

    public void clearClicked(View view) {
        Button button = (Button) view;
        clearNumber();
        updateScreen();
        if (button.getText() == "AC") {
            total = 0;
            clearEquation();
            updateProblem();
        }
        buttonClear.setText("AC");
    }

    public void toggleSign(View view) {
        if (strNumber.length() > 0) {
            double num = Double.parseDouble(strNumber.toString());
            clearNumber();
            num *= -1;
            strNumber.append(num);
            updateScreen();
        }
    }

    public void percentClicked(View view) {
        strNumber.delete(0, strNumber.length());
        strNumber.append(tv_screen.getText());
        if (strNumber.length() > 0) {
            double num = Double.parseDouble(strNumber.toString());
            clearNumber();
            num /= 100;
            strNumber.append(num);
            equation.append(" % ");
            updateScreen();
            updateProblem();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(mKEYEQUATION, equation.toString());
        outState.putString(mKEYSTRNUMBER, strNumber.toString());
        outState.putCharSequence(mKEYSCREEN, tv_screen.getText());
        outState.putDouble(mKEYTOTAL, total);
        outState.putBoolean(mKEYDIVIDE, divideClicked);
        outState.putBoolean(mKEYMULTIPLY, multiplyClicked);
        outState.putBoolean(mKEYADD, addClicked);
        outState.putBoolean(mKEYSUBTRACT, subtractClicked);
        outState.putCharSequence(mKEYCLEAR, buttonClear.getText());
        outState.putBoolean(mKEYBLUE, blueChecked);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        equation.append(savedInstanceState.getString(mKEYEQUATION));
        strNumber.append(savedInstanceState.getString(mKEYSTRNUMBER));
        total = savedInstanceState.getDouble(mKEYTOTAL);
        divideClicked = savedInstanceState.getBoolean(mKEYDIVIDE);
        multiplyClicked = savedInstanceState.getBoolean(mKEYMULTIPLY);
        addClicked = savedInstanceState.getBoolean(mKEYADD);
        subtractClicked = savedInstanceState.getBoolean(mKEYSUBTRACT);
        buttonClear.setText(savedInstanceState.getCharSequence(mKEYCLEAR));
        updateProblem();
        tv_screen.setText(savedInstanceState.getCharSequence(mKEYSCREEN));
        if (savedInstanceState.getBoolean(mKEYBLUE)) {
            tv_screen.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            blueChecked = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.blue_text_check:
                if (blueChecked) {
                    tv_screen.setTextColor(getResources().getColor(R.color.colorPrimaryText));
                    blueChecked = false;
                    item.setChecked(false);
                }
                else {
                    tv_screen.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    blueChecked = true;
                    item.setChecked(true);
                }
                return true;
            case R.id.about:
                showAbout();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void showAbout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.about);
        dialog.setMessage(R.string.info);
        dialog.show();
    }

    private void modifyViewsAndTotal(double num) {
        boolean notFirstChange = equation.length() > 0;
        if (notFirstChange && !isDigitOrPercent(equation.charAt(equation.length() - 2))) {
            equation.delete(equation.length() - 3, equation.length());
        }
        if (divideClicked) {
            if (notFirstChange) equation.append(" / ");
            total /= num;
        } else if (multiplyClicked) {
            if (notFirstChange) equation.append(" x ");
            total *= num;
        } else if (addClicked) {
            if (notFirstChange) equation.append(" + ");
            total += num;
        } else if (subtractClicked) {
            if (notFirstChange) equation.append(" - ");
            total -= num;
        } else {
            total = num;
        }
        equation.append(num);
    }

    private boolean isDigitOrPercent(char character)
    {
        boolean digit = false;
        switch (character) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '%':
            case '.':
            case 'E':
                digit = true;
        }
        return digit;
    }

    private void setOperator(int id) {
        switch (id)
        {
            case (R.id.divide):
                divideClicked = true;
                multiplyClicked = false;
                addClicked = false;
                subtractClicked = false;
                break;
            case(R.id.multiply):
                multiplyClicked = true;
                divideClicked = false;
                addClicked = false;
                subtractClicked = false;
                break;
            case (R.id.add):
                addClicked = true;
                divideClicked = false;
                multiplyClicked = false;
                subtractClicked = false;
                break;
            case (R.id.subtract):
                subtractClicked = true;
                divideClicked = false;
                multiplyClicked = false;
                addClicked = false;
                break;
            case (R.id.equals):
                divideClicked = false;
                multiplyClicked = false;
                addClicked = false;
                subtractClicked = false;
                break;
        }
    }

    private boolean actionClicked() {
        return divideClicked || multiplyClicked || addClicked || subtractClicked;
    }

    private void clearEquation()
    {
        equation.delete(0, equation.length());
    }

    private void clearNumber()
    {
        strNumber.delete(0, strNumber.length());
    }

    private void updateScreen()
    {
        tv_screen.setText(strNumber);
    }

    private void updateProblem()
    {
        tv_problem.setText(equation);
    }
}
