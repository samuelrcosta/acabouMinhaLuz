package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by marceloquinta on 10/02/17.
 */
public class BaseFragment extends Fragment {

    MaterialDialog dialog;

    public void showDialogWithMessage(String message){
        dialog = new MaterialDialog.Builder(getActivity())
                .content(message)
                .progress(true,0)
                .show();
    }

    public void dismissDialog(){
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showAlert(String message){
        Snackbar.make(getView().findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG).show();
    }

}
