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
package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public class EventModifiedBasicGuidArtifact extends EventBasicGuidArtifact {

   private final Collection<AttributeChange> attributeChanges;

   public EventModifiedBasicGuidArtifact(BranchId branch, ArtifactTypeId artifactType, String guid, Collection<AttributeChange> attributeChanges) {
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
