package it.uniba.dib.sms22235.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import it.uniba.dib.sms22235.entities.operations.Interval;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class QueryPurchasesManager {

    private final DBHelper dbHelper;

    public QueryPurchasesManager(Context context){
        dbHelper = new DBHelper(context);
    }

    /**
     * This method is used to drop the current purchase table
     * and recreate the same table
     * */
    public void dropTableAndRefresh(){
        try {
            dbHelper.getWritableDatabase().execSQL("DROP TABLE IF EXISTS purchases;");
        } catch (SQLException e){
            Log.e("DROP ERROR", e.getMessage());
        }

        try {
            dbHelper.getWritableDatabase().execSQL(KeysNamesUtils.PurchaseFields.CREATE_TABLE);
        } catch (SQLException e) {
            Log.e("CREATE ERROR", e.getMessage());
        }
    }

    /**
     * This method is used to insert into purchase table a new purchase record
     *
     * @param animal the animal of the purchase
     * @param itemName the name of the bought item
     * @param owner the owner of the animal who's registering the purchase
     * @param date the date of the purchase
     * @param category the category of the item
     * @param cost the cost of the purchase
     * @param amount the amount of item bought
     *
     * @return the number of rows of the table if the query is run successfully. -1 otherwise
     * */
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
        }catch(SQLiteException e){

            Log.d("INSERT ERROR", e.getMessage());
        }

        return testValue;
    }

    /**
     * This method runs a query which search in the purchase table for all the purchases
     * which math the input conditions.
     *
     * @param animals a list of the selected animals to search. Null if not present
     * @param categories a list of the select categories to search. Null if not present
     * @param costs an interval of cost to search. Null if not present
     *
     * @return a cursor with the result of the query
     * */
    public Cursor runFilterQuery(List<String> animals, List<String> categories, Interval<Float> costs) {

        String query = buildQueryString(animals, categories, costs);
        Cursor cursor = null;

        try{
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, null);

        }catch(SQLiteException e){
            // todo: think to good error name
            Log.d("Cursor error", "errore nel lancio della query");
        }

        return cursor;
    }

    /**
     * This query search for the input item into purchase table.
     *
     * @param itemName the name of the item to search
     * @param username the owner of the purchase
     * */
    public Cursor getPurchaseByItemNameQuery(String itemName, String username) {
        String query = "SELECT * FROM " + KeysNamesUtils.CollectionsNames.PURCHASES +
                " WHERE " + KeysNamesUtils.PurchaseFields.ITEM_NAME + " LIKE '%" + itemName + "%'" +
                " AND " + KeysNamesUtils.PurchaseFields.OWNER + " = '" + username + "';";

        Cursor cursor = null;

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, null);
        } catch (SQLiteException e) {
            Log.d("Cursor error", "errore nel lancio della query");
        }

        return cursor;
    }

    public Cursor getMinimumPurchaseValue(String username){
        String query = "SELECT MIN(" + KeysNamesUtils.PurchaseFields.COST + ") AS minCost" +
                " FROM " + KeysNamesUtils.CollectionsNames.PURCHASES +
                " WHERE " + KeysNamesUtils.PurchaseFields.OWNER + " = '" + username + "';";

        Cursor cursor = null;

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, null);
        } catch (SQLException e) {
            Log.d("Cursor error", "errore nel lancio della query");
        }

        return cursor;
    }

    public Cursor getMaximumPurchaseValue(String username){
        String query = "SELECT MAX(" + KeysNamesUtils.PurchaseFields.COST + ") AS maxCost" +
                " FROM " + KeysNamesUtils.CollectionsNames.PURCHASES +
                " WHERE " + KeysNamesUtils.PurchaseFields.OWNER + " = '" + username + "';";

        Cursor cursor = null;

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, null);
        } catch (SQLException e) {
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

                where.append(")");

            }
            // SE C'E' IL FILTRO CATEGORIE
            if (categories != null) {
                // SE E' GIA' STATO INSERITO UN FILTRO AGGIUNGE AND
                if (!where.toString().equals(empty_where.toString())) {
                    where.append(" AND ");
                }

                where.append(KeysNamesUtils.PurchaseFields.CATEGORY).append(" IN (");

                for (int i = 0; i < categories.size(); i++) {
                    where.append("'").append(categories.get(i)).append("'");
                    if (i < (categories.size() - 1))
                        where.append(",");
                }

                where.append(")");

            }

            // SE C'E' IL FILTRO COSTI
            if (costs != null) {
                if (!where.toString().equals(empty_where.toString())) {
                    where.append(" AND ");
                }
                where.append(KeysNamesUtils.PurchaseFields.COST).append(" BETWEEN ").append(costs.getMin()).append(" AND ")
                        .append(costs.getMax());
            }

            where.append(";");
            query.append(where);
        }

        return query.toString();
    }


}
