package com.chavez.eduardo.dasinvestigacion;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.chavez.eduardo.dasinvestigacion.network.DeveloperObject;
import com.chavez.eduardo.dasinvestigacion.network.GameClient;
import com.chavez.eduardo.dasinvestigacion.utils.BottomSheetDialogFragment;
import com.chavez.eduardo.dasinvestigacion.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Developers extends AppCompatActivity {
    private ListView listView;


    List<DeveloperObject> developerObjects;

    GameClient client = new Retrofit.Builder()
            .baseUrl(NetworkUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GameClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Desarrolladores");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        listView = (ListView) findViewById(R.id.listDevelopers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            addNewDeveloper();

        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewDeveloper() {
        final EditText userInput;
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_add, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(dialogView);
        userInput = (EditText) dialogView.findViewById(R.id.editTextDialogDeveloperName);
        alertDialog.setTitle("Agregar")
                .setCancelable(false)
                .setMessage("")
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("No agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog alertDialogN = alertDialog.create();
        alertDialogN.show();
        Button validator = alertDialogN.getButton(DialogInterface.BUTTON_POSITIVE);
        validator.setOnClickListener(new CustomListener(alertDialogN, userInput));
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData();
    }

    private void prepareData() {
        Call<List<DeveloperObject>> call = client.getDevelopers();
        call.enqueue(new Callback<List<DeveloperObject>>() {
            @Override
            public void onResponse(Call<List<DeveloperObject>> call, Response<List<DeveloperObject>> response) {
                if (response.isSuccessful()) {
                    developerObjects = response.body();
                    populateListView(developerObjects);
                }
            }

            @Override
            public void onFailure(Call<List<DeveloperObject>> call, Throwable t) {

            }
        });
    }

    private void populateListView(List<DeveloperObject> developerObjects) {
        ArrayList<String> developers = new ArrayList<>();

        for (DeveloperObject object : developerObjects) {
            developers.add(object.getGame_developer());
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, developers);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                promptEdition(i);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                promptDeletion(i);
                return true;
            }
        });
    }

    private void promptEdition(final int i) {
        final EditText userInput;
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(dialogView);
        userInput = (EditText) dialogView.findViewById(R.id.editTextDialogDeveloperName);
        userInput.setText(developerObjects.get(i).getGame_developer());
        alertDialog.setTitle("Editar")
                .setCancelable(false)
                .setMessage("")
                .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmEdit(i, userInput.getText().toString());
                    }
                })
                .setNegativeButton("No editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog alertDialogN = alertDialog.create();
        alertDialogN.show();
    }

    private void promptDeletion(int i) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Desea eliminar: " + developerObjects.get(i).getGame_developer() + "?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmDeletion(i);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void confirmDeletion(int i) {
        Call<ResponseBody> call = client.deleteDeveloper(String.valueOf(developerObjects.get(i).getId()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Developers.this, "Elemento eliminado con exito", Toast.LENGTH_SHORT).show();
                    prepareData();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void confirmEdit(final int i, final String s) {
        Call<DeveloperObject> call = client.updateDeveloper(String.valueOf(developerObjects.get(i).getId()), s);
        call.enqueue(new Callback<DeveloperObject>() {
            @Override
            public void onResponse(Call<DeveloperObject> call, Response<DeveloperObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Developers.this, developerObjects.get(i).getId() + " editado exitosamente", Toast.LENGTH_SHORT).show();
                    prepareData();
                }
            }

            @Override
            public void onFailure(Call<DeveloperObject> call, Throwable t) {

            }
        });
    }

    private class CustomListener implements View.OnClickListener {
        private final Dialog dialog;
        private EditText userInput;

        public CustomListener(Dialog dialog, EditText userInput) {
            this.dialog = dialog;
            this.userInput = userInput;
        }

        @Override
        public void onClick(View view) {
            validateNameField(userInput);
        }

        private boolean validateNameField(EditText editText) {
            String regexString = "^[A-Za-z\\S]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
            Pattern r = Pattern.compile(regexString);

            Matcher m = r.matcher(editText.getText());
            if (m.matches()) {
                String nombre = userInput.getText().toString();
                insertDeveloper(nombre);
                dialog.dismiss();
            } else {
                errorField(editText);
                editText.setError("Ingrese caracteres validos");
                return false;
            }

            return true;
        }

        void errorField(EditText editText) {
            editText.setBackgroundColor(Color.rgb(255, 235, 238));
        }


    }

    private void insertDeveloper(String nombre) {
        Call<ResponseBody> call = client.addDeveloper(nombre);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    prepareData();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
