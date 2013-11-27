package org.eclipse.osee.ote.rest.internal;

import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationItem;
import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTEConfigurationItem;

public class TranslateUtil {

   public static Configuration translateToOtherConfig(OTEConfiguration restConfig) {
      if(restConfig == null){
         return null;
      }
      Configuration config = new Configuration();
      for(OTEConfigurationItem item:restConfig.getItems()){
         config.addItem(TranslateUtil.translateToOtherConfig(item));
      }
      return config;
   }
   
   public static ConfigurationItem translateToOtherConfig(OTEConfigurationItem restConfigItem) {
      return new ConfigurationItem(restConfigItem.getLocationUrl(), restConfigItem.getBundleVersion(), restConfigItem.getBundleName(), restConfigItem.getMd5Digest(), restConfigItem.isOsgiBundle());
   }
   
   public static OTEConfiguration translateConfig(Configuration config){
      if(config == null){
         return null;
      }
      OTEConfiguration restConfig = new OTEConfiguration();
      for(ConfigurationItem item:config.getItems()){
         OTEConfigurationItem newitem = new OTEConfigurationItem();
         newitem.setBundleName(item.getSymbolicName());
         newitem.setBundleVersion(item.getVersion());
         newitem.setLocationUrl(item.getLocationUrl());
         newitem.setMd5Digest(item.getMd5Digest());
         restConfig.addItem(newitem);
      }
      return restConfig;
   }
   
}
