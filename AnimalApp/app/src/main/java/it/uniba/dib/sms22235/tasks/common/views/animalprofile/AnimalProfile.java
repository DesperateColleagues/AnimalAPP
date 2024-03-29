package it.uniba.dib.sms22235.tasks.common.views.animalprofile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.Html;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.AbstractPersonUser;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.tasks.common.dialogs.requests.BsdDialogQr;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogAnimalCardFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.DiagnosisFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.ExamsFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.PhotoDiaryFragment;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogEditAnimalDataFragment;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This fragment is used to display the animal profile
 * */
public class AnimalProfile extends Fragment implements
        DialogEditAnimalDataFragment.DialogEditAnimalDataFragmentListener{

    /**
     * This interface is used to notify the events that occur in the AnimalProfile
     * */
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
         * @param fileName the name of the file of the profile pic
         * @param imageView the image view where the image will be loaded
         * */
        void loadProfilePic(String fileName, ImageView imageView);

        /**
         * This method is used when an animal is updated
         *
         * @param animal the updated animal to save
         * */
        void onAnimalUpdated(Animal animal);


        /**
         * This method is used to check if an animal is at home or on a
         * temporary residence
         *
         * @param animal the animal to be checked
         * @param image the imageView where to load the logo
         * */
        void checkIfAtHome(Animal animal, ImageView image);

    }

    /**
     * This interface describe the event that happens when the animal profile is edited
     * */
    public interface AnimalProfileEditListener {
        /**
         * This method is used to get the list of all the veterinarians present in the database
         *
         * @return the list of veterinarians
         * */
        List<Veterinarian> getVeterinarianList();

        /**
         * This method is called when the owner set a veterinarian for his pet
         *
         * @param selectedAnimal the selected animal
         * */
        void onDialogChoosedVeterinarian(@NonNull Animal selectedAnimal);
    }

    private Animal mAnimal;

    private AbstractPersonUser user;

    private AnimalProfileListener generalListener;
    private AnimalProfileEditListener passionateListener;

    private DialogEditAnimalDataFragment dialogEditAnimalDataFragment;

    private int viewMode;

    // Used to launch the callback to retrieve intent's results
    private final ActivityResultLauncher<Intent> photoUploadAndSaveActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        generalListener.onProfilePicAdded(data.getData(), mAnimal.getMicrochipCode());
                    }
                }
            });

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();

        Bundle arguments = getArguments();

        // Get the arguments obtained from the navigation
        if (arguments != null) {
            mAnimal = (Animal) arguments.get(KeysNamesUtils.BundleKeys.ANIMAL);
            viewMode = arguments.getInt(KeysNamesUtils.BundleKeys.VIEW_MODE);
            user = (AbstractPersonUser) arguments.getSerializable(KeysNamesUtils.BundleKeys.USER_OBJECT);
            Log.e("AnimalAPP - AnimalProfile", "AnimalProfile:144 - ViewMode: " + viewMode);
        }

        try {
            // Attach the listener to the Fragment
            generalListener = (AnimalProfileListener) context;
            if (((NavigationActivityInterface) requireActivity()).getUser().getPurpose().equals(KeysNamesUtils.RolesNames.COMMON_USER)) {
                passionateListener = (AnimalProfileEditListener) context;
            }
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
        ((NavigationActivityInterface) requireActivity()).setNavViewVisibility(View.GONE);
        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.GONE);


        return inflater.inflate(R.layout.fragment_animal_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialogEditAnimalDataFragment = new DialogEditAnimalDataFragment();
        dialogEditAnimalDataFragment.setListener(this);

        String info = "• <b>" +
                getResources().getString(R.string.specie_animale) +
                ": </b>"+
                mAnimal.getAnimalSpecies() +
                "\n<br>" +
                "• <b>" +
                getResources().getString(R.string.razza) +
                ": </b>"+
                mAnimal.getRace() +
                "\n<br>" +
                "• <b>" +
                getResources().getString(R.string.data_di_nascita) +
                ": </b>"+
                mAnimal.getBirthDate();

        if (((NavigationActivityInterface) requireActivity()).getUserId().equals(mAnimal.getOwner())) {
            info = info + "\n<br>" +
                    "• <b>" +
                    getResources().getString(R.string.codice_microchip) +
                    ": </b>"+
                    mAnimal.getMicrochipCode() +
                    "\n<br>";
        }

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = view.findViewById(R.id.result_tabs);

        tabLayout.setupWithViewPager(viewPager);

        ImageView animalPicPreview = view.findViewById(R.id.animalPicPreview);
        generalListener.loadProfilePic(
                KeysNamesUtils.FileDirsNames.animalProfilePic(mAnimal.getMicrochipCode()),
                animalPicPreview);

        Button updateProfile = view.findViewById(R.id.btnUpdateProfile);

        ImageButton shareProfile = view.findViewById(R.id.btnShareProfile);
        ImageButton btnQrCodeGenerator = view.findViewById(R.id.btnQrCodeGenerator);

        if (((NavigationActivityInterface) requireActivity()).getUser().getPurpose().equals(KeysNamesUtils.RolesNames.COMMON_USER) &&
                ((NavigationActivityInterface) requireActivity()).getUser() instanceof Passionate) {
            if (((NavigationActivityInterface) requireActivity()).getUser().getUserIdentifier().equals(mAnimal.getOwner())) {
                animalPicPreview.setOnClickListener(v -> {
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    photoUploadAndSaveActivity.launch(i);
                });

                shareProfile.setOnClickListener(v -> {
                    DialogAnimalCardFragment animalCardFragment = new DialogAnimalCardFragment(mAnimal);
                    animalCardFragment.setAnimalProfileListener(generalListener);
                    animalCardFragment.show(getChildFragmentManager(), "DialogAnimalCardFragment");
                });

                btnQrCodeGenerator.setOnClickListener(v -> {
                    BsdDialogQr bsdDialogQr = new BsdDialogQr(mAnimal.getMicrochipCode(), mAnimal.getName(), mAnimal.getOwner(), false);
                    bsdDialogQr.show(getChildFragmentManager(), "BsdDialogQr");
                });

                updateProfile.setOnClickListener(v -> {
                    dialogEditAnimalDataFragment.setAnimal(mAnimal);
                    dialogEditAnimalDataFragment.setVeterinarianList(passionateListener.getVeterinarianList());
                    dialogEditAnimalDataFragment.show(requireActivity().getSupportFragmentManager(),
                            "DialogEditAnimalDataFragment");
                });
            } else {
                updateProfile.setVisibility(View.GONE);
                shareProfile.setVisibility(View.GONE);
                btnQrCodeGenerator.setVisibility(View.GONE);
            }
        } else {
            updateProfile.setVisibility(View.GONE);
            shareProfile.setVisibility(View.GONE);
            btnQrCodeGenerator.setVisibility(View.GONE);
        }

        ImageView animalPosition = view.findViewById(R.id.animalPosition);

        animalPosition.setOnClickListener(view1 -> {
            Toast.makeText(getContext(), getResources().getString(R.string.a_casa_o_in_stallo), Toast.LENGTH_SHORT).show();
        });

        generalListener.checkIfAtHome(mAnimal, animalPosition);

        // Set the text view with the mAnimal data
        if (mAnimal != null) {
            TextView txtAnimalNameProfile = view.findViewById(R.id.requestTitle);
            TextView txtInfoAnimal = view.findViewById(R.id.txtInfoAnimal);

            txtAnimalNameProfile.setText(mAnimal.getName());
            txtInfoAnimal.setText(Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY));
        }

    }

    private void setupViewPager(@NonNull ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());

        adapter.addFragment(new PhotoDiaryFragment(mAnimal, user, viewMode), getString(R.string.photo_diary));
        adapter.addFragment(new DiagnosisFragment(mAnimal, user, viewMode), getString(R.string.title_fragment_diagnosis));
        adapter.addFragment(new ExamsFragment(mAnimal, user, viewMode),getString(R.string.title_fragment_exams));

        viewPager.setAdapter(adapter);
    }

    @SuppressWarnings("deprecation")
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

    @Override
    public void onDialogChoosedVeterinarian(@NonNull Animal selectedAnimal) {
        if ((getActivity()) instanceof PassionateNavigationActivity) {
            passionateListener.onDialogChoosedVeterinarian(mAnimal);
        }
        generalListener.onAnimalUpdated(mAnimal);
    }
}


