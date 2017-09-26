package yy.zpy.cc.greendaolibrary.helper;

import org.greenrobot.greendao.converter.PropertyConverter;

import yy.zpy.cc.greendaolibrary.bean.GreenDaoType;

/**
 * Created by zpy on 2017/9/26.
 */

public class GreenDaoTypeConverter implements PropertyConverter<GreenDaoType,String> {

    @Override
    public GreenDaoType convertToEntityProperty(String databaseValue) {
        return GreenDaoType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(GreenDaoType entityProperty) {
        return entityProperty.name();
    }
}
