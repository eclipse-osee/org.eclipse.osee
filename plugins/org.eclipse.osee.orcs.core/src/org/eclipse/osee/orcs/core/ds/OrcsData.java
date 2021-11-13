/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.enums.DirtyState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.Id;

public interface OrcsData<T extends Id> extends HasVersion {

   void setLocalId(long localId);

   void setLocalId(Id id);

   Integer getLocalId();

   T getType();

   void setType(T type);

   T getBaseType();

   void setBaseType(T originalType);

   ModificationType getModType();

   void setModType(ModificationType modType);

   void setBaseModType(ModificationType modType);

   ModificationType getBaseModType();

   ModificationType getPreviousModType();

   boolean hasTypeUuidChange();

   boolean hasModTypeChange();

   public boolean isExistingVersionUsed();

   public void setUseBackingData(boolean useBackingData);

   public void setApplicabilityId(ApplicabilityId applicId);

   public ApplicabilityId getApplicabilityId();

   public DirtyState getDirtyState();

   public DirtyState calculateDirtyState(boolean dirty);

   public boolean isDirty();
}
