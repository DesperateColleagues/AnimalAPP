package it.uniba.dib.sms22235.adapters.purchases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;

/**
 * Adapter to give a representation to the category of purchases
 * */
public class GridViewCategoryAdapter extends BaseAdapter {

    private final Context context;//the context of the application
    private final ArrayList<Integer> categories;
    private final ArrayList<String> categoriesString;

    public GridViewCategoryAdapter(Context context){
        this.context = context;

        categories = new ArrayList<>();
        categoriesString = new ArrayList<>();

        categories.add(R.drawable.ic_baseline_clean_hands_24);
        categories.add(R.drawable.ic_baseline_celebration_24);
        categories.add(R.drawable.ic_baseline_pets_24);
        categories.add(R.drawable.ic_baseline_medical_services_24);

        categoriesString.add(context.getResources().getString(R.string.toelettatura));
        categoriesString.add(context.getResources().getString(R.string.divertimento));
        categoriesString.add(context.getResources().getString(R.string.cibo));
        categoriesString.add(context.getResources().getString(R.string.spese_mediche));
    }

    /**
     * This method, that overrides the standard one, is used to retrieve the number of items that are
     * present in the ArrayList of this adapter.
     * */
    @Override
    public int getCount() {
        return categoriesString.size();
    }

    /**
     * This method, that overrides the standard one, is used to retrieve the category at the specified position in the arraylist.
     *
     * @param position      the position of the category
     *
     * @return the object in that specific position
     * */
    @Override
    public Object getItem(int position) {
        return categoriesString.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * This method, that overrides the standard one, is used to build the ListView row by row.
     * It gets the view as a parameter, it adds all the elements that needs to be displayed and then it return it back.
     *
     * @param index             the position of the item in the ListView
     * @param view              the view
     * @param viewGroup         the view group
     *
     * @return listView     a view which contains the ListView
     * */
    @SuppressLint("InflateParams")
    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        View gridView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.item_fragment_dialog_add_category, null);

            ImageView imageView = gridView.findViewById(R.id.categoryImage);
            TextView textView = gridView.findViewById(R.id.txtImageCategory);

            imageView.setImageResource(categories.get(index));
            textView.setText(categoriesString.get(index));
        }
        else {
            gridView = view;
        }

        return gridView;
    }

}