package v1.qalandia.qalandia;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] checkBointType;
    private final Integer[] imageId;
    private final String[] inOrOut;
    private final String[] science;
    public ListAdapter(Activity context, String[] checkBointType, Integer[] imageId, String [] science, String [] inOrout) {
        super(context, R.layout.situation_list_item_template, checkBointType);
        this.context = context;
        this.checkBointType = checkBointType;
        this.imageId = imageId;
        this.science = science;
        this.inOrOut = inOrout;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.situation_list_item_template, null, true);

        TextView situation = (TextView) rowView.findViewById(R.id.situationText);
        situation.setText(checkBointType[position]);

        ImageView car = (ImageView) rowView.findViewById(R.id.carImage);
        car.setImageResource(imageId[position]);

        TextView inout = (TextView) rowView.findViewById(R.id.inOut);
        inout.setText(inOrOut[position]);

        TextView timeS = (TextView) rowView.findViewById(R.id.timeSince);
        timeS.setText(science[position]);

        return rowView;
    }
}