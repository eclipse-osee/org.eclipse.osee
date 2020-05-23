/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.orcs.core.internal.access;

import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.framework.core.access.IArtifactCheck;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.model.access.IAccessControlService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AccessControlServiceImpl implements IAccessControlService {

   private final static Collection<IArtifactCheck> artifactChecks = new LinkedList<IArtifactCheck>();

   public void addArtifactCheck(IArtifactCheck artifactCheck) {
      artifactChecks.add(artifactCheck);
   }

   public AccessControlServiceImpl() {
      // for osgi instantiation
   }

   @Override
   public boolean hasPermission(Object object, PermissionEnum permission) {
      throw new UnsupportedOperationException("Not available on server yet");
   }

   @Override
   public void removePermissions(BranchId branch) {
      throw new UnsupportedOperationException("Not available on server yet");
   }

   @Override
   public AccessDataQuery getAccessData(ArtifactToken userArtifact, Collection<?> itemsToCheck) {
      throw new UnsupportedOperationException("Not available on server yet");
   }

   @Override
   public XResultData isDeleteable(Collection<ArtifactToken> artifacts, XResultData results) {
      for (IArtifactCheck check : artifactChecks) {
         check.isDeleteable(artifacts, results);
      }
      return results;
   }

   @Override
   public XResultData isRenamable(Collection<ArtifactToken> artifacts, XResultData results) {
      for (IArtifactCheck check : artifactChecks) {
         check.isRenamable(artifacts, results);
      }
      return results;
   }

   @Override
   public XResultData isDeleteableRelation(ArtifactToken artifact, RelationTypeToken relationType, XResultData results) {
      for (IArtifactCheck check : artifactChecks) {
         check.isDeleteableRelation(artifact, relationType, results);
      }
      return results;
   }

}
