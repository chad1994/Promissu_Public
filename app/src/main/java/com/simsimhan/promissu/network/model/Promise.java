package com.simsimhan.promissu.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Promise {
//    {
//        "id":2,
//        "title": "테스트 모임3",
//        "description": "안녕하세요",
//        "deposit":null,
//        "date":"2018-01-11T04:00:00.000Z",
//        "participants":5,
//        "location_lat":"37.499385",
//        "location_lon":"127.029204",
//        "admin_id":"996635036",
//        "status":0,
//        "createdAt": "2019-01-13T11:52:59.000Z",
//        "updatedAt":"2019-01-13T11:52:59.000Z"
//    }

    public static class Response implements Parcelable {
//        private final int id;
//        private final String title;
//        private final int participants;
//        private final int waiting_time;
//        private final String description;
//        private final String admin_id;
//        private final float location_lat;
//        private final float location_lon;
//        private final int status;
//        private final Date start_datetime;
//        private final Date end_datetime;
//        private final Date createdAt;
//        private final String location;
        private final int status;
        private final int id;
        private final String title;
        private final String  description;
        private final Date start_datetime;
        private final Date end_datetime;
        private final String location;
        private final String location_name;
        private final Double location_lat;
        private final Double location_lon;
        private final int admin_id;
        private final Date updatedAt;
        private final Date createdAt;

        public Response(int status, int id, String title, String description, Date start_datetime, Date end_datetime, String location, String location_name, Double location_lat, Double location_lon, int admin_id, Date updatedAt, Date createdAt) {
            this.status = status;
            this.id = id;
            this.title = title;
            this.description = description;
            this.start_datetime = start_datetime;
            this.end_datetime = end_datetime;
            this.location = location;
            this.location_name = location_name;
            this.location_lat = location_lat;
            this.location_lon = location_lon;
            this.admin_id = admin_id;
            this.updatedAt = updatedAt;
            this.createdAt = createdAt;
        }

        protected Response(Parcel in) {
            status = in.readInt();
            id = in.readInt();
            title = in.readString();
            description = in.readString();
            start_datetime = new Date(in.readLong());
            end_datetime = new Date(in.readLong());
            location = in.readString();
            location_name = in.readString();
            location_lat = in.readDouble();
            location_lon = in.readDouble();
            admin_id = in.readInt();
            updatedAt = new Date(in.readLong());
            createdAt = new Date(in.readLong());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(status);
            dest.writeInt(id);
            dest.writeString(title);
            dest.writeString(description);
            dest.writeLong(start_datetime.getTime());
            dest.writeLong(end_datetime.getTime());
            dest.writeString(location);
            dest.writeString(location_name);
            dest.writeDouble(location_lat);
            dest.writeDouble(location_lon);
            dest.writeInt(admin_id);
            dest.writeLong(updatedAt.getTime());
            dest.writeLong(createdAt.getTime());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Response> CREATOR = new Creator<Response>() {
            @Override
            public Response createFromParcel(Parcel in) {
                return new Response(in);
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };

        public int getStatus() {
            return status;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Date getStart_datetime() {
            return start_datetime;
        }

        public Date getEnd_datetime() {
            return end_datetime;
        }

        public String getLocation() {
            return location;
        }

        public String getLocation_name() {
            return location_name;
        }

        public Double getLocation_lat() {
            return location_lat;
        }

        public Double getLocation_lon() {
            return location_lon;
        }

        public int getAdmin_id() {
            return admin_id;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public Date getCreatedAt() {
            return createdAt;
        }
    }


    //        {
//                "title": "테스트 모임333",
//                "description": "우리 함께 모여서 놀아요",
//                "start_datetime": "2018-01-28 14:01",
//                "end_datetime": "2018-01-28 15:00",
//                "waiting_time": 1440,
//                "location_lat": "37.499385",
//                "location_lon": "127.029204"
//        }
    public static class Request {
        private String title;
        private String description;
        private Date start_datetime;
        private Date end_datetime;
        private String location;
        private String location_name;
        private Double location_lat;
        private Double location_lon;


        public Request(String title, String description, Date start_datetime, Date end_datetime, String location, String location_name, Double location_lat, Double location_lon) {
            this.title = title;
            this.description = description;
            this.start_datetime = start_datetime;
            this.end_datetime = end_datetime;
            this.location = location;
            this.location_name = location_name;
            this.location_lat = location_lat;
            this.location_lon = location_lon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getStart_datetime() {
            return start_datetime;
        }

        public void setStart_datetime(Date start_datetime) {
            this.start_datetime = start_datetime;
        }

        public Date getEnd_datetime() {
            return end_datetime;
        }

        public void setEnd_datetime(Date end_datetime) {
            this.end_datetime = end_datetime;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation_name() {
            return location_name;
        }

        public void setLocation_name(String location_name) {
            this.location_name = location_name;
        }

        public Double getLocation_lat() {
            return location_lat;
        }

        public void setLocation_lat(Double location_lat) {
            this.location_lat = location_lat;
        }

        public Double getLocation_lon() {
            return location_lon;
        }

        public void setLocation_lon(Double location_lon) {
            this.location_lon = location_lon;
        }
    }
}
