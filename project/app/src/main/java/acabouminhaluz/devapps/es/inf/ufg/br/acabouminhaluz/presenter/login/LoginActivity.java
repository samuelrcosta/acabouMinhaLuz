package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.home.HomeActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.EasySharedPreferences;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.FormProblemException;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.User;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.BaseActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.register.RegisterActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebLogin;

public class LoginActivity extends BaseActivity {

    private final int MIN_PASSWORD = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if this user is logged
        checkLogged();

        setStringFromEdit(R.id.username,EasySharedPreferences.getStringFromKey(
                this, EasySharedPreferences.KEY_EMAIL));
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

    private void checkLogged(){
        String token = EasySharedPreferences.getStringFromKey(this, EasySharedPreferences.KEY_TOKEN);
        if(!token.equals("")){
            goToHome();
        }
    }

    public void login(View v){

        hideKeyboard();

        try{
            checkEmail();
            checkPassword();
        } catch (FormProblemException e){
            showAlert(e.getMessage());
            return;
        }

        String password = getStringFromEdit(R.id.password);
        String email = getStringFromEdit(R.id.username);

        showDialogWithMessage(getString(R.string.load_login));

        tryLogin(password,email);
    }

    public void goToRegister(View v){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkPassword() throws FormProblemException{
        String password = getStringFromEdit(R.id.password);
        if("".equals(password)){
            throw new FormProblemException(getString(R.string.error_password_empty));
        }

        if (password.length() < MIN_PASSWORD){
            throw new FormProblemException(getString(R.string.error_password_small));
        }
    }

    private void checkEmail() throws FormProblemException{
        String email = getStringFromEdit(R.id.username);
        if("".equals(email)){
            throw new FormProblemException(getString(R.string.error_email_empty));
        }
    }

    private void tryLogin(String password, String email) {
        WebLogin webLogin = new WebLogin(email,password);
        webLogin.call();
        //goToHome();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(User user) {
        dismissDialog();
        storeCredentials(user);
        goToHome();
    }

    private void storeCredentials(User user){
        EasySharedPreferences.setStringFromKey(this,EasySharedPreferences.KEY_EMAIL,user.getEmail());
        EasySharedPreferences.setStringFromKey(this,EasySharedPreferences.KEY_TOKEN,user.getToken());
        EasySharedPreferences.setStringFromKey(this,EasySharedPreferences.KEY_NAME,user.getName());
        EasySharedPreferences.setStringFromKey(this,EasySharedPreferences.KEY_CPF,user.getCPF());
        EasySharedPreferences.setStringFromKey(this,EasySharedPreferences.KEY_IMAGE,user.getImage());
    }

    private void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Exception exception) {
        dismissDialog();
        showAlert(exception.getMessage());
    }
}
