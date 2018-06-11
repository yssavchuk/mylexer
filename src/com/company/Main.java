package com.company;

public class Main {

    public static void main(String[] args) {
        Lexer lex = new Lexer("test/input.c");
        lex.printTokens();
    }
}
