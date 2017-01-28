package com.programmingbear.mynote;

/**
 * Created by satish on 15/11/2016.
 */
public class note {

    public String name;
    public String remark;
    public String dates;
    public int isStarred;

    public note(String name, String remark, String dates,int isStarred) {
        this.name = name;
        this.remark = remark;
        this.dates = dates;
        this.isStarred=isStarred;
    }
    public note(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDates() {
        return dates;
    }

    public int getisStarred() {
        return isStarred;
    }

    public void setIsStarred(int isStarred) {
        this.isStarred = isStarred;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }
}
