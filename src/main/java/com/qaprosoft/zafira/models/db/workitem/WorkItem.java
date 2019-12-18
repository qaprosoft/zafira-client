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
package com.qaprosoft.zafira.models.db.workitem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class WorkItem extends BaseWorkItem {

    private Long testCaseId;
    private Type type;

    public WorkItem(String jiraId, Long testCaseId, Type type) {
        super(jiraId);
        this.testCaseId = testCaseId;
        this.type = type;
    }

    public WorkItem(String jiraId, String description, Long testCaseId, Type type) {
        super(jiraId, description);
        this.testCaseId = testCaseId;
        this.type = type;
    }

    public enum Type {
        TASK,
        BUG,
        COMMENT,
        EVENT
    }

}
