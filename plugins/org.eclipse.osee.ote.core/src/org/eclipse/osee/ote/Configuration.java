package org.eclipse.osee.ote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This defines the bundles to be loaded into the OTE Server.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class Configuration implements Serializable {

   private static final long serialVersionUID = -3395485777990884086L;

   private ArrayList<ConfigurationItem> items;

   public Configuration() {
      items = new ArrayList<>();
   }
   
   public List<ConfigurationItem> getItems() {
      return items;
   }

   public void addItem(ConfigurationItem config) {
      items.add(config);
   }

}
