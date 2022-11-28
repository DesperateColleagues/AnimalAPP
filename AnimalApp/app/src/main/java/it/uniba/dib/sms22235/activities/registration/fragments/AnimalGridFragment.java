package it.uniba.dib.sms22235.activities.registration.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniba.dib.sms22235.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnimalGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimalGridFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AnimalGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnimalGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnimalGridFragment newInstance(String param1, String param2) {
        AnimalGridFragment fragment = new AnimalGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        /*ArrayList<Animal> a = new ArrayList<>();
        Animal a1 = new Animal("Bobby","Cane","Violenza e delusione","01001",1);
        a.add(a1);
        Animal a2 = new Animal("Paco","Cane","La pasta di ammezzzogiorno","01002",1);
        a.add(a2);
        Animal a3 = new Animal("Mira","Gatto","Coccole e cibi di lusso","01003",1);
        a.add(a3);*/

        LinearLayoutManager lineMan = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.item_fragment_animal_single_card, container, false);
    }
}