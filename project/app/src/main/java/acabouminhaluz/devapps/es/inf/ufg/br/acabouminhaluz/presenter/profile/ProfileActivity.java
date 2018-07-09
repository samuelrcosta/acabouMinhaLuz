package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.EasySharedPreferences;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.FormProblemException;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MessageEvent;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.BaseActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.home.HomeActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.login.LoginActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.utils.DownloadImageTask;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.utils.ImageUtil;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.utils.MaskEditUtil;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebProfileEdit;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebRegister;

public class ProfileActivity extends BaseActivity {
    private String name = "";
    private String cpf = "";
    private String email = "";
    private String image = "";

    private Bitmap imageBitmap = null;

    private static final int GALLERY_REQUEST = 450;
    private final int MIN_NAME = 3;
    private final int MIN_PASSWORD = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Add listener to toobar back button
        toolbarListener();

        // Load user data
        loadData();
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

    public void saveData(View v){

        hideKeyboard();

        try{
            checkForm();
        } catch (FormProblemException e){
            showAlert(e.getMessage());
            return;
        }

        this.name = getStringFromEdit(R.id.name);
        this.cpf = getStringFromEdit(R.id.cpf);
        this.email = getStringFromEdit(R.id.email);
        String password = getStringFromEdit(R.id.old_password);
        String new_password = getStringFromEdit(R.id.new_password);

        showDialogWithMessage(getString(R.string.load));

        trySave(this.name, this.cpf, this.email, this.image, password, new_password);
    }

    private void checkForm() throws FormProblemException{
        String name = getStringFromEdit(R.id.name);
        String cpf = getStringFromEdit(R.id.cpf);
        String email = getStringFromEdit(R.id.email);
        String password = getStringFromEdit(R.id.old_password);
        String new_password = getStringFromEdit(R.id.new_password);

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

        if(new_password.length() > 0 && new_password.length() < MIN_PASSWORD){
            throw new FormProblemException(getString(R.string.error_new_passoword_small));
        }
    }

    private void trySave(String name, String CPF, String email, String image, String password, String new_password) {
        String token = EasySharedPreferences.getStringFromKey(this, EasySharedPreferences.KEY_TOKEN);
        WebProfileEdit webEdit = new WebProfileEdit(token, name, CPF, email, image, password, new_password);
        webEdit.call();
    }

    private void loadData(){
        // Load avatar image
        loadAvatarImage();
        // Set text data
        setStringFromEdit(R.id.name,EasySharedPreferences.getStringFromKey(this, EasySharedPreferences.KEY_NAME));
        setStringFromEdit(R.id.cpf,EasySharedPreferences.getStringFromKey(this, EasySharedPreferences.KEY_CPF));
        setStringFromEdit(R.id.email,EasySharedPreferences.getStringFromKey(this, EasySharedPreferences.KEY_EMAIL));
        // Set cpf mask
        EditText cpfEditText = (EditText)findViewById(R.id.cpf);
        cpfEditText.addTextChangedListener(MaskEditUtil.mask(cpfEditText, MaskEditUtil.FORMAT_CPF));
    }

    public void updateImage(View v){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        de.hdodenhof.circleimageview.CircleImageView avatarImage = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.image);
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        avatarImage.setImageBitmap(bitmap);
                        // Save in global variable
                        this.imageBitmap = bitmap;
                        /*
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                saveBitmapImage();
                            }
                        });
                        */
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }

    private void saveBitmapImage(){
        this.image = ImageUtil.convert(this.imageBitmap);
    }

    private void toolbarListener(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });
    }

    private void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void logoff(View v){
        // Delete all key data
        EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_TOKEN, "");
        EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_NAME, "");
        EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_IMAGE, "");
        EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_CPF, "");
        // Go to login page
        goToLogin();
    }

    @Override
    public void onBackPressed() {
        goToHome();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent msg) {
        dismissDialog();
        if(msg.getMessage().equals("Ok")){
            // Save the new data
            EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_NAME, this.name);
            EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_CPF, this.cpf);
            EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_EMAIL, this.email);
            if(!this.image.equals("")){
                EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_IMAGE, this.image);
            }
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
        builder.setTitle("Perfil editado com sucesso!");
        //builder.setMessage("This is an alert dialog message");
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToHome();
            }
        });
        builder.show();
    }

    public void loadAvatarImage(){
        de.hdodenhof.circleimageview.CircleImageView content = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.image);
        // Get base64 image and convert to bitmap
        Bitmap image = ImageUtil.convert(EasySharedPreferences.getStringFromKey(this, EasySharedPreferences.KEY_IMAGE));
        content.setImageBitmap(image);
    }
}
