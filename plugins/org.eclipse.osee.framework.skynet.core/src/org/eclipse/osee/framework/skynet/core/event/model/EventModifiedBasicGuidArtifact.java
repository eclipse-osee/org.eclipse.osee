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

package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public class EventModifiedBasicGuidArtifact extends EventBasicGuidArtifact {

   private final Collection<AttributeChange> attributeChanges;

   public EventModifiedBasicGuidArtifact(BranchId branch, ArtifactTypeToken artifactType, String guid, Collection<AttributeChange> attributeChanges) {
      super(EventModType.Modified, branch, artifactType, guid);
      this.attributeChanges = attributeChanges;
   }

   @Override
   public String toString() {
      return String.format("[%s - G:%s - B:%s - A:%s - %s]", EventModType.Modified.name(), getGuid(),
         getBranch().getIdString(), getArtifactType(), attributeChanges);
   }

   public Collection<AttributeChange> getAttributeChanges() {
      return attributeChanges;
   }
}
