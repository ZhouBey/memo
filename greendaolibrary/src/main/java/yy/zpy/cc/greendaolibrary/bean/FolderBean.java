package yy.zpy.cc.greendaolibrary.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import yy.zpy.cc.greendaolibrary.helper.GreenDaoTypeConverter;

/**
 * Created by zpy on 2017/9/26.
 */

@Entity(indexes = {
        @Index(value = "id,createTime ASC", unique = true)
})
public class FolderBean {
    static final long serialVersionUID = 41L;
    @Id
    private Long id;
    private String name;
    private Long createTime;
    private Long updateTime;
    private Long deleteTime;
    private Boolean isLock;
    @Convert(converter = GreenDaoTypeConverter.class, columnType = String.class)
    private GreenDaoType greenDaoType;

    @Generated(hash = 1970749701)
    public FolderBean(Long id, String name, Long createTime, Long updateTime,
            Long deleteTime, Boolean isLock, GreenDaoType greenDaoType) {
        this.id = id;
        this.name = name;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
        this.isLock = isLock;
        this.greenDaoType = greenDaoType;
    }

    public FolderBean() {
        this.createTime = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Long deleteTime) {
        this.deleteTime = deleteTime;
    }

    public Boolean getIsLock() {
        return this.isLock;
    }

    public void setIsLock(Boolean isLock) {
        this.isLock = isLock;
    }

    public GreenDaoType getGreenDaoType() {
        return greenDaoType;
    }

    public void setGreenDaoType(GreenDaoType greenDaoType) {
        this.greenDaoType = greenDaoType;
    }

    @Override
    public String toString() {
        return "FolderBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleteTime=" + deleteTime +
                ", isLock=" + isLock +
                ", greenDaoType=" + greenDaoType +
                '}';
    }
}
