package com.simsimhan.promissu.ui.map.search;

public class Item {
    //	"address_name\": \"서울 강남구 역삼동 804\",\n"+
//			"            \"category_group_code\": \"SW8\",\n"+
//			"            \"category_group_name\": \"지하철역\",\n"+
//			"            \"category_name\": \"교통,수송 > 지하철,전철 > 수도권2호선\",\n"+
//			"            \"distance\": \"3568\",\n"+
//			"            \"id\": \"21160803\",\n"+
//			"            \"phone\": \"02-6110-2221\",\n"+
//			"            \"place_name\": \"강남역 2호선\",\n"+
//			"            \"place_url\": \"http://place.map.daum.net/21160803\",\n"+
//			"            \"road_address_name\": \"서울 강남구 강남대로 지하 396\",\n"+
//			"            \"x\": \"127.028000275071\",\n"+
//			"            \"y\": \"37.4980854357918\""
    private String address_name;
    private String category_group_code;
    private String category_group_name;
    private String category_name;
    private int id;
    private String phone;
    private String place_name;
    private String place_url;
    private String road_address_name;
    private double x;
    private double y;

    public Item(String address_name, String category_group_code, String category_group_name, String category_name, int id, String phone, String place_name, String place_url, String road_address_name, double x, double y) {
        this.address_name = address_name;
        this.category_group_code = category_group_code;
        this.category_group_name = category_group_name;
        this.category_name = category_name;
        this.id = id;
        this.phone = phone;
        this.place_name = place_name;
        this.place_url = place_url;
        this.road_address_name = road_address_name;
        this.x = x;
        this.y = y;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getCategory_group_code() {
        return category_group_code;
    }

    public void setCategory_group_code(String category_group_code) {
        this.category_group_code = category_group_code;
    }

    public String getCategory_group_name() {
        return category_group_name;
    }

    public void setCategory_group_name(String category_group_name) {
        this.category_group_name = category_group_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_url() {
        return place_url;
    }

    public void setPlace_url(String place_url) {
        this.place_url = place_url;
    }

    public String getRoad_address_name() {
        return road_address_name;
    }

    public void setRoad_address_name(String road_address_name) {
        this.road_address_name = road_address_name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Item{" +
                "address_name='" + address_name + '\'' +
                ", category_group_code='" + category_group_code + '\'' +
                ", category_group_name='" + category_group_name + '\'' +
                ", category_name='" + category_name + '\'' +
                ", id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", place_name='" + place_name + '\'' +
                ", place_url='" + place_url + '\'' +
                ", road_address_name='" + road_address_name + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
