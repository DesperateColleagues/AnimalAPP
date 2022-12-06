package it.uniba.dib.sms22235.utils;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import it.uniba.dib.sms22235.R;

public class KeysNamesUtils {
    public static class RolesNames {
        public static String VETERINARIAN = "vet";
        public static String COMMON_USER = "cus";
        public static String ORGANIZATION = "org";
        public static String ANIMAL = "ani";
    }

    public static class CollectionsNames {
        public static String ACTORS = "actors";
        public static String PURCHASES = "purchases";
        public static String ANIMALS = "animals";
        public static String OWNERSHIP = "ownership";
        public static String REQUESTS = "request";
        public static String REPORTS = "reports";
        public static String RESERVATIONS = "reservations";
    }

    public static class ActorFields {
        public static String FULL_NAME = "fullName";
        public static String EMAIL = "email";
        public static String USERNAME = "username";
        public static String CLINIC_NAME = "clinicName";
        public static String ORG_NAME = "orgName";
        public static String PHONE_NUMBER = "phoneNumber";
        public static String PURPOSE = "purpose";
    }

    public static class AnimalFields {
        public static String NAME = "name";
        public static String ANIMAL_SPECIES = "animalSpecies";
        public static String RACE = "race";
        public static String MICROCHIP_CODE = "microchipCode";
        public static String BIRTH_DATE = "birthDate";
        public static String OWNER = "owner";
    }

    public static class ReservationFields {
        public static String OWNER = "owner";
        public static String ANIMAL = "animal";
        public static String VETERINARIAN = "veterinarian";
        public static String DATE = "date";
        public static String TIME = "time";
        public static String DIAGNOSIS = "diagnosis";
    }
        
    public static class PurchaseFields {
        public static String ID = "id";
        public static String ANIMAL = "animal";
        public static String ITEM_NAME = "itemName";
        public static String DATE = "date";
        public static String CATEGORY = "category";
        public static String COST = "cost";
        public static String AMOUNT = "amount";
        public static String OWNER = "owner";

        public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS purchases " + " (\n" +
                 ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"+
                ANIMAL + " VARCHAR(255) NOT NULL,\n" +
                ITEM_NAME + " VARCHAR(255) NOT NULL,\n" +
                OWNER + " VARCHAR(255) DEFAULT NULL, \n" +
                CATEGORY + " VARCHAR(255) NOT NULL,\n" +
                DATE + " VARCHAR(100) NOT NULL,\n" +
                AMOUNT + " INTEGER NOT NULL,\n" +
                COST + " DECIMAL NOT NULL); ";
    }

    public static class PurchaseCategory {
        public static int CLEANING = R.drawable.ic_baseline_clean_hands_24;
        public static int MEDIC = R.drawable.ic_baseline_medical_services_24;
        public static int FOOD = R.drawable.ic_baseline_pets_24;
        public static int ENJOYMENT = R.drawable.ic_baseline_celebration_24;
    }

    public static class BundleKeys {
        public static String PASSIONATE = "passionate";
        public static String VETERINARIAN = "veterinarian";
        public static String VETERINARIAN_RESERVATIONS = "veterinarian_reservations";
        public static String PASSIONATE_PURCHASES = "purchases";
        public static String PASSIONATE_ANIMALS = "animals";
        public static String INTERFACE = "INT";
    }

    public static class FileDirsNames {
        public static String BASE_PATH = "/data/user/0/it.uniba.dib.sms22235/";
        public static String ROOT_PREFIX = "app_";
        public static String PROFILE_IMAGES = "AnimalAPP_images";

        /**
         * Method that helps to obtain the name of the directory where posts' images
         * are saved
         * @param username the username used to build the directory
         * */
        @NonNull
        @Contract(pure = true)
        public static String passionatePostDirName(String username) {
            return username + "_post";
        }

        /**
         * Method that helps to obtain the name of the directory where posts' references
         * are saved
         * @param username the username used to build the directory
         * */
        @NonNull
        @Contract(pure = true)
        public static String passionatePostRefDirName(String username) {
            return username + "_post_ref";
        }
    }
}
