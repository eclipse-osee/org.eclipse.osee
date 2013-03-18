package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.net.URL;

import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.OTEConfiguration;
import org.eclipse.osee.ote.OTEConfigurationItem;
import org.eclipse.osee.ote.OTEConfigurationStatus;
import org.eclipse.osee.ote.OTEFuture;
import org.eclipse.osee.ote.OTEStatusCallback;
import org.eclipse.osgi.framework.internal.core.BundleFragment;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

@SuppressWarnings("restriction")
public class OTEApiTest {

   private OTEApi oteApi;
   
   @Before
   public void setup(){
      BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
      ServiceReference<OTEApi> ref = context.getServiceReference(OTEApi.class);
      Assert.assertNotNull(ref);
      oteApi = context.getService(ref);
      Assert.assertNotNull(oteApi);
   }
   
   @Test
   public void testConfigurationLoading() throws IOException, Exception {
      clearJarCache();
      URL bundle1 = findEntry("data/loading.test1.jar");
      URL bundle2 = findEntry("data/loading.test2.jar");
      URL bundle3 = findEntry("data/loading.test3.jar");
      OTEConfigurationItem config1 = new OTEConfigurationItem(bundle1.toString(), "1.0.0", "loading.test1", ChecksumUtil.createChecksumAsString(bundle1.openStream(), "MD5"));
      OTEConfigurationItem config2 = new OTEConfigurationItem(bundle2.toString(), "1.0.0", "loading.test2", ChecksumUtil.createChecksumAsString(bundle2.openStream(), "MD5"));
      OTEConfigurationItem config3 = new OTEConfigurationItem(bundle3.toString(), "1.0.0", "loading.test3", ChecksumUtil.createChecksumAsString(bundle3.openStream(), "MD5"));
      
      OTEConfiguration validConfiguration = new OTEConfiguration();
      validConfiguration.addItem(config1);
      validConfiguration.addItem(config2);
      validConfiguration.addItem(config3);
      
      OTEStatusCallback<OTEConfigurationStatus> callable = new OTEStatusCallbackForTests<OTEConfigurationStatus>();
      
      OTEFuture<OTEConfigurationStatus> statusFuture = oteApi.loadConfiguration(validConfiguration, callable);
      OTEConfigurationStatus status = statusFuture.get();
      Assert.assertTrue(status.isSuccess());
      Assert.assertEquals(validConfiguration, status.getConfiguration());
      Assert.assertEquals(validConfiguration, oteApi.getConfiguration().get().getConfiguration());
      
      
      Bundle bundle = findBundle("loading.test1");
      Assert.assertNotNull(bundle);
      Assert.assertEquals(Bundle.ACTIVE, bundle.getState());
      
      bundle = findBundle("loading.test2");
      Assert.assertNotNull(bundle);
      Assert.assertEquals(Bundle.ACTIVE, bundle.getState());
      
      bundle = findBundle("loading.test3");
      Assert.assertNotNull(bundle);
      Assert.assertEquals(Bundle.ACTIVE, bundle.getState());
      
      
      OTEFuture<OTEConfigurationStatus> resetFeature = oteApi.resetConfiguration(callable);
      OTEConfigurationStatus resetStatus = resetFeature.get();
      Assert.assertTrue(resetStatus.isSuccess());
      Assert.assertNull(findBundle("loading.test1"));
      Assert.assertNull(findBundle("loading.test2"));
      Assert.assertNull(findBundle("loading.test3"));
      
      OTEConfiguration invalidConfiguration = new OTEConfiguration();
      invalidConfiguration.addItem(config1);
      invalidConfiguration.addItem(config3);
      OTEFuture<OTEConfigurationStatus> failStatusFuture = oteApi.loadConfiguration(invalidConfiguration, callable);
      OTEConfigurationStatus failStatus = failStatusFuture.get();
      Assert.assertFalse(failStatus.isSuccess());
      System.out.println(failStatus.getMessage());
      Assert.assertNull(findBundle("loading.test1"));
      Assert.assertNull(findBundle("loading.test2"));
      Assert.assertNull(findBundle("loading.test3"));
      
      //test the doing load case
      OTEFuture<OTEConfigurationStatus> good = oteApi.loadConfiguration(validConfiguration, callable);
      OTEFuture<OTEConfigurationStatus> bad = oteApi.loadConfiguration(validConfiguration, callable);
      
      OTEConfigurationStatus goodStatus = good.get();
      OTEConfigurationStatus badStatus = bad.get();
      Assert.assertTrue(goodStatus.isSuccess());
      Assert.assertFalse(badStatus.isSuccess());
      System.out.println(badStatus.getMessage());
      
      //test already configured
      bad = oteApi.loadConfiguration(validConfiguration, callable);
      badStatus = bad.get();
      Assert.assertFalse(badStatus.isSuccess());
      System.out.println(badStatus.getMessage());
   }
   
   private void clearJarCache() {
      OTEApiComponent impl = (OTEApiComponent)oteApi;
      impl.clearJarCache();
   }

   private Bundle findBundle(String symbolicName){
      Bundle[] bundles = FrameworkUtil.getBundle(OTEApiTest.class).getBundleContext().getBundles();
      for(Bundle bundle:bundles){
         if(bundle.getSymbolicName().equals(symbolicName)){
            return bundle;
         }
      }
      return null;
   }
   
   private URL findEntry(String path){
      URL url = null;
      Bundle bundle = FrameworkUtil.getBundle(OTEApiTest.class);
      url = bundle.getEntry(path);
      if(url == null && bundle instanceof BundleHost){
         BundleFragment[] fragments = ((BundleHost)bundle).getFragments();
         for(BundleFragment fragment: fragments){
            url = fragment.getEntry(path);
            if(url != null){
               break;
            }
         }
      }
      Assert.assertNotNull(url);
      return url;
   }

}
