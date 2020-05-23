/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.skynet.core.event.filter;

import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author Donald G. Dunne
 */
public interface IEventFilter {

   /**
    * return true if events for this branch should be passed through to listeners
    */
   public boolean isMatch(BranchId branch);

   /**
    * return true if events for this this guid artifact should be passed through to listeners
    */
   public boolean isMatchArtifacts(List<? extends DefaultBasicGuidArtifact> guidArts);

   /**
    * return true if events for this guid relation should be passed through to listeners
    */
   public boolean isMatchRelationArtifacts(List<? extends IBasicGuidRelation> relations);

}
