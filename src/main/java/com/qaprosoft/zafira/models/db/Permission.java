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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Permission extends AbstractEntity implements Comparable<Permission> {

    private Name name;
    private Block block;

    public Permission(Name name) {
        this.name = name;
    }

    public enum Block {
        DASHBOARDS,
        TEST_RUNS,
        TEST_RUN_VIEWS,
        INVITATIONS,
        USERS,
        PROJECTS,
        INTEGRATIONS,
        LAUNCHERS,
        BILLING
    }

    public enum Name {
        INVITE_USERS,
        MODIFY_INVITATIONS,
        VIEW_HIDDEN_DASHBOARDS,
        MODIFY_DASHBOARDS,
        MODIFY_WIDGETS,
        MODIFY_TEST_RUNS,
        TEST_RUNS_CI,
        MODIFY_TEST_RUN_VIEWS,
        VIEW_TEST_RUN_VIEWS,
        MODIFY_TESTS,
        MODIFY_USERS,
        VIEW_USERS,
        MODIFY_USER_GROUPS,
        MODIFY_PROJECTS,
        MODIFY_INTEGRATIONS,
        VIEW_INTEGRATIONS,
        MODIFY_LAUNCHERS,
        VIEW_LAUNCHERS,
        PAYMENT_METHODS,
        INVOICES
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Permission) {
            if (this.getId() != null) {
                equals = this.getId().equals(((Permission) o).getId());
            }
            if (this.getName() != null) {
                equals = this.getName().name().equals(((Permission) o).getName().name());
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return this.getId() != null ? this.getId().intValue() : this.getName().name().hashCode();
    }

    @Override
    public int compareTo(Permission o) {
        return o != null && this.getId() > o.getId() ? 1 : -1;
    }
}
