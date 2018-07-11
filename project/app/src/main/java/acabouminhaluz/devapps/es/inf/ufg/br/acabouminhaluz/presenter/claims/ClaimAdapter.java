package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.claims;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.Reclamacao;



import java.util.List;



public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ReclamacaoViewHolder> {


    //this context we will use to inflate the layout
    private Context context;

    //we are storing all the products in a list
    private List<Reclamacao> reclamacaos;

    //getting the context and product list with constructor
    public ClaimAdapter(Context context, List<Reclamacao> reclamacaos) {
        this.context = context;
        this.reclamacaos = reclamacaos;
    }

    @Override
    public ReclamacaoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_list_claims, null);
        return new ReclamacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReclamacaoViewHolder holder, int position) {

        Reclamacao reclamacao = reclamacaos.get(position);

        holder.textViewData.setText(reclamacaos.get(position).getData());
        holder.textViewHora.setText(reclamacaos.get(position).getHora());
        holder.textViewObs.setText(reclamacaos.get(position).getObs());


    }


    @Override
    public int getItemCount() {
        return reclamacaos.size();
    }


    class ReclamacaoViewHolder extends RecyclerView.ViewHolder {

        TextView textViewData, textViewHora, textViewObs;

        public ReclamacaoViewHolder(View itemView) {
            super(itemView);

            textViewData = itemView.findViewById(R.id.data);
            textViewHora = itemView.findViewById(R.id.hora);
            textViewObs = itemView.findViewById(R.id.obs);
        }
    }
}