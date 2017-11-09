/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.version;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IAtsVersion extends ICommitConfigItem, IAtsConfigObject {

   /*****************************
    * Name, Full Name, Description
    ******************************/

   String getFullName();

   @Override
   String getDescription();

   @Override
   String toString();

   /*****************************
    * Branching Data
    ******************************/
   /**
    * @return directly configured baseline branch id or parentTeamDefinition's branch id
    */
   @Override
   BranchId getBaselineBranchId();

   BranchId getBaselineBranchIdInherited();

   @Override
   String getCommitFullDisplayName();

   @Override
   Result isAllowCreateBranchInherited();

   boolean isAllowCreateBranch();

   boolean isAllowCommitBranch();

   @Override
   Result isAllowCommitBranchInherited();

   /*****************************
    * Misc
    ******************************/
   Date getReleaseDate();

   Boolean isReleased();

   Date getEstimatedReleaseDate();

   Collection<String> getStaticIds();

   boolean isLocked();

   Boolean isVersionLocked();

   Boolean isNextVersion();

}
