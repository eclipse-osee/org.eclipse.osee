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

import java.sql.SQLException;

public interface IRelationEnumeration {
   boolean isSideA();

   String getSideName() throws SQLException;

   public String getTypeName();

   public RelationType getRelationType() throws SQLException;

   public boolean isThisType(RelationLink link);

   public RelationSide getSide();
}
