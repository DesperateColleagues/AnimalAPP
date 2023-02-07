package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class PokeLink implements Serializable {
    private String id;
    private String passionateAnimal;
    private String otherAnimal;
    private String passionateAnimalUri;
    private String otherAnimalUri;
    private String type;
    private String description;
    public String passionateEmail;

    public PokeLink() {}

    public PokeLink(String id, String passionateAnimal, String otherAnimal, String passionateAnimalUri,
                    String otherAnimalUri, String type, String description, String passionateEmail) {
        this.passionateAnimal = passionateAnimal;
        this.otherAnimal = otherAnimal;
        this.passionateAnimalUri = passionateAnimalUri;
        this.otherAnimalUri = otherAnimalUri;
        this.type = type;
        this.description = description;
        this.passionateEmail = passionateEmail;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPassionateAnimal() {
        return passionateAnimal;
    }

    public String getOtherAnimal() {
        return otherAnimal;
    }

    public String getPassionateAnimalUri() {
        return passionateAnimalUri;
    }

    public String getOtherAnimalUri() {
        return otherAnimalUri;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokeLink pokeLink = (PokeLink) o;
        return id.equals(pokeLink.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Contract("_ -> new")
    public static PokeLink loadPokeLink(@NonNull DocumentSnapshot document) {
        return new PokeLink(
                document.getString(KeysNamesUtils.PokeLinkFields.ID),
                document.getString(KeysNamesUtils.PokeLinkFields.PASSIONATE_ANIMAL),
                document.getString(KeysNamesUtils.PokeLinkFields.OTHER_ANIMAL),
                document.getString(KeysNamesUtils.PokeLinkFields.PASSIONATE_ANIMAL_URI),
                document.getString(KeysNamesUtils.PokeLinkFields.OTHER_ANIMAL_URI),
                document.getString(KeysNamesUtils.PokeLinkFields.TYPE),
                document.getString(KeysNamesUtils.PokeLinkFields.DESCRIPTION),
                document.getString(KeysNamesUtils.PokeLinkFields.PASSIONATE_EMAIL)
        );
    }
}
