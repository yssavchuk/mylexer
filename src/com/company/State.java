package com.company;

public enum State {
    NUMBER, NUMBER_DECIMAL, NUMBER_INTEGER, //0-9 .
    START,
    ERROR,
    PRE_EQUAL_OPERATOR, //= % ! + - < >
    DOT, //.
    IDENTIFIER
}
