/*
 * Copyright 2016 Todd Burgess Media
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

        public int getRank() {
                return rank;
        }

        public void setRank(int rank) {
                this.rank = rank;
        }

        private int rank = 1;

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
