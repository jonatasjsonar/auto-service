package com.jsonar.webapp.converter;

import com.jsonar.firstservice.models.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter
public class UserConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }

        String[] split = StringUtils.split(s, ";");
        if (split.length != 3) {
            return null;
        }

        User user = new User();
        user.setId(split[0]);
        user.setNumber(NumberUtils.toInt(split[1], 0));
        user.setCity(split[2]);

        return user;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if (o == null || !(o instanceof User)) {
            return "";
        }

        User user = (User) o;

        return (user.getId() + ";" + user.getNumber() + ";" + user.getCity());
    }
}
