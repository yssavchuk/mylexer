package com.company;

public class Token {
    public String content;
    public TokenType tokenType;

    public Token(TokenType tokenType, String content){
        this.content = content;

        if(tokenType.equals(TokenType.IDENTIFIER)){
            if(Language.isLiteral(content)){
                this.tokenType = TokenType.LITERAL;
            }
            else if(Language.isKeyword(content)){
                this.tokenType = TokenType.KEYWORD;
            }
            else{
                this.tokenType = TokenType.IDENTIFIER;
            }
        }  else
            this.tokenType = tokenType;

        if(tokenType.equals(TokenType.PUNCTUATION)){
            switch (content) {
                case "\n":
                    this.content = "\\" + "n";
                    break;
                case "\t":
                    this.content = "\\" + "t";
                    break;
            }
        }
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getContent() {
        return content;
    }

}
