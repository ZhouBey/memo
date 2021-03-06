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
public class ImageBean {
    static final long serialVersionUID = 46L;
    @Id
    private Long id;
    private String imageID;
    private String path;
    private Long createTime;
    private Long updateTime;
    private Long deleteTime;
    @Convert(converter = GreenDaoTypeConverter.class, columnType = String.class)
    private GreenDaoType greenDaoType;
    
    public ImageBean() {
        this.createTime = System.currentTimeMillis();
    }

    @Generated(hash = 583304812)
    public ImageBean(Long id, String imageID, String path, Long createTime,
            Long updateTime, Long deleteTime, GreenDaoType greenDaoType) {
        this.id = id;
        this.imageID = imageID;
        this.path = path;
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

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public GreenDaoType getGreenDaoType() {
        return greenDaoType;
    }

    public void setGreenDaoType(GreenDaoType greenDaoType) {
        this.greenDaoType = greenDaoType;
    }
}
