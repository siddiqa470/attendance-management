package com.example.studentmanagementsystem;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CalendarAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> dateList;
    ArrayList<String> presentDates;

    public CalendarAdapter(Context context, ArrayList<String> dateList, ArrayList<String> presentDates) {
        this.context = context;
        this.dateList = dateList;
        this.presentDates = presentDates;
    }

    @Override public int getCount() { return dateList.size(); }

    @Override public Object getItem(int position) { return dateList.get(position); }

    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.calendar_day, parent, false);
        TextView dateText = view.findViewById(R.id.dateText);

        String fullDate = dateList.get(position);
        String[] parts = fullDate.split("-");
        String dayOnly = parts[2]; // "08"

        dateText.setText(dayOnly);

        if (presentDates.contains(fullDate)) {
            dateText.setTextColor(Color.BLUE); // Present
        } else {
            dateText.setTextColor(Color.RED); // Absent
        }

        return view;
    }
}
