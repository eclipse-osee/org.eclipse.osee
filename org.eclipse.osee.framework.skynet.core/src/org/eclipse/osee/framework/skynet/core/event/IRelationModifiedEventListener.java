/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;

/**
 * @author Donald G. Dunne
 */
public interface IRelationModifiedEventListener extends IEventListner {
   public void handleRelationModifiedEvent(Sender sender, RelationModType relationModType, RelationLink link, Branch branch, String relationType);

}
