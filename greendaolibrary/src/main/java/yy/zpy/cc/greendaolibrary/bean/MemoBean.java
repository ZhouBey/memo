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
public class MemoBean {
    static final long serialVersionUID = 42L;
    @Id
    private Long id;
    private long folderID;
    private String title;
    private String content;
    private boolean isLock;
    private long createTime;
    private long updateTime;
    private long deleteTime;
    @Convert(converter = GreenDaoTypeConverter.class, columnType = String.class)
    private GreenDaoType planType;

    @Generated(hash = 2114047551)
    public MemoBean(Long id, long folderID, String title, String content,
            boolean isLock, long createTime, long updateTime, long deleteTime,
            GreenDaoType planType) {
        this.id = id;
        this.folderID = folderID;
        this.title = title;
        this.content = content;
        this.isLock = isLock;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
        this.planType = planType;
    }

    @Generated(hash = 336734028)
    public MemoBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getFolderID() {
        return folderID;
    }

    public void setFolderID(long folderID) {
        this.folderID = folderID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
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

    public GreenDaoType getPlanType() {
        return planType;
    }

    public void setPlanType(GreenDaoType planType) {
        this.planType = planType;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "id=" + id +
                ", folderID=" + folderID +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isLock=" + isLock +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleteTime=" + deleteTime +
                ", planType=" + planType +
                '}';
    }

    public boolean getIsLock() {
        return this.isLock;
    }

    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }
}
