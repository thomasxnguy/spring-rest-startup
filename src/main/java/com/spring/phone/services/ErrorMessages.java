package com.spring.phone.services;

public abstract class ErrorMessages {

    public static final String NOT_NUMERICAL_PHONE_NUMBER = "Canonical phone number should be only numeric";
    public static final String NON_NUMERICAL_ID = "Wrong type of argument, id should be numerical";

    public static final String NO_LINKAGE_FOUND = "No linkage found";

    public static final String NOT_UNIQUE_RESULT = "Non unique result in response";

    public static final String EMPTY_HASH_REQUEST = "Empty hash in request";

    private ErrorMessages() {
        throw new IllegalAccessError("For static use only");
    }
}
