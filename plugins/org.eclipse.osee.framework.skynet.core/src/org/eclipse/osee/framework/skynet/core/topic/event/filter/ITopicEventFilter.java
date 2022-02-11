/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.skynet.core.topic.event.filter;

import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationTransfer;

/**
 * @author David W. Miller
 */
public interface ITopicEventFilter {

   /**
    * return true if events for this branch should be passed through to listeners
    */
   public boolean isMatch(BranchId branch);

   /**
    * return true if events for this this transfer artifact should be passed through to listeners
    */
   public boolean isMatchArtifacts(List<? extends EventTopicArtifactTransfer> transferArts);

   /**
    * return true if events for this transfer relation should be passed through to listeners
    */
   public boolean isMatchRelationArtifacts(List<? extends EventTopicRelationTransfer> transferRelations);

}
