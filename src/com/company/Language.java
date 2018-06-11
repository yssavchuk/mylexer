package com.company;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


public class Language {
    public static String[] KEYWORDS = new String[]{"default", "do", "double", "if", "else", "int"};
    public static Character[] PUNCUAITONS = new Character[] {'-','+','=','{','}','[',']',';',':','"','<','>','?',',','/',' ', '\n', '\t', '!','%','&','*','(',')'};
    public static final String[] LITERALS = new String[]{"true", "false", "null"};

    public static boolean isKeyword(String word)
    {
        return Arrays.asList(KEYWORDS).contains(word);
    }

    public static boolean isPunctuation(char ch)
    {
        return Arrays.asList(PUNCUAITONS).contains(ch);
    }
    public static boolean isLiteral(String word)
    {
        return Arrays.asList(LITERALS).contains(word);
    }
};
