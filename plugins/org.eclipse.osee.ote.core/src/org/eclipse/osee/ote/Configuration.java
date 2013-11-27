package org.eclipse.osee.ote;

import java.util.ArrayList;
import java.util.List;

/**
 * This defines the bundles to be loaded into the OTE Server.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class Configuration {

   private ArrayList<ConfigurationItem> items;

   public Configuration() {
      items = new ArrayList<ConfigurationItem>();
   }
   
   public List<ConfigurationItem> getItems() {
      return items;
   }

   public void addItem(ConfigurationItem config) {
      items.add(config);
   }

}
