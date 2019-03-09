package com.eunan.tracey.etimefinalyearproject.user;

import java.util.Objects;

public class UserModel {

    public String name;
    public String status;
    public String image;
    public String thumbImage;
    public String email;
    public String token;

    public UserModel(String name, String status, String image, String thumbImage,String email,String token) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.thumbImage = thumbImage;
        this.email = email;
        this.token = token;
    }

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                ", thumbImage='" + thumbImage + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(name, userModel.name) &&
                Objects.equals(status, userModel.status) &&
                Objects.equals(image, userModel.image) &&
                Objects.equals(thumbImage, userModel.thumbImage) &&
                Objects.equals(email, userModel.email) &&
                Objects.equals(token, userModel.token);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, status, image, thumbImage, email, token);
    }
}
