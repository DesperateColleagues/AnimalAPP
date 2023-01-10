package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAnimalCardFragment;
import it.uniba.dib.sms22235.activities.passionate.fragments.animalprofile.DiagnosisFragment;
import it.uniba.dib.sms22235.activities.passionate.fragments.animalprofile.PhotoDiaryFragment;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class AnimalProfile extends Fragment {

    public interface AnimalProfileListener {
        /**
         * This method is used to save to DB the animal profile pic
         *
         * @param source the source image
         * @param microchip the animal microchip code
         * */
        void onProfilePicAdded(Uri source, String microchip);

        /**
         * This method is used to load from firebase the profile pic and to put it into an
         * imageview
         *
         * @param microchip the microchip of the animal
         * @param imageView the image view where the image will be loaded
         * */
        void loadProfilePic(String microchip, ImageView imageView);
    }

    private Animal mAnimal;
    private AnimalProfileListener listener;

    // Used to launch the callback to retrieve intent's results
    private final ActivityResultLauncher<Intent> photoUploadAndSaveActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        listener.onProfilePicAdded(data.getData(), mAnimal.getMicrochipCode());
                    }
                }
            });

    @Override
    public void onAttach(@NonNull Context context) {
        PassionateNavigationActivity activity = (PassionateNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (AnimalProfileListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((PassionateNavigationActivity) requireActivity()).setNavViewVisibility(View.GONE);
        ((PassionateNavigationActivity) requireActivity()).getFab().setVisibility(View.GONE);

        Bundle arguments = getArguments();

        // Get the arguments obtained from the navigation
        if (arguments != null) {
            mAnimal = (Animal) arguments.get(KeysNamesUtils.BundleKeys.ANIMAL);
        }

        return inflater.inflate(R.layout.fragment_passionate_animal_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String info = "- <b>Specie</b>: " + mAnimal.getAnimalSpecies() +
                "\n<br>- <b>Razza</b>: " + mAnimal.getRace() +
                "\n<br>- <b>Data di nascita</b>: " + mAnimal.getBirthDate() +
                "\n<br>- <b>Codice microchip</b>: " + mAnimal.getMicrochipCode();

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = view.findViewById(R.id.result_tabs);
        tabLayout.setupWithViewPager(viewPager);

        ImageView animalPicPreview = view.findViewById(R.id.animalPicPreview);
        listener.loadProfilePic(mAnimal.getMicrochipCode(), animalPicPreview);

        Button shareProfile = view.findViewById(R.id.btnShareProfile);
        shareProfile.setOnClickListener(v -> {
            DialogAnimalCardFragment animalCardFragment = new DialogAnimalCardFragment(mAnimal);
            animalCardFragment.setAnimalProfileListener(listener);
            animalCardFragment.show(getChildFragmentManager(), "DialogAnimalCardFragment");
        });

        // Set the text view with the mAnimal data
        if (mAnimal != null) {
            TextView txtAnimalNameProfile = view.findViewById(R.id.requestTitle);
            TextView txtInfoAnimal = view.findViewById(R.id.txtInfoAnimal);

            txtAnimalNameProfile.setText(mAnimal.getName());

            txtInfoAnimal.setText(Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY));
        }

        animalPicPreview.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            photoUploadAndSaveActivity.launch(i);
        });
    }

    private Bitmap generateBitmap(){
        //inflate layout
        @SuppressLint("InflateParams") LinearLayout root = (LinearLayout) LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_dialog_animal_card, null, false);

        ((TextView) root.findViewById(R.id.txtAnimalCardName)).setText(mAnimal.getName());
        ((TextView) root.findViewById(R.id.txtAnimalCardSpecies)).setText(mAnimal.getAnimalSpecies());
        ((TextView) root.findViewById(R.id.txtAnimalCardRace)).setText(mAnimal.getRace());
        ((TextView) root.findViewById(R.id.txtAnimalCardMicrochipCode)).setText(mAnimal.getMicrochipCode());
        ((TextView) root.findViewById(R.id.txtAnimalCardBirthDate)).setText(mAnimal.getBirthDate());

        ImageView imgAnimalCardPhoto = root.findViewById(R.id.animalCardProfileSend);
        listener.loadProfilePic(mAnimal.getMicrochipCode(), imgAnimalCardPhoto);

        //reference View with image
        root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(root.getMeasuredWidth(), root.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());
        root.draw(canvas);

        return bitmap;
    }

    @NonNull
    private View prepareViewImage() {
        @SuppressLint("InflateParams") LinearLayout root = (LinearLayout) LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_dialog_animal_card, null, false);

        ((TextView) root.findViewById(R.id.txtAnimalCardName)).setText(mAnimal.getName());
        ((TextView) root.findViewById(R.id.txtAnimalCardSpecies)).setText(mAnimal.getAnimalSpecies());
        ((TextView) root.findViewById(R.id.txtAnimalCardRace)).setText(mAnimal.getRace());
        ((TextView) root.findViewById(R.id.txtAnimalCardMicrochipCode)).setText(mAnimal.getMicrochipCode());
        ((TextView) root.findViewById(R.id.txtAnimalCardBirthDate)).setText(mAnimal.getBirthDate());

        ImageView imgAnimalCardPhoto = root.findViewById(R.id.animalCardProfileSend);
        listener.loadProfilePic(mAnimal.getMicrochipCode(), imgAnimalCardPhoto);

        return root;
    }

    private void setupViewPager(@NonNull ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new PhotoDiaryFragment(mAnimal.getMicrochipCode()), "Photo diary");
        adapter.addFragment(new DiagnosisFragment(), "Diagnosi");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}


