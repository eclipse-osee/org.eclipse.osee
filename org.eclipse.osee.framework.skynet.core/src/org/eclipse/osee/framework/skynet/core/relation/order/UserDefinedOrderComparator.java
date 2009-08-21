/*
 * Created on Aug 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Andrew M. Finkbeiner
 *
 */
class UserDefinedOrderComparator implements Comparator<Artifact>{
   
   private Map<String, Integer> value;
   
   UserDefinedOrderComparator(List<String> guidOrder){
      value = new HashMap<String,Integer>(guidOrder.size());
      for(int i = 0; i < guidOrder.size(); i++){
         value.put(guidOrder.get(i), i);
      }
   }
   
   @Override
   public int compare(Artifact artifact1, Artifact artifact2) {
      Integer val1 = value.get(artifact1.getGuid());
      Integer val2 = value.get(artifact2.getGuid());
      if(val1 == null){
         val1 = Integer.MAX_VALUE-1;
      }
      if(val2 == null){
         val2 = Integer.MAX_VALUE;
      }
      return val1 - val2;
   }
}
