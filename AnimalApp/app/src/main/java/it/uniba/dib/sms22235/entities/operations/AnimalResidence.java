package it.uniba.dib.sms22235.entities.operations;

import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Owner;

public class AnimalResidence {
    private Owner owner;
    private Animal animal;
    private String startDate;
    private String endDate;
    boolean isTemp;
}
