package com.example.recipepal;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

// from: https://stackoverflow.com/questions/1778485/android-listview-display-all-available-items-without-scroll-with-static-header/1958482

public class UIUtils {
    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView, RecipeInfoActivity activity) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                if (listView.getId() == R.id.instructionsListView) {
                    /*View item = listAdapter.getView(itemPos, null, listView);
                    TextView t = item.findViewById(R.id.instructionContextTextView);

                    Rect bounds = new Rect();
                    Paint textPaint = t.getPaint();
                    textPaint.getTextBounds(t.getText().toString(), 0, t.getText().toString().length(), bounds);
                    int height = bounds.height();
                    totalItemsHeight += (height * 4);

                    Log.d("tag", "setListViewHeightBasedOnItems: " + height * 4);*/
                    View item = listAdapter.getView(itemPos, null, listView);
                    TextView t = item.findViewById(R.id.instructionContextTextView);
                    item.measure(0, 0);
                    int lines = t.getLineCount();

                    totalItemsHeight += item.getMeasuredHeight() * 2;
                    Log.d("UIUtils", "setListViewHeightBasedOnItems: " + lines);
                    Log.d("UIUtils", "setListViewHeightBasedOnItems: " + item.getMeasuredHeight() * lines);

                } else {
                    View item = listAdapter.getView(itemPos, null, listView);
                    item.measure(0, 0);
                    totalItemsHeight += item.getMeasuredHeight();
                    Log.d("hi", "setListViewHeightBasedOnItems: " + item.getMeasuredHeight());
                }
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }
    }
}