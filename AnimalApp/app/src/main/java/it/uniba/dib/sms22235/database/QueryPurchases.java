package it.uniba.dib.sms22235.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.uniba.dib.sms22235.entities.operations.Interval;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class QueryPurchases {

    private final DBHelper dbHelper;

    public QueryPurchases(Context context){
        dbHelper = new DBHelper(context);
    }

    public long insertPurchase(String animal, String itemName, String owner, String date,
                               String category, float cost, int amount) {
        long testValue = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KeysNamesUtils.PurchaseFields.ANIMAL, animal);
        cv.put(KeysNamesUtils.PurchaseFields.ITEM_NAME, itemName);
        cv.put(KeysNamesUtils.PurchaseFields.OWNER, owner);
        cv.put(KeysNamesUtils.PurchaseFields.DATE, date);
        cv.put(KeysNamesUtils.PurchaseFields.CATEGORY, category);
        cv.put(KeysNamesUtils.PurchaseFields.COST, cost);
        cv.put(KeysNamesUtils.PurchaseFields.AMOUNT, amount);

        try{

            testValue = db.insert(KeysNamesUtils.CollectionsNames.PURCHASES, null, cv);
        }catch(Exception e){

            Log.d("DATABASE", e.getMessage());
        }

        return testValue;
    }

    public Cursor runFilterQuery(List<String> animals, List<String> categories, Interval<Float> costs) {

        String query = buildQueryString(animals, categories, costs);
        Cursor cursor = null;

        try{
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, null);

        }catch(Exception e){
            // todo: think to good error name
            Log.d("Cursor error", "errore nel lancio della query");
        }

        return cursor;
    }

    public Cursor getPurchaseByItemNameQuery(String itemName) {
        String query = "SELECT * FROM " + KeysNamesUtils.CollectionsNames.PURCHASES + " WHERE "
                + KeysNamesUtils.PurchaseFields.ITEM_NAME + " = '" + itemName + "';";
        Cursor cursor = null;

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            Log.d("Cursor error", "errore nel lancio della query");
        }

        return cursor;
    }

    @NonNull
    private String buildQueryString(List<String> animals, List<String> categories, Interval<Float> costs) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(KeysNamesUtils.CollectionsNames.PURCHASES).append("\n");

        StringBuilder where = new StringBuilder();
        where.append("WHERE ");

        StringBuilder empty_where = new StringBuilder();
        empty_where.append("WHERE ");

        /*
        if (animals != null && categories != null && costs != null)
        {
            where.append(KeysNamesUtils.PurchaseFields.ANIMAL).append(" IN (");
            for (int i = 0; i < animals.size(); i++) {
                where.append("'").append(animals.get(i)).append("'");
                if (i < (animals.size() - 1 ))
                    where.append(",");
            }

            where.append(") AND ");

            where.append(KeysNamesUtils.PurchaseFields.CATEGORY).append(" IN (");

            for (int i = 0; i < categories.size(); i++) {
                where.append("'").append(categories.get(i)).append("'");
                if (i < (categories.size() - 1 ))
                    where.append(",");
            }

            where.append(") AND ").append(KeysNamesUtils.PurchaseFields.COST).append(" BETWEEN (").append(costs.getMin()).append(",")
                    .append(costs.getMax()).append(");");

        }
        if (animals != null && categories != null && costs == null){

        }
        if (animals != null && categories == null && costs != null){

        }
        if (animals == null && categories != null && costs != null){

        }
        if (animals != null && categories == null && costs == null){

        }
        if (animals == null && categories == null && costs != null){

        }
        if (animals == null && categories != null && costs == null){

        }
        */

        // SE NON CI SONO FILTRI SELEZIONA TUTTA LA TABELLA
        if (animals == null && categories == null && costs == null){
            query.append(";");

        } else {
            // SE C'E' IL FILTRO ANIMALI
            if (animals != null) {

                where.append(KeysNamesUtils.PurchaseFields.ANIMAL).append(" IN (");

                for (int i = 0; i < animals.size(); i++) {
                    where.append("'").append(animals.get(i)).append("'");
                    if (i < (animals.size() - 1))
                        where.append(",");
                }

            }
            // SE C'E' IL FILTRO CATEGORIE
            if (categories != null) {
                // SE E' GIA' STATO INSERITO UN FILTRO AGGIUNGE AND
                if (!where.toString().equals(empty_where.toString())) {
                    where.append(") AND ");
                }

                where.append(KeysNamesUtils.PurchaseFields.CATEGORY).append(" IN (");

                for (int i = 0; i < categories.size(); i++) {
                    where.append("'").append(categories.get(i)).append("'");
                    if (i < (categories.size() - 1))
                        where.append(",");
                }

            }

            // SE C'E' IL FILTRO COSTI
            if (costs != null) {
                if (!where.toString().equals(empty_where.toString())) {
                    where.append(") AND ");
                }
                where.append(KeysNamesUtils.PurchaseFields.COST).append(" BETWEEN (").append(costs.getMin()).append(",")
                        .append(costs.getMax());
            }

            where.append(");");
            query.append(where);
        }

        return query.toString();
    }


}
