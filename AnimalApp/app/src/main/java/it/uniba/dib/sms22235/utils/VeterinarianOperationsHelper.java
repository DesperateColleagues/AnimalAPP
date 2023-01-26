package it.uniba.dib.sms22235.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms22235.entities.operations.Diagnosis;

public class VeterinarianOperationsHelper {

    private FirebaseFirestore db;
    private Context context;

    public VeterinarianOperationsHelper(Context context){
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void registerDiagnosis(Diagnosis diagnosis) {
        Log.wtf("WTF",diagnosis.getId());
        Log.wtf("WTF",diagnosis.getDescription());
        db.collection(KeysNamesUtils.CollectionsNames.DIAGNOSIS)
                .document(diagnosis.getId())
                .set(diagnosis)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context,
                            "Diagnosi inserita con successo",
                            Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context,
                                "Errore interno, dati non aggiornati",
                                Toast.LENGTH_SHORT).show()
                );
    }
}
