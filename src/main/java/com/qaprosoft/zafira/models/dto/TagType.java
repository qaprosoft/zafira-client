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
package com.qaprosoft.zafira.models.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class TagType extends AbstractType {

    @NotEmpty(message = "Name required")
    @Size(max = 50)
    private String name;

    @NotEmpty(message = "Value required")
    @Size(max = 255)
    private String value;

    @Override
    public int hashCode() {
        return (this.name + this.value).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TagType && this.hashCode() == obj.hashCode();
    }
}
