package com.example.attendanceproject.helper;

public class validerFormatEmail {
    //Email Pattern
    public static boolean regexEmailValidationPattern(String email) {
        String regex = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})";
        if (email.matches(regex)) {
            return true;
        }
        return false;
    }


}
