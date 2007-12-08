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

import org.eclipse.osee.framework.skynet.core.artifact.Branch;

public interface IRelationEnumeration {
   boolean isSideA();

   String getSideName(Branch branch);

   public String getTypeName();

   public IRelationLinkDescriptor getDescriptor(Branch branch);

   public boolean isThisType(IRelationLink link);
}
