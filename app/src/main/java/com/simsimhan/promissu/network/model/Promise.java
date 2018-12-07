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
}
