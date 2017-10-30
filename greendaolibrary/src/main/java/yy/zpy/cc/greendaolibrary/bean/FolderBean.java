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
    private long createTime;
    private long updateTime;
    private long deleteTime;
    private Boolean isLock;
    @Convert(converter = GreenDaoTypeConverter.class, columnType = String.class)
    private GreenDaoType greenDaoType;

    @Generated(hash = 1060495434)
    public FolderBean(Long id, String name, long createTime, long updateTime,
            long deleteTime, Boolean isLock, GreenDaoType greenDaoType) {
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(long deleteTime) {
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
