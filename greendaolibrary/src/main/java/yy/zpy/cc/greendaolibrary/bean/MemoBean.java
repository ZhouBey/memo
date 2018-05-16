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
    private float fontSize;
    private int gravity;
    private float lineHeight;
    private String fontColor;
    private String backgroundColor;
    private String signFont;
    private boolean isLock;
    private long createTime;
    private long updateTime;
    private long deleteTime;
    @Convert(converter = GreenDaoTypeConverter.class, columnType = String.class)
    private GreenDaoType greenDaoType;

    
    public MemoBean() {
        this.createTime = System.currentTimeMillis();
    }



    @Generated(hash = 838952493)
    public MemoBean(Long id, long folderID, String title, String content, float fontSize,
            int gravity, float lineHeight, String fontColor, String backgroundColor,
            String signFont, boolean isLock, long createTime, long updateTime,
            long deleteTime, GreenDaoType greenDaoType) {
        this.id = id;
        this.folderID = folderID;
        this.title = title;
        this.content = content;
        this.fontSize = fontSize;
        this.gravity = gravity;
        this.lineHeight = lineHeight;
        this.fontColor = fontColor;
        this.backgroundColor = backgroundColor;
        this.signFont = signFont;
        this.isLock = isLock;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
        this.greenDaoType = greenDaoType;
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

    public float getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public int getGravity() {
        return this.gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public float getLineHeight() {
        return this.lineHeight;
    }

    public void setLineHight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public String getFontColor() {
        return this.fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getSignFont() {
        return this.signFont;
    }

    public void setSignFont(String signFont) {
        this.signFont = signFont;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }



    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }
}
