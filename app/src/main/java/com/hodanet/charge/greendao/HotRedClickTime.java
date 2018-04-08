package com.hodanet.charge.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 *
 */

@Entity
public class HotRedClickTime {
    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    @Unique
    private Long adId;
    private Long cilckTime;
    @Generated(hash = 68143373)
    public HotRedClickTime(Long _id, @NotNull Long adId, Long cilckTime) {
        this._id = _id;
        this.adId = adId;
        this.cilckTime = cilckTime;
    }
    @Generated(hash = 1025586044)
    public HotRedClickTime() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public Long getAdId() {
        return this.adId;
    }
    public void setAdId(Long adId) {
        this.adId = adId;
    }
    public Long getCilckTime() {
        return this.cilckTime;
    }
    public void setCilckTime(Long cilckTime) {
        this.cilckTime = cilckTime;
    }
    

}
