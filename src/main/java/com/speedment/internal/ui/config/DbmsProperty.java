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
package com.speedment.internal.ui.config;

import com.speedment.Speedment;
import com.speedment.config.Dbms;
import com.speedment.config.Project;
import com.speedment.config.Schema;
import com.speedment.config.aspects.Parent;
import com.speedment.config.parameters.DbmsType;
import com.speedment.exception.SpeedmentException;
import com.speedment.internal.core.config.dbms.StandardDbmsType;
import com.speedment.internal.core.config.utils.ConfigUtil;
import com.speedment.internal.ui.property.IntegerPropertyItem;
import com.speedment.internal.ui.property.StringPropertyItem;
import groovy.lang.Closure;
import static java.util.Collections.newSetFromMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.controlsfx.control.PropertySheet;
import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableSet;

/**
 *
 * @author Emil Forslund
 */
public final class DbmsProperty extends AbstractParentProperty<Dbms, Schema> implements Dbms, ChildHelper<Dbms, Project> {
    
    private final ObservableSet<Schema> schemaChildren;
    private final StringProperty ipAddress;
    private final IntegerProperty port;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty typeName;
    private final ObservableValue<DbmsType> dbmsType;
    
    private Project parent;
    
    public DbmsProperty(Speedment speedment) {
        super(speedment);
        schemaChildren = observableSet(newSetFromMap(new ConcurrentHashMap<>()));
        ipAddress      = new SimpleStringProperty();
        port           = new SimpleIntegerProperty();
        username       = new SimpleStringProperty();
        password       = new SimpleStringProperty();
        typeName       = new SimpleStringProperty();
        dbmsType       = bindDbmsType();
        setDefaults();
    }
    
    public DbmsProperty(Speedment speedment, Project parent, Dbms prototype) {
        super(speedment, prototype);
        this.schemaChildren = copyChildrenFrom(prototype, Schema.class, SchemaProperty::new);
        this.typeName       = new SimpleStringProperty(prototype.getTypeName());
        this.dbmsType       = bindDbmsType();
        this.ipAddress      = new SimpleStringProperty(prototype.getIpAddress().orElse("localhost"));
        this.port           = new SimpleIntegerProperty(prototype.getPort().orElse(getType().getDefaultPort()));
        this.username       = new SimpleStringProperty(prototype.getUsername().orElse("root"));
        this.password       = new SimpleStringProperty(prototype.getPassword().orElse(""));
        this.parent         = parent;
    }
    
    private void setDefaults() {
        setType(StandardDbmsType.defaultType());
        setIpAddress("localhost");
        setPort(getType().getDefaultPort());
        setUsername("root");
        setPassword("");
    }
    
    private ObservableValue<DbmsType> bindDbmsType() {
        return Bindings.createObjectBinding(() -> 
            getSpeedment().getDbmsHandlerComponent().findByName(typeName.getValue()).orElse(null),
            typeName
        );
    }
    
    @Override
    protected Stream<PropertySheet.Item> guiVisibleProperties() {
        return Stream.of(
            // TODO: Add DbmsType
            new StringPropertyItem(
                ipAddress,       
                "IP Address",                  
                "The ip of the database host."
            ),
            new IntegerPropertyItem(
                port,       
                "Port",                  
                "The port of the database on the database host."
            ),
            new StringPropertyItem(
                username,      
                "Username",                  
                "The username to use when connecting to the database."
            )
        );
    }
    
    public void clear() {
        schemaChildren.clear();
    }
    
    @Override
    public Optional<Project> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setParent(Parent<?> parent) {
        if (parent instanceof Project) {
            this.parent = (Project) parent;
        } else {
            throw wrongParentClass(parent.getClass());
        }
    }

    @Override
    public ObservableList<Schema> children() {
        return createChildrenView(schemaChildren);
    }
    
    @Override
    public Optional<String> getIpAddress() {
        return Optional.ofNullable(ipAddress.get());
    }

    @Override
    public void setIpAddress(String ipAddress) {
        this.ipAddress.setValue(ipAddress);
    }
    
    public StringProperty ipAddressProperty() {
        return ipAddress;
    }

    @Override
    public Optional<Integer> getPort() {
        return Optional.ofNullable(port.getValue());
    }

    @Override
    public void setPort(Integer port) {
        this.port.setValue(port);
    }
    
    public IntegerProperty portProperty() {
        return port;
    }

    @Override
    public Optional<String> getUsername() {
        return Optional.ofNullable(username.get());
    }

    @Override
    public void setUsername(String username) {
        this.username.setValue(username);
    }
    
    public StringProperty usernameProperty() {
        return username;
    }

    @Override
    public Optional<String> getPassword() {
        return Optional.ofNullable(password.get());
    }

    @Override
    public void setPassword(String password) {
        this.password.setValue(password);
    }
    
    public StringProperty passwordProperty() {
        return password;
    }

    @Override
    public DbmsType getType() {
        return dbmsType.getValue();
    }

    @Override
    public void setType(DbmsType dbmsType) {
        this.typeName.setValue(dbmsType.getName());
    }
    
    public ObservableValue<DbmsType> typeProperty() {
        return dbmsType;
    }

    @Override
    public String getTypeName() {
        return typeName.getValue();
    }

    @Override
    public void setTypeName(String name) {
        this.typeName.setValue(name);
    }
    
    public StringProperty typeNameProperty() {
        return typeName;
    }
    
    @Override
    public Schema addNewSchema() {
        final Schema schema = new SchemaProperty(getSpeedment());
        add(schema);
        return schema;
    }
    
    @Override
    public Schema schema(Closure<?> c) {
        return ConfigUtil.groovyDelegatorHelper(c, () -> addNewSchema());
    }
    
    @Override
    public Optional<Schema> add(Schema child) {
        requireNonNull(child);
        child.setParent(this);
        return schemaChildren.add(child) ? Optional.empty() : Optional.of(child);
    }
    
    @Override
    public Optional<Schema> remove(Schema child) {
        requireNonNull(child);
        if (schemaChildren.remove(child)) {
            child.setParent(null);
            return Optional.of(child);
        } else return Optional.empty();
    }

    @Override
    public Stream<Schema> stream() {
        return schemaChildren.stream().sorted(COMPARATOR);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Schema> Stream<T> streamOf(Class<T> childType) {
        requireNonNull(childType);
        
        if (Schema.class.isAssignableFrom(childType)) {
            return (Stream<T>) schemaChildren.stream().sorted(COMPARATOR);
        } else {
            throw wrongChildTypeException(childType);
        }
    }
    
    @Override
    public int count() {
        return schemaChildren.size();
    }

    @Override
    public int countOf(Class<? extends Schema> childType) {
        if (Schema.class.isAssignableFrom(childType)) {
            return schemaChildren.size();
        } else {
            throw wrongChildTypeException(childType);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Schema> T find(Class<T> childType, String name) throws SpeedmentException {
        requireNonNull(childType);
        requireNonNull(name);
        
        if (Schema.class.isAssignableFrom(childType)) {
            return (T) schemaChildren.stream().filter(child -> name.equals(child.getName()))
                .findAny().orElseThrow(() -> noChildWithNameException(childType, name));
        } else {
            throw wrongChildTypeException(childType);
        }
    }
}