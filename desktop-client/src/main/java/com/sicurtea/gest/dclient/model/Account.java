package com.sicurtea.gest.dclient.model;

import java.util.Set;

public class Account {
    String fiscalCode;
    String name;
    String numberVAT;
    String legalAddress;
    String category;
    Set<Contact> contacts;

    private class Contact{
        //TODO enum description types
        String contact;


    }
}