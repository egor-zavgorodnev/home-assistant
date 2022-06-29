package home.assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import home.assistant.model.BusInfo;

public class ForthInfoAdapter extends ArrayAdapter<BusInfo> {

    private LayoutInflater inflater;
    private int layout;
    private List<BusInfo> records;

    public ForthInfoAdapter(Context context, int resource, List<BusInfo> records) {
        super(context, resource, records);
        this.records = records;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);

        TextView busView = view.findViewById(R.id.forthVehicle);
        TextView timeView = view.findViewById(R.id.forthTime);

        BusInfo record = records.get(position);

        busView.setText(record.getBusNumber());
        timeView.setText(record.getArrivalTime());

        return view;
    }
}
