package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tburgess on 04/06/16.
 */
public class Tag implements Serializable {

        @SerializedName("count")
        private String count;
        @SerializedName("name")
        private String name;

        private boolean placeholder = false;

        public boolean isPlaceholder() {
                return placeholder;
        }

        public void setPlaceholder(boolean placeholder) {
                this.placeholder = placeholder;
        }

        @Override
        public String toString() {
            return name + " " + count;
        }

        public String getCount() {
                return count;
        }

        public void setCount(String count) {
                this.count = count;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }
}
