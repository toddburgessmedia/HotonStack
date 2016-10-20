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
import java.util.List;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 24/06/16.
 */
public class GitHubProjectCollection implements Serializable {

    @SerializedName("items")
    private List<GitHubProject> projects;

    public List<GitHubProject> getProjects() {
        return projects;
    }

    public void setProjects(List<GitHubProject> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (GitHubProject project : projects) {
            sb.append(project.toString() + "\n");
        }
        return sb.toString();
    }

    public boolean isEmpty() {
        if (projects.size() == 0)
            return true;
        else
            return false;
    }

    public void mergeProjects(GitHubProjectCollection collection) {

        List<GitHubProject> newprojects = collection.getProjects();

        for (int i = 0; i < newprojects.size(); i++) {
            projects.add(newprojects.get(i));
        }
    }

    public void insertPlaceHolders () {

        GitHubProject holder = new GitHubProject();
        holder.setPlaceholder(true);

        projects.add(0,holder);
        projects.add(projects.size(), holder);
    }

    public void insertLastPlaceHolder() {

        GitHubProject holder = new GitHubProject();
        holder.setPlaceholder(true);

        projects.add(projects.size(), holder);
    }
}
