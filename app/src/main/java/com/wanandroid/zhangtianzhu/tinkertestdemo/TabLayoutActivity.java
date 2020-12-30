package com.wanandroid.zhangtianzhu.tinkertestdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TabLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        int[][] array = {{12,23,57},{23,45,67},{13,56,78}};
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        tableLayout.setStretchAllColumns(true);

        for(int row=0;row<3;row++){
            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.GRAY);
            for(int i=0;i<3;i++){
                TextView tv= new TextView(this);
                tv.setText(array[row][i]+"");
                tv.setBackground(getDrawable(R.drawable.tab_row_bg));
                tv.setGravity(Gravity.CENTER);
                tableRow.addView(tv);
            }
            tableLayout.addView(tableRow,new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }
}
