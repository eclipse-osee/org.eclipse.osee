/*
 * Created on Aug 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface RelationOrder {

   RelationOrderId getOrderId();
   void applyOrder(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException;
   void sort(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException;
}
