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

import org.eclipse.osee.framework.skynet.core.types.impl.DatabaseArtifactTypeAccessor;
import org.eclipse.osee.framework.skynet.core.types.impl.DatabaseAttributeTypeAccessor;
import org.eclipse.osee.framework.skynet.core.types.impl.DatabaseOseeEnumTypeAccessor;
import org.eclipse.osee.framework.skynet.core.types.impl.DatabaseRelationTypeAccessor;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeManager {

   private static final OseeTypeManager instance = new OseeTypeManager();

   private final OseeTypeCache oseeTypeCache;

   private OseeTypeManager() {
      oseeTypeCache =
            new OseeTypeCache(new OseeTypeFactory(), new DatabaseArtifactTypeAccessor(),
                  new DatabaseAttributeTypeAccessor(), new DatabaseRelationTypeAccessor(),
                  new DatabaseOseeEnumTypeAccessor());
   }

   public static OseeTypeCache getCache() {
      return instance.oseeTypeCache;
   }
}
