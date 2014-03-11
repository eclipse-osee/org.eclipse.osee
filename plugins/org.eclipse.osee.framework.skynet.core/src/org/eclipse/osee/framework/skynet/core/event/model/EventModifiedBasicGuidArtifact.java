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

/**
 * @author Donald G. Dunne
 */
public class EventModifiedBasicGuidArtifact extends EventBasicGuidArtifact {

   private final Collection<AttributeChange> attributeChanges;

   public EventModifiedBasicGuidArtifact(Long branchUuid, Long artTypeGuid, String guid, Collection<AttributeChange> attributeChanges) {
      super(EventModType.Modified, branchUuid, artTypeGuid, guid);
      this.attributeChanges = attributeChanges;
   }

   @Override
   public String toString() {
      return String.format("[%s - G:%s - B:%s - A:%s - %s]", EventModType.Modified.name(), getGuid(), getBranchUuid(),
         getArtTypeGuid(), attributeChanges);
   }

   public Collection<AttributeChange> getAttributeChanges() {
      return attributeChanges;
   }
}
