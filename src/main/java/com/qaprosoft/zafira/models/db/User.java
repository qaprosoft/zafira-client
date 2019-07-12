/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.Group.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class User extends AbstractEntity implements Comparable<User> {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String photoURL;
    private List<Group> groups = new ArrayList<>();
    private List<UserPreference> preferences = new ArrayList<>();
    private Date lastLogin;
    private String tenant;
    private Source source;
    private Status status;
    private String resetToken;

    public enum Source {
        INTERNAL,
        LDAP
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public User(long id) {
        super.setId(id);
    }

    public User(String username) {
        this.username = username;
    }

    public void setRoles(List<Role> roles) {
        // Do nothing just treak for dozer mapper
    }

    public List<Role> getRoles() {
        Set<Role> roles = new HashSet<>();
        for (Group group : groups) {
            roles.add(group.getRole());
        }
        return new ArrayList<>(roles);
    }

    public Set<Permission> getPermissions() {
        return this.groups.stream()
                          .flatMap(group -> group.getPermissions().stream())
                          .collect(Collectors.toSet());
    }

    public List<Group> getGrantedGroups() {
        this.groups.forEach(group -> {
            group.setUsers(null);
            group.setId(null);
            group.setCreatedAt(null);
            group.setModifiedAt(null);
            group.getPermissions().forEach(permission -> permission.setId(null));
        });
        return this.groups;
    }

    @Override
    public int compareTo(User user) {
        return username.compareTo(user.getUsername());
    }
}
