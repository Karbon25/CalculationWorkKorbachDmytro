package com.example.calculationworkkorbachdmytro.ui.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.calculationworkkorbachdmytro.R;
import com.example.calculationworkkorbachdmytro.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        EditText ipAddress = root.findViewById(R.id.setting_ip_address);
        EditText port = root.findViewById(R.id.setting_port);
        ipAddress.setText(preferences.getString("addressServer", "127.0.0.1"));
        port.setText(Integer.toString(preferences.getInt("portServer", 4700)));
        root.findViewById(R.id.setting_button_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ipAddress.getText().length() == 0 || port.getText().length() == 0){
                    Toast.makeText(getContext(), "Заповніть поля", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        int portInt = Integer.parseInt(port.getText().toString());
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("addressServer", ipAddress.getText().toString());
                        editor.putInt("portServer", portInt);
                        editor.apply();
                        Toast.makeText(getContext(), "Налаштування збережені", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(getContext(), "Введіть коректні дані", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}