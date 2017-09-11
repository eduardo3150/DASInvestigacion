package com.chavez.eduardo.dasinvestigacion.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;

import com.chavez.eduardo.dasinvestigacion.R;
import com.chavez.eduardo.dasinvestigacion.network.GameClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Eduardo on 1/9/2017.
 */

public class BottomSheetDialogFragment extends android.support.design.widget.BottomSheetDialogFragment {
    GameClient client = new Retrofit.Builder()
            .baseUrl(NetworkUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GameClient.class);

    AppCompatButton button;
    EditText userInput;
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_comment, null);
        dialog.setContentView(contentView);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userInput = (EditText) view.findViewById(R.id.developerName);
        button = (AppCompatButton) view.findViewById(R.id.addDeveloper);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = userInput.getText().toString();
                if (input.isEmpty()){
                    userInput.setError(getString(R.string.error_field_required));
                } else {
                    createNewDeveloper(input);
                }
            }
        });

    }

    private void createNewDeveloper(String input) {
        Call<ResponseBody> call = client.addDeveloper(input);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
