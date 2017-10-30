package yy.zpy.cc.greendaolibrary.bean;

import android.text.format.DateFormat;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.io.Serializable;

import yy.zpy.cc.greendaolibrary.helper.GreenDaoTypeConverter;

/**
 * Created by zpy on 2017/9/26.
 */


@Entity(indexes = {
        @Index(value = "id,createTime ASC", unique = true)
})
public class MemoBean implements Serializable {
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
    private GreenDaoType greenDaoType;

    @Generated(hash = 131344341)
    public MemoBean(Long id, long folderID, String title, String content, boolean isLock,
            long createTime, long updateTime, long deleteTime, GreenDaoType greenDaoType) {
        this.id = id;
        this.folderID = folderID;
        this.title = title;
        this.content = content;
        this.isLock = isLock;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
        this.greenDaoType = greenDaoType;
    }

    public MemoBean() {
        this.createTime = System.currentTimeMillis();
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

    public GreenDaoType getGreenDaoType() {
        return greenDaoType;
    }

    public void setGreenDaoType(GreenDaoType greenDaoType) {
        this.greenDaoType = greenDaoType;
    }

    public boolean getIsLock() {
        return this.isLock;
    }

    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }

    @Override
    public String toString() {
        return "MemoBean{" +
                "id=" + id +
                ", folderID=" + folderID +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isLock=" + isLock +
                ", createTime=" + DateFormat.format("yyyy-MM-dd HH:mm:ss", createTime) +
                ", updateTime=" + DateFormat.format("yyyy-MM-dd HH:mm:ss", updateTime) +
                ", deleteTime=" + DateFormat.format("yyyy-MM-dd HH:mm:ss", deleteTime) +
                ", greenDaoType=" + greenDaoType +
                '}';
    }
}
