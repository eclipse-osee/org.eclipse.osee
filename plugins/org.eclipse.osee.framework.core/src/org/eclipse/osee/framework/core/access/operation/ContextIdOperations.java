/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access.operation;

import java.util.Collection;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.access.IOseeAccessProvider;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ContextIdOperations {

   private final IAccessControlService accessControlService;

   public ContextIdOperations(IAccessControlService accessControlServiceImpl) {
      this.accessControlService = accessControlServiceImpl;
   }

   public void hasRelationContextWriteAccess(ArtifactToken subject, ArtifactToken artifact, RelationTypeToken relationType, Collection<? extends ArtifactToken> related, XResultData rd) {
      for (IOseeAccessProvider provider : accessControlService.getOseeAccessProviders()) {
         if (provider.isApplicable(subject, artifact.getBranch())) {
            provider.hasRelationContextWriteAccess(subject, artifact, relationType, rd);
         }
      }
   }

   public XResultData hasArtifactContextWriteAccess(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts, XResultData rd) {
      for (IOseeAccessProvider provider : accessControlService.getOseeAccessProviders()) {
         if (provider.isApplicable(subject, artifacts.iterator().next().getBranch())) {
            provider.hasArtifactContextWriteAccess(subject, artifacts, rd);
         }
      }
      return rd;
   }

   public XResultData hasAttributeTypeContextWriteAccess(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, XResultData rd) {
      for (IOseeAccessProvider provider : accessControlService.getOseeAccessProviders()) {
         if (provider.isApplicable(subject, artifacts.iterator().next())) {
            provider.hasAttributeTypeContextWriteAccess(subject, artifacts, attributeType, rd);
         }
      }
      return rd;
   }

}
