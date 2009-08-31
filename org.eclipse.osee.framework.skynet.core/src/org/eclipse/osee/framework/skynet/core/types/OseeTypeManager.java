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
package org.eclipse.osee.framework.skynet.core.types;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeManager {

   private static final OseeTypeManager instance = new OseeTypeManager();

   private final IOseeTypeDataAccessor dataAccessor;
   private final OseeTypeCache oseeTypeCache;
   private final IOseeTypeFactory typeFactory;

   private OseeTypeManager() {
      typeFactory = new OseeTypeFactory();
      dataAccessor = new OseeTypeDatabaseAccessor();
      oseeTypeCache = new OseeTypeCache(dataAccessor, typeFactory);
   }

   public static OseeTypeCache getCache() {
      return instance.oseeTypeCache;
   }

   public static IOseeTypeFactory getTypeFactory() {
      return instance.typeFactory;
   }

   public static IOseeTypeDataAccessor getDataTypeAccessor() {
      return instance.dataAccessor;
   }
}
