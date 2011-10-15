/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.attribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.AttributeClassProvider;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * @author Roberto E. Escobar
 */
public class AttributeClassResolver {

   private final Map<String, Class<? extends Attribute<?>>> map =
      new ConcurrentHashMap<String, Class<? extends Attribute<?>>>();

   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   public void start() {
      // Nothing yet;
   }

   public void stop() {
      // Nothing yet;
   }

   public void addProvider(AttributeClassProvider provider) {
      for (Class<? extends Attribute<?>> clazz : provider.getClasses()) {
         String alias = toAlias(clazz);
         map.put(alias, clazz);
      }
   }

   public void removeProvider(AttributeClassProvider provider) {
      for (Class<? extends Attribute<?>> clazz : provider.getClasses()) {
         String alias = toAlias(clazz);
         map.remove(alias);
      }
   }

   private String toAlias(Class<? extends Attribute<?>> clazz) {
      OseeAttribute annotation = clazz.getAnnotation(OseeAttribute.class);
      String toReturn;
      if (annotation != null) {
         toReturn = annotation.value();
      } else {
         toReturn = clazz.getSimpleName();
         logger.warn("Unable to find OseeAttribute annotation for [%s] - registering using clazz simple name [%s]",
            clazz, toReturn);
      }
      return toReturn;
   }

   public Class<? extends Attribute<?>> getBaseClazz(String alias) {
      return map.get(alias);
   }

   public Class<? extends Attribute<?>> getBaseClazz(AttributeType attributeType) {
      String alias = attributeType.getBaseAttributeTypeId();
      if (alias.contains(".")) {
         alias = Lib.getExtension(alias);
      }
      return getBaseClazz(alias);
   }

   public boolean isBaseTypeCompatible(Class<? extends Attribute<?>> baseType, AttributeType attributeType) throws OseeCoreException {
      Conditions.checkNotNull(baseType, "baseType", "Unable to determine base type from null");
      Conditions.checkNotNull(attributeType, "attributeType");
      Class<? extends Attribute<?>> clazz = getBaseClazz(attributeType);
      Conditions.checkNotNull(clazz, "base attribute type class", "Unable to find base attribute type class for [%s]",
         attributeType);
      return baseType.isAssignableFrom(clazz);
   }
}
