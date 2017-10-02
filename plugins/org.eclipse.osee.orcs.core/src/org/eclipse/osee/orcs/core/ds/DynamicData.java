/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class DynamicData extends BaseIdentity<String> implements Named {

   private static final String FIELD_NAME = "field.name";
   private static final String IS_HIDDEN = "is.hidden";
   private static final String IS_PRIMARY = "is.primary";
   private static String LEVEL = "field.level";

   private final String alias;
   private DynamicObject parent;

   private Map<String, Object> properties;

   public DynamicData(String uid, String alias) {
      super(uid);
      this.alias = alias;
   }

   public String getAlias() {
      return alias;
   }

   public boolean isAliasValid() {
      return Strings.isValid(getAlias());
   }

   @Override
   public String getName() {
      return isAliasValid() ? getAlias() : getFieldName();
   }

   public boolean hasParent() {
      return parent != null;
   }

   public void setParent(DynamicObject parent) {
      Conditions.checkExpressionFailOnTrue(this == parent, "Cannot assign self as parent - parent [%s] child [%s]",
         this, parent);
      this.parent = parent;
   }

   public DynamicObject getParent() {
      return parent;
   }

   @SuppressWarnings("unchecked")
   public <T> T getObject(String key) {
      return properties != null ? (T) properties.get(key) : null;
   }

   public void put(String key, Object value) {
      if (properties == null) {
         properties = new LinkedHashMap<>();
      }
      properties.put(key, value);
   }

   public boolean isHidden() {
      return getObject(IS_HIDDEN);
   }

   public boolean isPrimaryKey() {
      return getObject(IS_PRIMARY);
   }

   public void setHidden(boolean hidden) {
      put(IS_HIDDEN, hidden);
   }

   public void setPrimaryKey(boolean primary) {
      put(IS_PRIMARY, primary);
   }

   public String getFieldName() {
      return getObject(FIELD_NAME);
   }

   public void setFieldName(String fieldName) {
      put(FIELD_NAME, fieldName);
   }

   public Integer getLevel() {
      return getObject(LEVEL);
   }

   public void setLevel(int level) {
      put(LEVEL, level);
   }

   @Override
   public String toString() {
      return "DynamicData [name=" + getName() + ", parent=" + parent + ", props=" + properties + "]";
   }

}
