package com.simsimhan.promissu.network;

public class Login {
    public static class Response {
        private final String token;

        public Response(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }

    public static class Request {
        private String email;
        private String password;

        public Request(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
