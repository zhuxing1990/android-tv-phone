package com.vunke.videochat.model;

import java.util.List;

/**
 * Created by zhuxi on 2020/6/28.
 */

public class ContactsList {

    /**
     * code : 200
     * data : {"id":9,"password":"99998888","userId":"kjts007","userName":"73184787021","data":[{"fliId":"9","friendsName":"张三","friendsNumber":"","id":1},{"fliId":"9","friendsName":"李四","friendsNumber":"","id":2},{"fliId":"9","friendsName":"王五","friendsNumber":"","id":3}]}
     * message : success
     */

    private int code;
    /**
     * id : 9
     * password : 99998888
     * userId : kjts007
     * userName : 73184787021
     * data : [{"fliId":"9","friendsName":"张三","friendsNumber":"","id":1},{"fliId":"9","friendsName":"李四","friendsNumber":"","id":2},{"fliId":"9","friendsName":"王五","friendsNumber":"","id":3}]
     */

    private UserData obj;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserData getObj() {
        return obj;
    }

    public void setObj(UserData obj) {
        this.obj = obj;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class UserData {
        private int id;
        private String password;
        private String userId;
        private String userName;
        /**
         * fliId : 9
         * friendsName : 张三
         * friendsNumber :
         * id : 1
         */

        private List<ContactData> data;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public List<ContactData> getData() {
            return data;
        }

        public void setData(List<ContactData> data) {
            this.data = data;
        }

        public static class ContactData {
            private String fliId;
            private String friendsName;
            private String friendsNumber;
            private int id;

            public String getFliId() {
                return fliId;
            }

            public void setFliId(String fliId) {
                this.fliId = fliId;
            }

            public String getFriendsName() {
                return friendsName;
            }

            public void setFriendsName(String friendsName) {
                this.friendsName = friendsName;
            }

            public String getFriendsNumber() {
                return friendsNumber;
            }

            public void setFriendsNumber(String friendsNumber) {
                this.friendsNumber = friendsNumber;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
    }
}
