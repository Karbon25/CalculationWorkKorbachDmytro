package com.example.calculationworkkorbachdmytro.ui.upload;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.calculationworkkorbachdmytro.R;
import com.example.calculationworkkorbachdmytro.databinding.FragmentUploadBinding;

public class UploadFragment extends Fragment {

    private FragmentUploadBinding binding;
    private UploadViewModel viewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(UploadViewModel.class);

        binding = FragmentUploadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel.connectDatabase(this.getActivity());
        viewModel.setConfigAPI(this.getActivity());
        viewModel.getUploadState().observe(this.getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String status) {
                ((TextView)root.findViewById(R.id.upload_status)).setVisibility(VISIBLE);
                ((TextView)root.findViewById(R.id.upload_status)).setText(status);
            }
        });
        viewModel.getUploadDatabaseSize().observe(this.getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer currentSize) {
                ((TextView)root.findViewById(R.id.upload_size_database)).setText("Потребує вивантаження: "+currentSize.toString());
            }
        });
        viewModel.getUploadProgressBar().observe(this.getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer currentState) {
                if(currentState == 1){
                    ((ProgressBar)root.findViewById(R.id.upload_progress)).setVisibility(VISIBLE);
                }else{
                    ((ProgressBar)root.findViewById(R.id.upload_progress)).setVisibility(INVISIBLE);
                }
            }
        });
        viewModel.setUploadDatabaseSize();
        root.findViewById(R.id.upload_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.uploadDatabase();
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