package br.edu.ufabc.padm.jean.obompassageiro;

import android.content.Intent;
import android.location.Location;
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

import br.edu.ufabc.padm.jean.obompassageiro.model.Lines;
import br.edu.ufabc.padm.jean.obompassageiro.services.ShareIntentService;

public class ShareFragment extends Fragment {

    private boolean isBomPassageiroEnabled = false;
    private Spinner spinner;

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

                isBomPassageiroEnabled = isChecked;

                if(spinner != null) {
                    spinner.setEnabled(isBomPassageiroEnabled);

                    if(!isChecked) {
                        spinner.setSelection(0);
                    }
                }

                if(!isChecked) {
                    Snackbar snackbar = Snackbar
                            .make(getView(), getString(R.string.share_switch_unchecked), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });

        /*
            Preenche o Spinner
         */
        Lines data = new Lines();

        spinner = (Spinner) view.findViewById(R.id.spinner_share);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, data.getNamesList());
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {

                if(posicao == 0) {
                    // First element is default
                    return;
                }

                String nome = parent.getItemAtPosition(posicao).toString();

                Location l = new Location("");
                l.setLatitude(0.0);
                l.setLongitude(0.0);

                Intent mServiceIntent = new Intent(getActivity(), ShareIntentService.class);
                mServiceIntent.putExtra(ShareIntentService.PARAM_DESTINATION, "SantoAndre");
                mServiceIntent.putExtra(ShareIntentService.PARAM_LOCATION, l);
                mServiceIntent.setAction(ShareIntentService.ACTION_SEND);
                getActivity().startService(mServiceIntent);

                Snackbar snackbar = Snackbar
                        .make(getView(), getString(R.string.share_switch_checked), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setEnabled(isBomPassageiroEnabled);

        return view;
    }

}
