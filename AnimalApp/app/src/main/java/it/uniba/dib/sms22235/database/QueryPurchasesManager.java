package it.uniba.dib.sms22235.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import androidx.annotation.NonNull;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
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
            dbHelper.getWritableDatabase().execSQL(KeysNamesUtils.PurchaseContract.CREATE_TABLE);
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

        cv.put(KeysNamesUtils.PurchaseContract.COLUMN_NAME_ANIMAL, animal);
        cv.put(KeysNamesUtils.PurchaseContract.COLUMN_NAME_ITEM_NAME, itemName);
        cv.put(KeysNamesUtils.PurchaseContract.COLUMN_NAME_OWNER, owner);
        cv.put(KeysNamesUtils.PurchaseContract.COLUMN_NAME_DATE, date);
        cv.put(KeysNamesUtils.PurchaseContract.COLUMN_NAME_CATEGORY, category);
        cv.put(KeysNamesUtils.PurchaseContract.COLUMN_NAME_COST, cost);
        cv.put(KeysNamesUtils.PurchaseContract.COLUMN_NAME_AMOUNT, amount);

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
     * @param owner the owner of the purchases
     * @param animals a list of the selected animals to search. Null if not present
     * @param categories a list of the select categories to search. Null if not present
     * @param costs an interval of cost to search. Null if not present
     * @param dateFrom the lower bound of the date interval
     * @param dateTo the upper bound of the date interval
     *
     * @return the result set
     * */
    public Cursor runFilterQuery(String owner, List<String> animals, List<String> categories,
                                 Interval<Float> costs, String dateFrom, String dateTo) {

        String query = buildQueryString(owner, animals, categories, costs, dateFrom, dateTo);
        Cursor cursor = null;

        try{
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, buildSelectionArgs(
                    owner,
                    animals,
                    categories,
                    costs,
                    dateFrom,
                    dateTo
            ));

        }catch(SQLiteException e){
            Log.d("Cursor error", "Errore nell'ottenimento delle informazioni dal server interno");
        }

        return cursor;
    }

    /**
     * This query search for the input item into purchase table.
     *
     * @param itemName the name of the item to search
     * @param owner the owner of the purchase
     *
     * @return the result set
     * */
    public Cursor getPurchaseByItemNameQuery(String itemName, String owner) {
        String query = "SELECT * FROM " + KeysNamesUtils.CollectionsNames.PURCHASES +
                " WHERE " + KeysNamesUtils.PurchaseContract.COLUMN_NAME_ITEM_NAME + " LIKE ?" +
                " AND " + KeysNamesUtils.PurchaseContract.COLUMN_NAME_OWNER + " = ?;";

        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            itemName = "%" + itemName + "%";
            cursor = db.rawQuery(query, new String[] {itemName, owner});
        } catch (SQLiteException e) {
            Log.d("Cursor error", "errore nel lancio della query");
        }

        return cursor;
    }

    /**
     * Get the minimum purchase value (unitary cost) from a certain owner
     *
     * @param owner the owner of the purchases
     * @return the result set
     * */
    public Cursor getMinimumPurchaseValue(String owner) {
        String query = "SELECT MIN(" + KeysNamesUtils.PurchaseContract.COLUMN_NAME_COST + ") AS minCost" +
                " FROM " + KeysNamesUtils.CollectionsNames.PURCHASES +
                " WHERE " + KeysNamesUtils.PurchaseContract.COLUMN_NAME_OWNER + " = ?;";

        Cursor cursor = null;

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, new String[]{owner});
        } catch (SQLException e) {
            Log.d("Cursor error", "errore nel lancio della query");
        }

        return cursor;
    }

    /**
     * Get the maximum purchase value (unitary cost) from a certain owner
     *
     * @param owner the owner of the purchases
     * @return the result set
     * */
    public Cursor getMaximumPurchaseValue(String owner) {
        String query = "SELECT MAX(" + KeysNamesUtils.PurchaseContract.COLUMN_NAME_COST + ") AS maxCost" +
                " FROM " + KeysNamesUtils.CollectionsNames.PURCHASES +
                " WHERE " + KeysNamesUtils.PurchaseContract.COLUMN_NAME_OWNER + " = ?;";

        Cursor cursor = null;

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, new String[]{owner});
        } catch (SQLException e) {
            Log.d("Cursor error", "errore nel lancio della query");
        }

        return cursor;
    }

    /**
     * Build the raw query used for filter
     *
     * @param animals a list of the selected animals to search. Null if not present
     * @param categories a list of the select categories to search. Null if not present
     * @param costs an interval of cost to search. Null if not present
     * @param dateFrom the lower bound of the date interval
     * @param dateTo the upper bound of the date interval
     *
     * @return the raw query without the selection arguments
     */
    @NonNull
    private String buildQueryString(String owner, List<String> animals, List<String> categories,
                                    Interval<Float> costs, String dateFrom, String dateTo) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ")
                .append(KeysNamesUtils.CollectionsNames.PURCHASES)
                .append("\n");

        StringBuilder where = new StringBuilder();
        where.append("WHERE ")
                .append(KeysNamesUtils.PurchaseContract.COLUMN_NAME_OWNER)
                .append(" = ?");

        // If nof filters are present select all table's rows
        if (animals == null && categories == null && costs == null && dateFrom.equals("") && dateTo.equals("")){
            query.append(";");
        } else {
            // If there is the animal's filter
            if (animals != null) {
                where.append(" AND ");

                where.append(KeysNamesUtils.PurchaseContract.COLUMN_NAME_ANIMAL)
                        .append(" IN (")
                        .append(makePlaceholders(animals.size()))
                        .append(")");
            }

            // if there is category's filter
            if (categories != null) {
                where.append(" AND ")
                        .append(KeysNamesUtils.PurchaseContract.COLUMN_NAME_CATEGORY)
                        .append(" IN (")
                        .append(makePlaceholders(categories.size())).append(")");
            }

            // If cost filter is present
            if (costs != null) {
                where.append(" AND ");

                where.append(KeysNamesUtils.PurchaseContract.COLUMN_NAME_COST)
                        .append(" BETWEEN ")
                        .append("CAST(? AS DECIMAL)") // getMin
                        .append(" AND ")
                        .append("CAST(? AS DECIMAL)"); // getMax
            }

            // If dates filter is present
            if (!dateFrom.equals("") && !dateTo.equals("")) {
                where.append(" AND ");

                where.append(KeysNamesUtils.PurchaseContract.COLUMN_NAME_DATE)
                        .append(" BETWEEN ?")
                        .append(" AND ? ");
            }

            where.append(";");
            query.append(where);
        }

        return query.toString();
    }

    /**
     * */
    @NonNull
    private String [] buildSelectionArgs(String owner, List<String> animals, List<String> categories,
                                         Interval<Float> costs, String dateFrom, String dateTo) {
        ArrayList<String> selectionArgs = new ArrayList<>();

        selectionArgs.add(owner);

        if (animals != null) {
            selectionArgs.addAll(animals);
        }

        if (categories != null) {
            selectionArgs.addAll(categories);
        }

        if (costs != null) {
            selectionArgs.add("" + costs.getMin());
            selectionArgs.add("" + costs.getMax());
        }

        if (!dateFrom.equals("") && !dateTo.equals("")) {
            selectionArgs.add(dateFrom);
            selectionArgs.add(dateTo);
        }

        return selectionArgs.toArray(new String[0]);
    }

    /**
     * Returns a string with SQLiter place holders "?" based on input len
     *
     * @param len the number of place holders to generate
     * @return the place holders' string
     * */
    @NonNull
    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}
