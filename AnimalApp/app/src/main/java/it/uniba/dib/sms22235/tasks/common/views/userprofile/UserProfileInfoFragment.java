package it.uniba.dib.sms22235.tasks.common.views.userprofile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This fragment displays the profile info of the current user
 * */
public class UserProfileInfoFragment extends Fragment {

    private Passionate passionate;
    private Organization organization;
    private Veterinarian veterinarian;
    private LinkedHashMap<String, String> additionalInfo;

    private String fileName;
    private ImageView passionatePicPreview;

    private UserProfileInfoFragmentListener listener;

    // Used to launch the callback to retrieve intent's results
    private final ActivityResultLauncher<Intent> photoUploadAndSaveActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();
                    final Uri imageUri = data.getData();
                    final InputStream imageStream;

                    try {
                        imageStream = getContext().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        Glide.with(getContext()).load(selectedImage).into(passionatePicPreview);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (data != null) {
                        NavigationActivityInterface activity = (NavigationActivityInterface) requireActivity();

                        listener.saveImageProfile(
                                activity.getStorageInstance(),
                                data.getData(),
                                activity.getUserId(),
                                getContext()
                        );
                    }
                }
            });

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (UserProfileInfoFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
                Object obj = arguments.getSerializable(KeysNamesUtils.BundleKeys.USER_PROFILE);
                additionalInfo = (LinkedHashMap<String, String>) arguments
                        .getSerializable(KeysNamesUtils.BundleKeys.USER_PROFILE_INFO);

                if (obj instanceof Passionate) {
                    passionate = (Passionate) obj;
                    fileName = "pic_" + passionate.getUsername();
                } else if (obj instanceof Veterinarian) {
                    veterinarian = (Veterinarian) obj;
                    fileName = "pic_" + veterinarian.getEmail();
                } else if (obj instanceof Organization) {
                    organization = (Organization) obj;
                    fileName = "pic_" + organization.getEmail();
                }
        }

        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.GONE);
        ((NavigationActivityInterface) requireActivity()).setNavViewVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_user_profile_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // This can be done because only one of these three objects will be different from null
        if (passionate != null) {
            setEmailAndName(passionate.getEmail(), passionate.getFullName(), view);
        } else if (organization != null) {
            setEmailAndName(organization.getEmail(), organization.getOrgName(), view);
        } else if (veterinarian != null) {
            setEmailAndName(veterinarian.getEmail(), veterinarian.getFullName(), view);
        }

        TextView txtInfo = view.findViewById(R.id.txtInfo);

        for (String currentKey : additionalInfo.keySet()) {
            String line = "<b>• " + currentKey + ": </b>" + additionalInfo.get(currentKey) + "<br>";
            txtInfo.append((Html.fromHtml(line, Html.FROM_HTML_MODE_LEGACY)));
        }

        NavigationActivityInterface activity = (NavigationActivityInterface) requireActivity();

        passionatePicPreview = view.findViewById(R.id.passionatePicPreview);

        listener.loadImageProfile(
                activity.getStorageInstance(),
                passionatePicPreview,
                activity.getUserId(),
                getContext()
        );

        passionatePicPreview.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            photoUploadAndSaveActivity.launch(i);
        });
    }

    private void setEmailAndName(String email, String name, @NonNull View view) {
        ((TextView) view.findViewById(R.id.txtName)).setText(name);
        ((TextView) view.findViewById(R.id.txtEmail)).setText(email);
    }
}
