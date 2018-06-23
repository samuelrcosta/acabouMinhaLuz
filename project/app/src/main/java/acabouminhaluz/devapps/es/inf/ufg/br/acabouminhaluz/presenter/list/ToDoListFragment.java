package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.list;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.EasySharedPreferences;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.TaskDAO;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.Task;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.BaseFragment;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebLogin;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebTodo;


/**
 * A simple {@link Fragment} subclass.
 */
public class ToDoListFragment extends BaseFragment {

    private List<Task> taskList;
    private AdapterTodo adapter;


    public ToDoListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        taskList = new LinkedList<>();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        initRecycler();
        getTasks();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void initRecycler(){
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AdapterTodo(taskList,getActivity());
        recyclerView.setAdapter(adapter);
    }


    public void getTasks(){
        showDialogWithMessage(getString(R.string.load_tasks));
        TaskDAO dao = new TaskDAO(getActivity());
        adapter.setTasks(dao.getAll());
        adapter.notifyDataSetChanged();
        dismissDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Exception exception) {
        dismissDialog();
        showAlert(exception.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(List<Task> tasks) {
        dismissDialog();
        adapter.setTasks(tasks);
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Task task) {
        TaskDAO dao = new TaskDAO(getActivity());
        adapter.setTasks(dao.getAll());
        adapter.notifyDataSetChanged();
    }

    public void add(){
        Task task = new Task();
        task.setName("NEW");
        task.setDescription("DESCRIPTION");
        TaskDAO dao = new TaskDAO(getActivity());
        dao.create(task);

        adapter.setTasks(dao.getAll());
        adapter.notifyDataSetChanged();

    }



}
