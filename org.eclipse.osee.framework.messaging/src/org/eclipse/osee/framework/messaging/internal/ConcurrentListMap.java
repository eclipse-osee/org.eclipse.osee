/*
 * Created on May 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author b1528444
 *
 */
public class ConcurrentListMap<MAP_TYPE, LIST_TYPE> {
   
   private Map<MAP_TYPE, List<LIST_TYPE>> data;
   private List<LIST_TYPE> EMPTY_LIST = new ArrayList<LIST_TYPE>();
   
   public ConcurrentListMap(){
      data = new HashMap<MAP_TYPE, List<LIST_TYPE>>();
   }
   
   public synchronized boolean add(MAP_TYPE key, LIST_TYPE value){
      List<LIST_TYPE> values = data.get(key);
      if(values == null){
         values = new CopyOnWriteArrayList<LIST_TYPE>();
         data.put(key, values);
      }
      if(values.contains(value)){
         return false;
      } else {
         values.add(value);
         return true;
      }
   }
   
   public synchronized List<LIST_TYPE> get(MAP_TYPE key){
      List<LIST_TYPE> values = data.get(key);
      if(values == null){
         return EMPTY_LIST;
      } else {
         return values;
      }
   }
   
   public synchronized boolean remove(MAP_TYPE key, LIST_TYPE value){
      List<LIST_TYPE> values = data.get(key);
      if(values == null){
         return false;
      } else {
         return values.remove(value);
      }
   }

   public void clear() {
      data.clear();
   }
}
