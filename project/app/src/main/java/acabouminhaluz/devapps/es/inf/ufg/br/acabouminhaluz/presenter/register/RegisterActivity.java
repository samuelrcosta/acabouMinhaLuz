package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.EasySharedPreferences;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.FormProblemException;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MessageEvent;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.BaseActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.login.LoginActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebRegister;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.utils.MaskEditUtil;

/**
 * Created by Samuel on 26/06/2018.
 */
public class RegisterActivity extends BaseActivity {

    private final int MIN_NAME = 3;
    private final int MIN_PASSWORD = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setStringFromEdit(R.id.email, EasySharedPreferences.getStringFromKey(
                this, EasySharedPreferences.KEY_EMAIL));

        EditText cpfEditText = (EditText)findViewById(R.id.cpf);
        cpfEditText.addTextChangedListener(MaskEditUtil.mask(cpfEditText, MaskEditUtil.FORMAT_CPF));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void loginRedirectgoToLogin(View v){
        this.goToLogin();
    }

    public void goToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void register(View v){

        hideKeyboard();

        try{
            checkForm();
        } catch (FormProblemException e){
            showAlert(e.getMessage());
            return;
        }

        String name = getStringFromEdit(R.id.name);
        String cpf = getStringFromEdit(R.id.cpf);
        String email = getStringFromEdit(R.id.email);
        String password = getStringFromEdit(R.id.password);


        showDialogWithMessage(getString(R.string.load_register));

        tryRegister(name, cpf, email, password);
    }

    private void checkForm() throws FormProblemException{
        String name = getStringFromEdit(R.id.name);
        String cpf = getStringFromEdit(R.id.cpf);
        String email = getStringFromEdit(R.id.email);
        String password = getStringFromEdit(R.id.password);

        if("".equals(name)){
            throw new FormProblemException(getString(R.string.error_name_empty));
        }
        if(name.length() < MIN_NAME){
            throw new FormProblemException(getString(R.string.error_name_small));
        }

        if("".equals(cpf)){
            throw new FormProblemException(getString(R.string.error_cpf_empty));
        }
        if(cpf.length() < 14){
            throw new FormProblemException(getString(R.string.error_cpf_invalid));
        }

        if("".equals(email)){
            throw new FormProblemException(getString(R.string.error_email_empty));
        }

        if("".equals(password)){
            throw new FormProblemException(getString(R.string.error_password_empty));
        }

        if (password.length() < MIN_PASSWORD){
            throw new FormProblemException(getString(R.string.error_password_small));
        }
    }

    private void tryRegister(String name, String CPF, String email, String password) {
        WebRegister webRegister = new WebRegister(name, CPF, email, password);
        webRegister.call();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent msg) {
        dismissDialog();
        if(msg.getMessage().equals("Ok")){
            confirmDialog();
        }else{
            showAlert(msg.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Exception exception) {
        dismissDialog();
        showAlert(exception.getMessage());
    }

    private void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cadastro realizado com sucesso!");
        //builder.setMessage("This is an alert dialog message");
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToLogin();
            }
        });
        builder.show();
    }

}
