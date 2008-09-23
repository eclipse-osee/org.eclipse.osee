/*
 * Created on Sep 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * @author Donald G. Dunne
 */
public class RelationModifiedEvent extends ArtifactTransactionModifiedEvent {

   protected final Sender sender;
   protected final RelationModType relationModType;
   protected final RelationLink link;
   protected final Branch branch;
   protected final String relationType;
   protected final UnloadedRelation unloadedRelation;

   public RelationModifiedEvent(Sender sender, RelationModType relationModType, RelationLink link, Branch branch, String relationType) {
      this.sender = sender;
      this.relationModType = relationModType;
      this.link = link;
      this.branch = branch;
      this.relationType = relationType;
      this.unloadedRelation = null;
   }

   public RelationModifiedEvent(Sender sender, RelationModType relationModType, UnloadedRelation unloadedRelation) {
      this.sender = sender;
      this.relationModType = relationModType;
      this.unloadedRelation = unloadedRelation;
      this.link = null;
      this.branch = null;
      this.relationType = null;
   }

   @Override
   public String toString() {
      return relationModType + " - " + (link != null ? "Loaded - " + link : "Unloaded - " + unloadedRelation) + " - " + sender;
   }
}
