package Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class RowSplit {
	private static int colourReverse = 0;
	/* Insert comma delimited data and columnize it into a row */
	public static void splitText(String stats, Context context, TableLayout tl)
	{
		/* Text to add to the table */
		TextView tv;
		
		/* Split the string into words that can be set into columns */
		String[] splitString = stats.split(",");
		
		/* New row object to pass back */
		TableRow tr = new TableRow(context);
		tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		
		/* Iterate through the loop to set all the comma delimited data into separate columns */
		for(int i = 0; i < splitString.length; i++)
		{
			tv = new TextView(context);
			tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
			tv.setTextColor(Color.BLACK);
			tv.setText(splitString[i]);
			tr.addView(tv);
		}
		
		if(colourReverse % 2 == 0)
		{
			tr.setBackgroundColor(Color.WHITE);
		}
		else
		{
			tr.setBackgroundColor(Color.LTGRAY);
		}
		colourReverse++;

		tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		
		return;
	}
	
}
