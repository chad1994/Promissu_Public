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
//        "location_x":"37.499385",
//        "location_y":"127.029204",
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
        private final float location_x;
        private final float location_y;
        private final int status;
        private final Date start_datetime;
        private final Date end_datetime;
        private final Date createdAt;

        public Response(int id, String title, int participants, Date date, String description, float location_x, float location_y, int status, Date createAt, Date updatedAt, int waiting_time, Date createdAt) {
            this.id = id;
            this.title = title;
            this.participants = participants;
            this.description = description;
            this.location_x = location_x;
            this.location_y = location_y;
            this.status = status;
            this.start_datetime = createAt;
            this.end_datetime = updatedAt;
            this.waiting_time = waiting_time;
            this.createdAt = createdAt;
        }

        protected Response(Parcel in) {
            id = in.readInt();
            title = in.readString();
            participants = in.readInt();
            waiting_time = in.readInt();
            description = in.readString();
            location_x = in.readFloat();
            location_y = in.readFloat();
            status = in.readInt();
            end_datetime = new Date(in.readLong());
            start_datetime = new Date(in.readLong());
            createdAt = new Date(in.readLong());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(title);
            dest.writeInt(participants);
            dest.writeInt(waiting_time);
            dest.writeString(description);
            dest.writeFloat(location_x);
            dest.writeFloat(location_y);
            dest.writeInt(status);
            dest.writeLong(end_datetime.getTime());
            dest.writeLong(start_datetime.getTime());
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
    }


    //        {
//                "title": "테스트 모임333",
//                "description": "우리 함께 모여서 놀아요",
//                "start_datetime": "2018-01-28 14:01",
//                "end_datetime": "2018-01-28 15:00",
//                "waiting_time": 1440,
//                "location_x": "37.499385",
//                "location_y": "127.029204"
//        }
    public static class Request {
        private String title;
        private String description;
        private Date start_datetime;
        private Date end_datetime;
        private float location_x;
        private float location_y;
        private int waiting_time;

        public Request(String title, String description, Date start_datetime, Date end_datetime, float location_x, float location_y, int waiting_time) {
            this.title = title;
            this.description = description;
            this.start_datetime = start_datetime;
            this.end_datetime = end_datetime;
            this.location_x = location_x;
            this.location_y = location_y;
            this.waiting_time = waiting_time;
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

        public int getWaiting_time() {
            return waiting_time;
        }

        public void setWaiting_time(int waiting_time) {
            this.waiting_time = waiting_time;
        }
    }
}
