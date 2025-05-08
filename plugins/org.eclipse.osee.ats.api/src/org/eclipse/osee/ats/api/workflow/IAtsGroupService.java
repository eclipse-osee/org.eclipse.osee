/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsGroupService {

   Collection<ArtifactToken> getGroups(BranchToken branch);

   ArtifactToken getGroupOrNull(ArtifactToken groupToken, BranchToken branch);

   Collection<ArtifactToken> getGroupsNotRoot(BranchToken branch);

   Collection<ArtifactToken> getGroups(String groupName, BranchToken branch);

   ArtifactToken addGroup(ArtifactToken groupToken, BranchToken branch, IAtsChangeSet changes);

   ArtifactToken getTopUniversalGroupArtifact(BranchId branch);

}
