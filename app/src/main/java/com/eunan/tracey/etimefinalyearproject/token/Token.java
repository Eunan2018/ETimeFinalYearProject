package com.eunan.tracey.etimefinalyearproject.token;

public class Token {

    private String tokenId;
    private String userId;

    public Token(String tokenId, String userId) {
        this.tokenId = tokenId;
        this.userId = userId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenId='" + tokenId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
