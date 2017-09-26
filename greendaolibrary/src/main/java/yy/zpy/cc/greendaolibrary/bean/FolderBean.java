package yy.zpy.cc.greendaolibrary.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import yy.zpy.cc.greendaolibrary.helper.GreenDaoTypeConverter;
import org.greenrobot.greendao.annotation.Generated;

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
    private GreenDaoType planType;

    @Generated(hash = 213574424)
    public FolderBean(Long id, String name, Long createTime, Long updateTime,
            Long deleteTime, Boolean isLock, GreenDaoType planType) {
        this.id = id;
        this.name = name;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
        this.isLock = isLock;
        this.planType = planType;
    }

    @Generated(hash = 1368532233)
    public FolderBean() {
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

    public Boolean getLock() {
        return isLock;
    }

    public void setLock(Boolean lock) {
        isLock = lock;
    }

    public GreenDaoType getPlanType() {
        return planType;
    }

    public void setPlanType(GreenDaoType planType) {
        this.planType = planType;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleteTime=" + deleteTime +
                ", isLock=" + isLock +
                ", planType=" + planType +
                '}';
    }

    public Boolean getIsLock() {
        return this.isLock;
    }

    public void setIsLock(Boolean isLock) {
        this.isLock = isLock;
    }
}
