/*
 * Created on Aug 20, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.Comparator;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class RelationOrderIdComparator implements Comparator<RelationOrderId> {

   @Override
   public int compare(RelationOrderId o1, RelationOrderId o2) {
      return o1.prettyName().compareToIgnoreCase(o2.prettyName());
   }

}
