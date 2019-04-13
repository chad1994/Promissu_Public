package com.simsimhan.promissu.network.model;

public class Participant {
    public static class Response {
        private final int kakao_id;
        private final String nickname;
        private final int participation;

        public Response(int kakao_id, String nickname, int participation) {
            this.kakao_id = kakao_id;
            this.nickname = nickname;
            this.participation = participation;
        }

        public int getKakao_id() {
            return kakao_id;
        }

        public String getNickname() {
            return nickname;
        }

        public int getParticipation() {
            return participation;
        }
    }
}
