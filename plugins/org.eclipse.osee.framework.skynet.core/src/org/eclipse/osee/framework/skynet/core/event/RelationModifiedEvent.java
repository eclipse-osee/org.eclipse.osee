/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * @author Donald G. Dunne
 */
public class RelationModifiedEvent extends ArtifactTransactionModifiedEvent {

   protected final Sender sender;
   protected final RelationEventType relationEventType;
   protected final RelationLink link;
   protected final Branch branch;
   protected final String relationType;
   protected final UnloadedRelation unloadedRelation;

   public RelationModifiedEvent(Sender sender, RelationEventType relationEventType, RelationLink link, Branch branch, String relationType) {
      this.sender = sender;
      this.relationEventType = relationEventType;
      this.link = link;
      this.branch = branch;
      this.relationType = relationType;
      this.unloadedRelation = null;
   }

   public RelationModifiedEvent(Sender sender, RelationEventType relationEventType, UnloadedRelation unloadedRelation) {
      this.sender = sender;
      this.relationEventType = relationEventType;
      this.unloadedRelation = unloadedRelation;
      this.link = null;
      this.branch = null;
      this.relationType = null;
   }

   @Override
   public String toString() {
      return relationEventType + " - " + (link != null ? "Loaded - " + link : "Unloaded - " + unloadedRelation) + " - " + sender;
   }
}
