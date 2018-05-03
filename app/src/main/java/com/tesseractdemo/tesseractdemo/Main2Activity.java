package com.tesseractdemo.tesseractdemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    ArrayList<String> data = new ArrayList<String>();
    ArrayList<String> data1 = new ArrayList<String>();
    ArrayList<String> data2 = new ArrayList<String>();
    ArrayList<Double> price = new ArrayList<Double>();
    CheckBox[] chs;
    int count;
    int d;
    Double VAT = 0.0;
    Double DeliveryCharge = 0.0;
    EditText editText;
    Integer users_No = 0;
    Integer count2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        loadTable();
    }
    public void itemClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){

        }
    }

    public void loadTable() {

        ArrayList<FoodOrder> orderList = new ArrayList<>();
        Intent intent = this.getIntent();
        Log.d("ARRAYYYY", Integer.toString(orderList.size()));
        orderList = intent.getParcelableArrayListExtra("Order List From Main1");

        d= 0;
        count=0;
        count2 = 0;
        editText = (EditText) findViewById(R.id.editText3);

//        for(int m=0; m<orderList.size(); m++){
//            if(m == orderList.size()-2){
//                DeliveryCharge = orderList.get(m).getPrice();
//                orderList.remove(m);
//            }
//            else if(m == orderList.size()-1){
//                VAT = orderList.get(m).getPrice();
//                orderList.remove(m);
        // VAT= Orderlist.get(orderList.size() - 1).getPrice();
//            }
//        }

        VAT = orderList.get(orderList.size() - 1).getPrice();
        DeliveryCharge = orderList.get(orderList.size() - 2).getPrice();

        Log.d("Delivery", Double.toString(DeliveryCharge));
        Log.d("VAT", Double.toString(VAT));

        orderList.remove(orderList.size() - 1);
        orderList.remove(orderList.size() - 1);

        //ArrayList<Integer> ch_id = new ArrayList<Integer>();
        //data.add("Quantity");
        Integer sumOfQty = 0;
        for(int i=0; i<orderList.size() ;i++) {

            Log.d("OrderListQty", Integer.toString(orderList.get(i).getQuantity()));
            for (int z = 0; z < orderList.get(i).getQuantity(); z++) {

                data.add("1");
                sumOfQty += 1;
                data1.add(orderList.get(i).getName());
                data2.add(Double.toString(orderList.get(i).getPrice()));
            }
        }

        Log.d("SumofQnty", Integer.toString(sumOfQty));
        Log.d("dataSize", Integer.toString(data.size()));
        Log.d("data1Size", Integer.toString(data1.size()));
        Log.d("data2Size", Integer.toString(data2.size()));

        TableLayout table;
        table = (TableLayout) findViewById(R.id.mytable);


        chs = new CheckBox[sumOfQty];

        for (int i = 0; i < sumOfQty; i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            String qty = "1";
            String ord = data1.get(i);
            Double pri = Double.parseDouble(data2.get(i))/ Double.parseDouble(data.get(i));
            price.add(pri);

            TextView tv2 = new TextView(this);
            tv2.setText(ord);
            tv2.setTypeface(null, Typeface.BOLD_ITALIC);
            tv2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            TextView tv3 = new TextView(this);
            tv3.setText(Double.toString(pri));
            tv3.setTypeface(null, Typeface.BOLD_ITALIC);
            tv3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

            chs[count] = new CheckBox(this);
            chs[count].setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);


            row.addView(tv2);
            row.addView(tv3);
            row.addView(chs[count]);
            table.addView(row);

            count++;

        }
    }

    public void Sum (View v) {

        if(editText.getText().toString().trim().equals("")){
            Toast.makeText(Main2Activity.this, "Please enter the number of users.", Toast.LENGTH_LONG).show();
        }
        else {
            users_No = Integer.valueOf(editText.getText().toString());
            if (count2 != users_No) {
                float sum = 0;
                for (int j = 0; j < count; j++) {
                    if (chs[j].isChecked()) {
                        sum += price.get(j);
                    }
                }

                sum += DeliveryCharge / users_No;
                sum += VAT / users_No;
                TableLayout table;
                table = (TableLayout) findViewById(R.id.mytable);
                TextView tv4 = new TextView(this);
                tv4.setText("Your Share is: ");
                tv4.setTypeface(null, Typeface.BOLD_ITALIC);

                tv4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                TextView tv5 = new TextView(this);
                tv5.setText(String.valueOf(sum));
                tv5.setTypeface(null, Typeface.BOLD_ITALIC);
                tv5.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                TableRow row = new TableRow(this);
                TextView tv6 = new TextView(this);
                tv6.setText("LE (VAT included)");
                tv6.setTypeface(null, Typeface.BOLD_ITALIC);
                tv4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                row.addView(tv4);
                row.addView(tv5);
                row.addView(tv6);
                row.setBackgroundColor(0xD1B39980);
                table.addView(row);
                count2++;
            }

            else if (count2 == users_No){
                Toast.makeText(Main2Activity.this, "You've exceeded the number of users.", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void clear (View v){
        for (int j = 0; j < count; j++){
            if(chs[j].isChecked()){
                chs[j].setChecked(false);
            }
        }
    }
}

