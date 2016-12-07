package br.edu.ufabc.padm.jean.obompassageiro;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import br.edu.ufabc.padm.jean.obompassageiro.model.Lines;

public class ShareFragment extends Fragment {

    public ShareFragment() {
    }

    public static ShareFragment newInstance() {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_share, container, false);

        Switch s = (Switch) view.findViewById(R.id.share_switch);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String textSnack = "";

                if(isChecked) {
                    textSnack = getString(R.string.share_switch_checked);
                } else {
                    textSnack = getString(R.string.share_switch_unchecked);
                }

                Snackbar snackbar = Snackbar
                        .make(getView(), textSnack, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

        });

        /*
            Preenche o Spinner
         */
        Lines data = new Lines();

        Spinner spn1 = (Spinner) view.findViewById(R.id.spinner_share);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, data.getNamesList());
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spn1.setAdapter(spinnerArrayAdapter);

        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                String nome = parent.getItemAtPosition(posicao).toString();
                //Toast.makeText(v.getContext(), "Nome Selecionado: " + nome, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

}
