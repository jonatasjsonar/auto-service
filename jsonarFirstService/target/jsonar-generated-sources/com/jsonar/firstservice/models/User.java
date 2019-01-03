package com.jsonar.firstservice.models;

import java.lang.Override;
import java.lang.String;

public class User {
  private String id;

  private int number;

  private String city;

  private Phone phone;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Phone getPhone() {
    return phone;
  }

  public void setPhone(Phone phone) {
    this.phone = phone;
  }

  @Override
  public String toString() {
    // Implementation not available
    return null;
  }
}
