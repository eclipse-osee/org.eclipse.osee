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
package org.eclipse.osee.orcs.db.internal.types;

import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public final class OseeUtil {

   private OseeUtil() {
      // Utility Class
   }

   public static String getRelationOrderType(String guid) throws OseeCoreException {
      IRelationSorterId type = RelationOrderBaseTypes.getFromGuid(guid);
      return type.getName().replaceAll(" ", "_");
   }

   public static String orderTypeNameToGuid(String orderTypeName) throws OseeCoreException {
      Conditions.checkNotNull(orderTypeName, "orderTypeName");
      return RelationOrderBaseTypes.getFromOrderTypeName(orderTypeName.replaceAll("_", " ")).getGuid();
   }
}
