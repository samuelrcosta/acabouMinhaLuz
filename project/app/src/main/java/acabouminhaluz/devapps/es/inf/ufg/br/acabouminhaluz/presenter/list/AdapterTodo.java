package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.Task;

/**
 * Created by marceloquinta on 10/02/17.
 */

public class AdapterTodo extends RecyclerView.Adapter<AdapterTodo.TodoViewHolder> {

    /**
     * List of tasks
     */
    private List<Task> tasks;

    /**
     * The application context
     */
    private Context context;

    public AdapterTodo(List<Task> tasks, Context context){
        this.tasks = tasks;
        this.context = context;
    }

    @Override
    public AdapterTodo.TodoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_task, viewGroup, false);
        AdapterTodo.TodoViewHolder viewHolder = new AdapterTodo.TodoViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        final Task task = tasks.get(position);
        holder.nameView.setText(task.getName());
        holder.descriptionView.setText(task.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public static class TodoViewHolder
            extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView descriptionView;

        TodoViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView)itemView.findViewById(R.id.label_task_title);
            descriptionView = (TextView)itemView.findViewById(R.id.label_task_desc);
        }
    }
}
