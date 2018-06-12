package com.company;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class Lexer {

    ArrayList<Token> tokens = new ArrayList<>();
    String buffer = "";
    State curState = State.START;
    boolean fileEnded = false;

    public Lexer(String filePath){
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            int c;

            while ((c = br.read()) != -1) {
                nextStep((char)c);
                System.out.println(c);
            }

            br.close();
            fromFinalState();
            System.out.println("Final state is " + curState);
            fileEnded = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextStep(Character ch){
        boolean next = false;
        while(!next){
            System.out.println(curState.toString());
            switch (curState){
                case START:
                    next = fromStart(ch);break;
                case NUMBER:
                    next = fromNumber(ch);break;
                case NUMBER_DECIMAL:
                    next = fromNumberDecimal(ch);break;
                case PRE_EQUAL_OPERATOR:
                    next = fromPreEqualOperator(ch);break;
                case DOT:
                    next = fromDot(ch);break;
                case IDENTIFIER:
                    next = fromIdentifier(ch);break;
                case ERROR:
                    next = fromError(ch);break;
                case PRE_NUMBER_OPERATOR:
                    next = fromPreNumber(ch); break;
            }
        }

        if(curState != State.START) {
            buffer += ch;
        }
    }

    public boolean fromStart(Character ch){
        if (ch >= '0' && ch <= '9') {
            curState = State.NUMBER;
        }
        else if ( ch == '<' || ch == '>' || ch == '=' || ch == '%' || ch == '!' ){
            curState = State.PRE_EQUAL_OPERATOR;
        }
        else if ( ch == '-' || ch == '+'){
            curState = State.PRE_NUMBER_OPERATOR;
        }
        else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
            curState = State.IDENTIFIER;
        }
        else if (ch == '?' || ch == ':') {
            tokens.add(new Token(TokenType.OPERATOR, Character.toString(ch)));
            setStartState();
            return true;
        }
        else{
            if(Language.isPunctuation(ch))
                tokens.add(new Token(TokenType.PUNCTUATION, Character.toString(ch)));
            else
                tokens.add(new Token(TokenType.ERROR, Character.toString(ch)));
            setStartState();
            return true;
        }
        return true;
    }

    public boolean fromNumber(Character ch){
        if (ch >= '0' && ch <= '9') {
            curState = State.NUMBER;
            return true;
        }
        else if (ch == '.'){
            curState = State.NUMBER_DECIMAL;
            return true;
        }
        else{
            return afterNumber(ch);
        }
    }

    public boolean fromNumberDecimal(Character ch){
        if (ch >= '0' && ch <= '9') {
            curState = State.NUMBER_DECIMAL;
            return true;
        }
        else if (ch == '.'){
            curState = State.ERROR;
            return true;
        }
        else{
            return afterNumber(ch);
        }
    }

    public boolean fromPreEqualOperator(Character ch){
        if (ch == '=') {
            tokens.add(new Token(TokenType.OPERATOR, buffer + '='));
            setStartState();
            return true;
        }
        else {
            tokens.add(new Token(TokenType.OPERATOR, buffer));
            setStartState();
            return false;
        }
    }

    public boolean fromDot(Character ch){
        if (ch >= '0' && ch <= '9') {
            curState = State.NUMBER_DECIMAL;
            return true;
        }
        else {
            tokens.add(new Token(TokenType.OPERATOR, buffer));
            setStartState();
            return false;
        }
    }

    public boolean fromIdentifier(Character ch){
        if (!((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || ch == '_')) {
            if (Language.isPunctuation(ch) || ch == '.') {
                tokens.add(new Token(TokenType.IDENTIFIER, buffer));
                setStartState();
                return false;
            } else {
                tokens.add(new Token(TokenType.ERROR, buffer));
                setStartState();
                return true;
            }
        }
        return true;
    }


    public boolean fromError(Character ch){
        if (Language.isPunctuation(ch)) {
            tokens.add(new Token(TokenType.ERROR, buffer));
            setStartState();
            return false;
        }
        else
            return true;
    }

    public boolean fromPreNumber(Character ch){
        if (ch == '=' || ch == '-' || ch == '+') {
            tokens.add(new Token(TokenType.OPERATOR, buffer + Character.toString(ch)));
            setStartState();
            return true;
        }
        else if (ch >= '0' && ch <= '9') {
            curState = State.NUMBER;
            return true;
        } else {
            tokens.add(new Token(TokenType.OPERATOR, buffer));
            setStartState();
            return false;
        }
    }

    public void fromFinalState(){
        switch (curState){
            case START:
                return;
            case NUMBER_DECIMAL:
                afterNumber('\n');break;
            case NUMBER_INTEGER:
                afterNumber('\n');break;
            case NUMBER:
                afterNumber('\n');break;
            case IDENTIFIER:
                fromIdentifier('\n');break;
            case ERROR:
                fromError('\n');break;
            case DOT:
                fromDot('\n');break;
            case PRE_EQUAL_OPERATOR:
                fromPreEqualOperator('\n');break;
            case PRE_NUMBER_OPERATOR:
                fromPreNumber('\n');break;
        }
    }
    ////////////////////////////////////////////////////////////////////////
    public boolean afterNumber(Character ch){
        if(Language.isPunctuation(ch)) {
            if (curState == State.NUMBER_DECIMAL) {
                tokens.add(new Token(TokenType.NUMBER, buffer));
                setStartState();
                return false;
            }
            else{
                tokens.add(new Token(TokenType.NUMBER, buffer));
                setStartState();
                return false;
            }
        }
        else {
            curState = State.ERROR;
            return false;
        }
    }

    public void setStartState(){
        buffer = "";
        curState = State.START;
    }

    public void printTokens(){
        System.out.println("Print tokens ------------------------------------------------");
        for (Token t: this.tokens)
            if (!(t.getTokenType() == TokenType.PUNCTUATION && t.getContent().equals(" ")))
                System.out.println(t.getTokenType().toString() + ": " + t.getContent());
    }

}
