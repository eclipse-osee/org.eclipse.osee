package org.eclipse.osee.ote.rest.internal;

import java.util.Comparator;

import org.eclipse.osee.ote.rest.model.OTEConfigurationItem;

public class OTEConfigItemSort implements Comparator<OTEConfigurationItem> {

   @Override
   public int compare(OTEConfigurationItem arg0, OTEConfigurationItem arg1) {
      return arg0.getBundleName().compareTo(arg1.getBundleName());
   }

}
