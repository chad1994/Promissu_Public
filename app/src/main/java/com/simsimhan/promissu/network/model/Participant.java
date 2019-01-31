package com.simsimhan.promissu.network.model;

public class Participant {
    public static class Response {
        private final int kakao_id;
        private final String nickname;

        public Response(int kakao_id, String nickname) {
            this.kakao_id = kakao_id;
            this.nickname = nickname;
        }

        public int getKakao_id() {
            return kakao_id;
        }

        public String getNickname() {
            return nickname;
        }
    }
}
