package com.simsimhan.promissu.network.model;

import java.util.Date;

public class Promise {
    public static class Response {
        private final Date time;
        private final String name;

        public Response(Date time, String name) {
            this.time = time;
            this.name = name;
        }

        public Date getTime() {
            return time;
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
