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
        private final int id;
        private final String title;
        private final int participants;
        private final int waiting_time;
        private final String description;
        private final String admin_id;
        private final float location_lat;
        private final float location_lon;
        private final int status;
        private final Date start_datetime;
        private final Date end_datetime;
        private final Date createdAt;
        private final String location;

        public Response(int id, String title, int participants, Date date, String description, String admin_id, float location_lat, float location_lon, int status, Date createAt, Date updatedAt, int waiting_time, Date createdAt, String location) {
            this.id = id;
            this.title = title;
            this.participants = participants;
            this.description = description;
            this.admin_id = admin_id;
            this.location_lat = location_lat;
            this.location_lon = location_lon;
            this.status = status;
            this.start_datetime = createAt;
            this.end_datetime = updatedAt;
            this.waiting_time = waiting_time;
            this.createdAt = createdAt;
            this.location = location;
        }

        protected Response(Parcel in) {
            id = in.readInt();
            title = in.readString();
            participants = in.readInt();
            waiting_time = in.readInt();
            description = in.readString();
            admin_id = in.readString();
            location_lat = in.readFloat();
            location_lon = in.readFloat();
            status = in.readInt();
            end_datetime = new Date(in.readLong());
            start_datetime = new Date(in.readLong());
            createdAt = new Date(in.readLong());
            location = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(title);
            dest.writeInt(participants);
            dest.writeInt(waiting_time);
            dest.writeString(description);
            dest.writeString(admin_id);
            dest.writeFloat(location_lat);
            dest.writeFloat(location_lon);
            dest.writeInt(status);
            dest.writeLong(end_datetime.getTime());
            dest.writeLong(start_datetime.getTime());
            dest.writeLong(createdAt.getTime());
            dest.writeString(location);
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

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public float getLocation_lat() {
            return location_lat;
        }

        public float getLocation_lon() {
            return location_lon;
        }

        public int getStatus() {
            return status;
        }

        public Date getStartTime() {
            return start_datetime;
        }

        public Date getEndTime() {
            return end_datetime;
        }

        public int getWaitTime() {
            return waiting_time;
        }

        public String getDescription() {
            return description;
        }

        public int getParticipants() {
            return participants;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public String getAdmin_id() {
            return admin_id;
        }

        public String getLocation() {
            return location;
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
        private float location_lat;
        private float location_lon;
        private int waiting_time;
        private String location;

        public Request(String title, String description, Date start_datetime, Date end_datetime, float location_lat, float location_lon, int waiting_time, String location) {
            this.title = title;
            this.description = description;
            this.start_datetime = start_datetime;
            this.end_datetime = end_datetime;
            this.location_lat = location_lat;
            this.location_lon = location_lon;
            this.waiting_time = waiting_time;
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetail() {
            return description;
        }

        public void setDetail(String detail) {
            this.description = detail;
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

        public float getLocation_lat() {
            return location_lat;
        }

        public void setLocation_lat(float location_lat) {
            this.location_lat = location_lat;
        }

        public float getLocation_lon() {
            return location_lon;
        }

        public void setLocation_lon(float location_lon) {
            this.location_lon = location_lon;
        }

        public int getWaiting_time() {
            return waiting_time;
        }

        public void setWaiting_time(int waiting_time) {
            this.waiting_time = waiting_time;
        }
    }
}
