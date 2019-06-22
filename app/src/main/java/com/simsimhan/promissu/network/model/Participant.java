package com.simsimhan.promissu.network.model;

import java.util.Comparator;
import java.util.Date;

public class Participant {
    public static class Response {
        private final int kakao_id;
        private final String nickname;
        private final int participation;
        private final Date updatedAt;
        private int status = 0;

        public Response(int kakao_id, String nickname, int participation, Date updatedAt, int status) {
            this.kakao_id = kakao_id;
            this.nickname = nickname;
            this.participation = participation;
            this.updatedAt = updatedAt;
            this.status = status;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }


    }

    public static class Request {
        private int partId;
        private String nickname;

        public Request(int partId, String nickname) {
            this.partId = partId;
            this.nickname = nickname;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getPartId() {
            return partId;
        }

        public void setPartId(int partId) {
            this.partId = partId;
        }
    }

    public static class CompareByStatus implements Comparator<Participant.Response> {

        @Override
        public int compare(Response o1, Response o2) {
            if (o1.status > o2.status)
                return 1;
            else if (o1.status < o2.status)
                return -1;
            else {
                if (o1.updatedAt.after(o2.updatedAt)) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }
}
