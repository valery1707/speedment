/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.core;

import com.speedment.core.manager.Manager;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks this class as an Entity.
 * <p>
 * A Class with this annotation is marked as an entity.
 *
 * @author pemi
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {

    /**
     * Class that holds the manager type.
     *
     * @return the manager class type
     */
    Class<? extends Manager<?, ?, ?>> managerType();

    /**
     * Class that holds the builder type.
     *
     * @return the builder class type
     */
    Class<? extends Buildable<?>> builderType();

    /**
     * Class that holds the primary key type.
     *
     * @return the primary key class type
     */
    Class<?> primaryKeyType();
}
