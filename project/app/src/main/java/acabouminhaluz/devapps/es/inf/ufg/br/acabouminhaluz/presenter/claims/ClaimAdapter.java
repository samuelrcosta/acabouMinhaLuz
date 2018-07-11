package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.claims;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.Reclamacao;

public class ClaimAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Reclamacao> mDataSource;

    public ClaimAdapter(Context context, ArrayList<Reclamacao> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.activity_list_claims, parent, false);

        TextView dataTextView = (TextView) rowView.findViewById(R.id.data);
        TextView horaTextView = (TextView) rowView.findViewById(R.id.hora);
        TextView obsTextView = (TextView) rowView.findViewById(R.id.obs);

        Reclamacao reclamacao = (Reclamacao) getItem(position);

        dataTextView.setText(reclamacao.getData());
        horaTextView.setText(reclamacao.getHora());
        obsTextView.setText(reclamacao.getObs());


        return rowView;
    }
}
