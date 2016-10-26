/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
