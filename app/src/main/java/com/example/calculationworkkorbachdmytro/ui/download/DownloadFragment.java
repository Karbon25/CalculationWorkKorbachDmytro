package com.example.calculationworkkorbachdmytro.ui.download;

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
import com.example.calculationworkkorbachdmytro.databinding.FragmentDownloadBinding;

public class DownloadFragment extends Fragment {

    private FragmentDownloadBinding binding;
    private DownloadViewModel viewModel;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DownloadViewModel.class);
        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel.connectDatabase(this.getActivity());
        viewModel.setConfigAPI(this.getActivity());
        viewModel.getUpdateStatus().observe(this.getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String status) {
                ((TextView)root.findViewById(R.id.download_state_update)).setVisibility(VISIBLE);
                ((TextView)root.findViewById(R.id.download_state_update)).setText(status);
            }
        });
        viewModel.getCurrentSizeDatabase().observe(this.getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer currentSize) {
                ((TextView)root.findViewById(R.id.download_current_size_database)).setText("Кількість зареєстрованих студентів "+currentSize.toString());
            }
        });
        viewModel.getStatusProgressBar().observe(this.getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer currentState) {
                if(currentState == 1){
                    ((ProgressBar)root.findViewById(R.id.download_progress_update)).setVisibility(VISIBLE);
                }else{
                    ((ProgressBar)root.findViewById(R.id.download_progress_update)).setVisibility(INVISIBLE);
                }
            }
        });
        viewModel.setCurrentSizeDatabase();
        root.findViewById(R.id.download_database_update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.updateDatabase();
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