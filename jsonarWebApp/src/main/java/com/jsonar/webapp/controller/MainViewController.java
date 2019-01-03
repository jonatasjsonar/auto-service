package com.jsonar.webapp.controller;

import com.jsonar.firstservice.models.User;
import com.jsonar.firstservice.services.RandomService;
import com.jsonar.secondservice.services.UUIDService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "mainView")
@ApplicationScoped
public class MainViewController {

    private RandomService randomService;
    private UUIDService uuidService;

    private Integer number;
    private String city;
    private String uuid;
    private User user;

    @PostConstruct
    public void init() {
        randomService = new RandomService();
        uuidService = new UUIDService();
    }

    public void changeNumber() {
        try {
            number = randomService.randomInt();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeCity() {
        city = randomService.randomCity();
        System.out.println(city);
    }

    public void changeUUID() {
        uuid = uuidService.randomUUID();
        System.out.println(uuid);
    }

    /*
        Getters & Setters
     */
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
