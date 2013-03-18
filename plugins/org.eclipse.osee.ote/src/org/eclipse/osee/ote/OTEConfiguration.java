package org.eclipse.osee.ote;

import java.util.ArrayList;
import java.util.List;

/**
 * This defines the bundles to be loaded into the OTE Server.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class OTEConfiguration {

   private ArrayList<OTEConfigurationItem> items;

   public OTEConfiguration() {
      items = new ArrayList<OTEConfigurationItem>();
   }
   
   public List<OTEConfigurationItem> getItems() {
      return items;
   }

   public void addItem(OTEConfigurationItem config) {
      items.add(config);
   }

}
