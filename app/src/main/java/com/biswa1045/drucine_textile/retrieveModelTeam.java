package com.biswa1045.drucine_textile;

public class retrieveModelTeam {
    String img;
    String name;
    String desc;
    String time;
    String price;
    String del;
    public retrieveModelTeam() {
    }

    public retrieveModelTeam(String del,String img, String desc, String name, String time,String price) {
this.img = img;
this.desc = desc;
this.name = name;
this.time = time;
this.price = price;
        this.del = del;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
