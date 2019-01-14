package com.simsimhan.promissu.network.model;

import java.util.Date;

public class Promise {
//    {
//        "id":2,
//        "title": "테스트 모임3",
//        "description": "안녕하세요",
//        "deposit":null,
//        "date":"2018-01-11T04:00:00.000Z",
//        "participants":5,
//        "location_x":"37.499385",
//        "location_y":"127.029204",
//        "admin_id":"996635036",
//        "status":0,
//        "createdAt": "2019-01-13T11:52:59.000Z",
//        "updatedAt":"2019-01-13T11:52:59.000Z"
//    }

    public static class Response {
        private final int id;
        private final String title;
        private final Date date;
        private final String name;
        private final float location_x;
        private final float location_y;
        private final int status;
        private final Date createAt;
        private final Date updatedAt;

        public Response(Date date, String name) {
            this.id = -1;
            this.title = "";
            this.date = date;
            this.name = name;
            this.location_x = 0f;
            this.location_y = 0f;
            this.status = 0;
            this.createAt = null;
            this.updatedAt = null;
        }

        public Response(int id, String title, Date date, String name, float location_x, float location_y, int status, Date createAt, Date updatedAt) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.name = name;
            this.location_x = location_x;
            this.location_y = location_y;
            this.status = status;
            this.createAt = createAt;
            this.updatedAt = updatedAt;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public float getLocation_x() {
            return location_x;
        }

        public float getLocation_y() {
            return location_y;
        }

        public int getStatus() {
            return status;
        }

        public Date getCreateAt() {
            return createAt;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public Date getDate() {
            return date;
        }

        public String getName() {
            return name;
        }
    }

    //    {
//            "title": "테스트 모임3",
//            "explain": "우리 함께 모여서 놀아요",
//            "date": "2018-01-11 13:00",
//            "location_x": "37.499385",
//            "location_y": "127.029204",
//            "admin_id": "tester"
//    }
    public static class Request {
        private String title;
        private Date date;
        private float location_x;
        private float location_y;
        private Date delayTime;

        public Request(String title, Date date, float location_x, float location_y, Date delayTime) {
            this.title = title;
            this.date = date;
            this.location_x = location_x;
            this.location_y = location_y;
            this.delayTime = delayTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public float getLocation_x() {
            return location_x;
        }

        public void setLocation_x(float location_x) {
            this.location_x = location_x;
        }

        public float getLocation_y() {
            return location_y;
        }

        public void setLocation_y(float location_y) {
            this.location_y = location_y;
        }

        public Date getDelayTime() {
            return delayTime;
        }

        public void setDelayTime(Date delayTime) {
            this.delayTime = delayTime;
        }
    }
}
