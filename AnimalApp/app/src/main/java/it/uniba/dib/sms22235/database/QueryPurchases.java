package it.uniba.dib.sms22235.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.entities.operations.Interval;
import it.uniba.dib.sms22235.entities.operations.Purchase;

public class QueryPurchases {

    private DBHelper dbHelper;
    private Context context;

    public QueryPurchases(Context context){
        dbHelper = new DBHelper(context);
    }

    public void insertPurchase(String animal, String itemName, String owner, String date,
                               int category, float cost, int amount) {
        // todo: implement db insert
    }

    public void runQuery(List<String> animals, List<String> categories, Interval<Float> costs) {
        // todo: build a query string using the filters
    }

    private String buildQueryString() {
        // todo: implement query builder
        return "";
    }


}
