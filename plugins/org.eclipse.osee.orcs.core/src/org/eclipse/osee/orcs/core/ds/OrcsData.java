/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.data.HasLocalId;

public interface OrcsData extends HasLocalId, HasVersion {

   void setLocalId(int localId);

   long getTypeUuid();

   void setTypeUuid(long typeUuid);

   long getBaseTypeUuid();

   void setBaseTypeUuid(long originalTypeUuid);

   ModificationType getModType();

   void setModType(ModificationType modType);

   void setBaseModType(ModificationType modType);

   ModificationType getBaseModType();

   ModificationType getPreviousModType();

   boolean hasTypeUuidChange();

   boolean hasModTypeChange();
}
