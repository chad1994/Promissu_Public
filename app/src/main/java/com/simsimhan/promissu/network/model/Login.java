package com.simsimhan.promissu.network.model;

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
        private String kakao_access_token;

        public Request(String kakao_access_token) {
            this.kakao_access_token = kakao_access_token;
        }

        public String getKakaoAccessToken() {
            return kakao_access_token;
        }

        public void setKakaoAccessToken(String kakaoAccessToken) {
            this.kakao_access_token = kakaoAccessToken;
        }
    }
}