package com.application.upapplication.Model;

import java.util.Date;

/**
 * Created by user on 12/23/2016.
 */

public class UserDetails {
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthday;
    private String phoneNumber;
    private String course;
    private int academicYear;
    private String aboutMe;
    private int age;
    private String interest1;
    private String interest2;
    private String interest3;
    private int isVerified;
    private int targetMale;
    private int targetFemale;
    private double longitude;
    private double latitude;
    private Date lastLogin;
    private String photo;

    public UserDetails(){

    }
    public UserDetails(String firstName, String lastName, String gender, String birthday, String phoneNumber, String course, int academicYear, String aboutMe, String interest1, String interest2, String interest3, int targetMale, int targetFemale) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.course = course;
        this.academicYear = academicYear;
        this.aboutMe = aboutMe;
        this.interest1 = interest1;
        this.interest2 = interest2;
        this.interest3 = interest3;
        this.targetMale = targetMale;
        this.targetFemale = targetFemale;
    }

    public UserDetails(String id, String firstName, String lastName, String gender, String birthday, String phoneNumber, String course, int academicYear, String aboutMe, int age, String interest1, String interest2, String interest3, int isVerified, int targetMale, int targetFemale, Date lastLogin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.course = course;
        this.academicYear = academicYear;
        this.aboutMe = aboutMe;
        this.age = age;
        this.interest1 = interest1;
        this.interest2 = interest2;
        this.interest3 = interest3;
        this.isVerified = isVerified;
        this.targetMale = targetMale;
        this.targetFemale = targetFemale;
        this.latitude = 0;
        this.longitude =0;
        this.lastLogin = lastLogin;
    }
    public UserDetails(String id, String firstName, String lastName, String gender, String birthday, String phoneNumber, String course, int academicYear, String aboutMe, int age, String interest1, String interest2, String interest3, int isVerified, int targetMale, int targetFemale, Date lastLogin,String photo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.course = course;
        this.academicYear = academicYear;
        this.aboutMe = aboutMe;
        this.age = age;
        this.interest1 = interest1;
        this.interest2 = interest2;
        this.interest3 = interest3;
        this.isVerified = isVerified;
        this.targetMale = targetMale;
        this.targetFemale = targetFemale;
        this.latitude = 0;
        this.longitude =0;
        this.lastLogin = lastLogin;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getInterest1() {
        return interest1;
    }

    public void setInterest1(String interest1) {
        this.interest1 = interest1;
    }

    public String getInterest2() {
        return interest2;
    }

    public void setInterest2(String interest2) {
        this.interest2 = interest2;
    }

    public String getInterest3() {
        return interest3;
    }

    public void setInterest3(String interest3) {
        this.interest3 = interest3;
    }

    public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }

    public int getTargetMale() {
        return targetMale;
    }

    public void setTargetMale(int targetMale) {
        this.targetMale = targetMale;
    }

    public int getTargetFemale() {
        return targetFemale;
    }

    public void setTargetFemale(int targetFemale) {
        this.targetFemale = targetFemale;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}

