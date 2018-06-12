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
                case COMMENT:
                    next = fromComment(ch); break;
                case MULTILINE_COMMENT:
                    next = fromMultilineComment(ch);break;
                case SINGLELINE_COMMENT:
                    next = fromSinglelineComment(ch);break;
                case MULTILINE_COMMENT_STAR:
                    next = fromMultilineCommentStar(ch);break;
                case OPER_FIRST_SYM:
                    next = fromOperFirstSym(ch);break;
                case STRING_LITERAL:
                    next = fromStringLiteral(ch);break;
                case STRING_LITERAL_SYMBOL:
                    next = fromStringLiteralSymbol(ch);break;
                case STRING_LITERAL_BODY:
                    next = fromStringLiteralBody(ch);break;
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
        else if ( ch == '&' ||  ch == '|' ){
            curState = State.OPER_FIRST_SYM;
        }
        else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
            curState = State.IDENTIFIER;
        }
        else if (ch == '\"')
            curState = State.STRING_LITERAL;
        else if (ch == '?' || ch == ':') {
            tokens.add(new Token(TokenType.OPERATOR, Character.toString(ch)));
            setStartState();
            return true;
        }
        else if (ch == '/'){
            curState = State.COMMENT;
        }
        else{
            if(Language.isPunctuation(ch))
                tokens.add(new Token(TokenType.PUNCTUATION, Character.toString(ch)));
            else
                tokens.add(new Token(TokenType.ERROR, Character.toString(ch)));
            setStartState();
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

    public boolean fromComment(Character ch){
        if (ch == '/') {
            curState = State.SINGLELINE_COMMENT;
            return true;
        }
        else if (ch == '=') {
            tokens.add(new Token(TokenType.OPERATOR, buffer + Character.toString(ch)));
            setStartState();
            return true;
        }
        else if (ch == '*') {
            curState = State.MULTILINE_COMMENT;
            return true;
        }
        else {
            tokens.add(new Token(TokenType.OPERATOR, buffer));
            setStartState();
            return false;
        }
    }

    public boolean fromMultilineComment(Character ch){
        if (ch == '*') {
            curState = State.MULTILINE_COMMENT_STAR;
        }
        return true;
    }

    public boolean fromSinglelineComment(Character ch){
        if (ch == '\r' || ch == '\n') {
            tokens.add(new Token(TokenType.COMMENT, buffer));
            setStartState();
            return false;
        }
        else
            return true;
    }

    public boolean fromMultilineCommentStar(Character ch){
        if (ch == '/') {
            tokens.add(new Token(TokenType.COMMENT, buffer + Character.toString(ch)));
            setStartState();
            return true;
        }
        else if(ch != '*'){
            curState = State.MULTILINE_COMMENT;
        }
        return true;
    }

    public boolean fromOperFirstSym(Character ch){
        if (ch == '=' || Character.toString(ch).equals(buffer)) {
            tokens.add(new Token(TokenType.OPERATOR, buffer + Character.toString(ch)));
            setStartState();
            return true;
        }
        else {
            tokens.add(new Token(TokenType.OPERATOR, buffer));
            setStartState();
            return false;
        }
    }

    public boolean fromStringLiteral(Character ch){
        if (ch == '\"'){
            tokens.add(new Token(TokenType.LITERAL, buffer + Character.toString(ch)));
            setStartState();
            return true;
        }
        else if (ch == '\n') {
            tokens.add(new Token(TokenType.ERROR, buffer));
            setStartState();
            return false;
        } else if (ch == '\\') {
            curState = State.STRING_LITERAL_SYMBOL;
             return true;
        } else {
            curState = State.STRING_LITERAL_BODY;
            return true;
        }
    }

    public boolean fromStringLiteralSymbol(Character ch){
        if (ch == 'n' || ch == 't' || ch == '\\' || ch == '\'' || ch == '\"' || ch == '\n' || ch == '\r') {
            curState = State.STRING_LITERAL_BODY;
            return true;
        }
        else {
            curState = State.ERROR;
            return true;
        }
    }

    public boolean fromStringLiteralBody(Character ch){
        if (ch == '\r' || ch == '\n') {
            tokens.add(new Token(TokenType.ERROR, buffer));
            setStartState();
            return false;
        } else if (ch == '\\') {
            curState = State.STRING_LITERAL_SYMBOL;
            return true;
        } else if (ch == '\"') {
            tokens.add(new Token(TokenType.LITERAL, buffer + Character.toString(ch)));
            setStartState();
            return true;
        } else if (fileEnded) {
            tokens.add(new Token(TokenType.ERROR, buffer));
            return true;
        } else
            return true;
    }

    public void fromFinalState(){
        switch (curState){
            case START:
                return;
            case NUMBER_DECIMAL:
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
            case COMMENT:
                fromComment('\n');break;
            case SINGLELINE_COMMENT:
                fromSinglelineComment('\n');break;
            case MULTILINE_COMMENT:
                tokens.add(new Token(TokenType.ERROR, "Unclosed comment"));break;
            case MULTILINE_COMMENT_STAR:
                tokens.add(new Token(TokenType.ERROR, "Unclosed comment"));break;
            case OPER_FIRST_SYM:
                fromOperFirstSym('\n');break;
            case STRING_LITERAL_SYMBOL:
                tokens.add(new Token(TokenType.ERROR, "Unclosed string literal"));break;
            case STRING_LITERAL_BODY:
                tokens.add(new Token(TokenType.ERROR, "Unclosed string literal"));break;
            case STRING_LITERAL:
                tokens.add(new Token(TokenType.ERROR, "Unclosed string literal"));break;

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
