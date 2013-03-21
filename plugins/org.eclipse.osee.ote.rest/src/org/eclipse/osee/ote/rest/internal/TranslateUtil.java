package org.eclipse.osee.ote.rest.internal;

import org.eclipse.osee.ote.OTEConfiguration;
import org.eclipse.osee.ote.OTEConfigurationItem;
import org.eclipse.osee.ote.rest.model.OteConfiguration;
import org.eclipse.osee.ote.rest.model.OteConfigurationItem;

public class TranslateUtil {

   public static OTEConfiguration translateToOtherConfig(OteConfiguration restConfig) {
      OTEConfiguration config = new OTEConfiguration();
      for(OteConfigurationItem item:restConfig.getItems()){
         config.addItem(TranslateUtil.translateToOtherConfig(item));
      }
      return config;
   }
   
   public static OTEConfigurationItem translateToOtherConfig(OteConfigurationItem restConfigItem) {
      return new OTEConfigurationItem(restConfigItem.getLocationUrl(), restConfigItem.getBundleVersion(), restConfigItem.getBundleName(), restConfigItem.getMd5Digest());
   }
   
   public static OteConfiguration translateConfig(OTEConfiguration config){
      OteConfiguration restConfig = new OteConfiguration();
      for(OTEConfigurationItem item:config.getItems()){
         OteConfigurationItem newitem = new OteConfigurationItem();
         newitem.setBundleName(item.getSymbolicName());
         newitem.setBundleVersion(item.getVersion());
         newitem.setLocationUrl(item.getLocationUrl());
         newitem.setMd5Digest(item.getMd5Digest());
         restConfig.addItem(newitem);
      }
      return restConfig;
   }
   
}
