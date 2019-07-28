package com.simsimhan.promissu.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Promise {

    public static class Response implements Parcelable {
        private final int status;
        private final int id;
        private final int deposit;
        private final String title;
        private final String description;
        private final Date start_datetime;
        private final Date end_datetime;
        private final String location;
        private final String location_name;
        private final Double location_lat;
        private final Double location_lon;
        private final long admin_id;
        private final Date updatedAt;
        private final Date createdAt;

        public Response(int status, int id, int deposit, String title, String description, Date start_datetime, Date end_datetime, String location, String location_name, Double location_lat, Double location_lon, long admin_id, Date updatedAt, Date createdAt) {
            this.status = status;
            this.id = id;
            this.deposit = deposit;
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
            deposit = in.readInt();
            title = in.readString();
            description = in.readString();
            start_datetime = new Date(in.readLong());
            end_datetime = new Date(in.readLong());
            location = in.readString();
            location_name = in.readString();
            location_lat = in.readDouble();
            location_lon = in.readDouble();
            admin_id = in.readLong();
            updatedAt = new Date(in.readLong());
            createdAt = new Date(in.readLong());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(status);
            dest.writeInt(id);
            dest.writeInt(deposit);
            dest.writeString(title);
            dest.writeString(description);
            dest.writeLong(start_datetime.getTime());
            dest.writeLong(end_datetime.getTime());
            dest.writeString(location);
            dest.writeString(location_name);
            dest.writeDouble(location_lat);
            dest.writeDouble(location_lon);
            dest.writeLong(admin_id);
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

        public int getDeposit() {
            return deposit;
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

        public Long getAdmin_id() {
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
        private String datetime;
        private String location;
        private String location_name;
        private Double location_lat;
        private Double location_lon;
        private int late_range;


        public Request(String title, String description, String datetime, String location, String location_name, Double location_lat, Double location_lon, int late_range) {
            this.title = title;
            this.description = description;
            this.datetime = datetime;
            this.location = location;
            this.location_name = location_name;
            this.location_lat = location_lat;
            this.location_lon = location_lon;
            this.late_range = late_range;
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

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public int getLate_range() {
            return late_range;
        }

        public void setLate_range(int late_range) {
            this.late_range = late_range;
        }
    }
}
