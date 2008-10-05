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
package org.eclipse.osee.framework.skynet.core.relation;

import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;

public interface IRelationEnumeration {
   boolean isSideA();

   String getSideName() throws OseeTypeDoesNotExist, OseeDataStoreException;

   public String getTypeName();

   public RelationType getRelationType() throws OseeTypeDoesNotExist, OseeDataStoreException;

   public boolean isThisType(RelationLink link);

   public RelationSide getSide();
}
