package com.biswa1045.drucine_textile;

public class retrieveModel {
    String design_img;
    String style;
    String price;
    String quantity;
    String size;
    String id;
    String del;

    public retrieveModel() {
    }

    public retrieveModel(String del,String design_img, String style, String price,String quantity,String size,String id) {
this.design_img = design_img;
this.style = style;
this.price = price;
this.quantity = quantity;
this.size = size;
        this.id = id;
        this.del = del;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesign_img() {
        return design_img;
    }

    public void setDesign_img(String design_img) {
        this.design_img = design_img;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
