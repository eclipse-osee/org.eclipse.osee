package org.eclipse.osee.ote;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
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
   public void setup() {
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
      ConfigurationItem config1 =
         new ConfigurationItem(bundle1.toString(), "1.0.0", "loading.test1", ChecksumUtil.createChecksumAsString(
            bundle1.openStream(), "MD5"), true);
      ConfigurationItem config2 =
         new ConfigurationItem(bundle2.toString(), "1.0.0", "loading.test2", ChecksumUtil.createChecksumAsString(
            bundle2.openStream(), "MD5"), true);
      ConfigurationItem config3 =
         new ConfigurationItem(bundle3.toString(), "1.0.0", "loading.test3", ChecksumUtil.createChecksumAsString(
            bundle3.openStream(), "MD5"), true);

      Configuration validConfiguration = new Configuration();
      validConfiguration.addItem(config1);
      validConfiguration.addItem(config2);
      validConfiguration.addItem(config3);

      OTEStatusCallback<ConfigurationStatus> callable = new OTEStatusCallbackForTests<>();

      Future<ConfigurationStatus> statusFuture = oteApi.loadConfiguration(validConfiguration, callable);
      ConfigurationStatus status = statusFuture.get();
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

      Future<ConfigurationStatus> resetFeature = oteApi.resetConfiguration(callable);
      ConfigurationStatus resetStatus = resetFeature.get();
      Assert.assertTrue(resetStatus.isSuccess());
      Assert.assertNull(findBundle("loading.test1"));
      Assert.assertNull(findBundle("loading.test2"));
      Assert.assertNull(findBundle("loading.test3"));

      Configuration invalidConfiguration = new Configuration();
      invalidConfiguration.addItem(config1);
      invalidConfiguration.addItem(config3);
      Future<ConfigurationStatus> failStatusFuture = oteApi.loadConfiguration(invalidConfiguration, callable);
      ConfigurationStatus failStatus = failStatusFuture.get();
      Assert.assertFalse(failStatus.isSuccess());
      System.out.println(failStatus.getMessage());
      Assert.assertNull(findBundle("loading.test1"));
      Assert.assertNull(findBundle("loading.test2"));
      Assert.assertNull(findBundle("loading.test3"));

      //test the doing load case
      Future<ConfigurationStatus> good = oteApi.loadConfiguration(validConfiguration, callable);
      Future<ConfigurationStatus> bad = oteApi.loadConfiguration(validConfiguration, callable);

      ConfigurationStatus goodStatus = good.get();
      ConfigurationStatus badStatus = bad.get();
      Assert.assertTrue(goodStatus.isSuccess());
      Assert.assertFalse(badStatus.isSuccess());
      System.out.println(badStatus.getMessage());

      //test already configured
      bad = oteApi.loadConfiguration(validConfiguration, callable);
      badStatus = bad.get();
      Assert.assertFalse(badStatus.isSuccess());
      System.out.println(badStatus.getMessage());
   }

   @Test
   public void testJarAcquire() throws IOException, Exception {
      clearJarCache();
      URL bundle1 = findEntry("data/loading.test1.jar");
      URL bundle2 = findEntry("data/loading.test2.jar");
      URL bundle3 = findEntry("data/loading.test3.jar");
      ConfigurationItem config1 =
         new ConfigurationItem(bundle1.toString(), "1.0.0", "loading.test1", ChecksumUtil.createChecksumAsString(
            bundle1.openStream(), "MD5"), true);
      ConfigurationItem config2 =
         new ConfigurationItem(bundle2.toString(), "1.0.0", "loading.test2", ChecksumUtil.createChecksumAsString(
            bundle2.openStream(), "MD5"), true);
      ConfigurationItem config3 =
         new ConfigurationItem(bundle3.toString(), "1.0.0", "loading.test3", ChecksumUtil.createChecksumAsString(
            bundle3.openStream(), "MD5"), true);

      Configuration validConfiguration = new Configuration();
      validConfiguration.addItem(config1);
      validConfiguration.addItem(config2);
      validConfiguration.addItem(config3);

      OTEStatusCallback<ConfigurationStatus> callable = new OTEStatusCallbackForTests<>();

      Future<ConfigurationStatus> statusFuture = oteApi.downloadConfigurationJars(validConfiguration, callable);
      ConfigurationStatus status = statusFuture.get();
      Assert.assertTrue(status.isSuccess());
      OTEServerRuntimeCache cache = oteApi.getRuntimeCache();
      Assert.assertNotNull(cache.get(config1.getSymbolicName(), config1.getMd5Digest()));
      Assert.assertNotNull(cache.get(config2.getSymbolicName(), config2.getMd5Digest()));
      Assert.assertNotNull(cache.get(config3.getSymbolicName(), config3.getMd5Digest()));

      Assert.assertNull(cache.get(config1.getSymbolicName(), config2.getMd5Digest()));
      Assert.assertNull(cache.get(config2.getSymbolicName(), config3.getMd5Digest()));
      Assert.assertNull(cache.get(config3.getSymbolicName(), config1.getMd5Digest()));

      //test failure to download
      clearJarCache();
      ConfigurationItem config4 =
         new ConfigurationItem("file://this/file/does/not/exist/2351.jar", "1.0.0", "loading.test11",
            ChecksumUtil.createChecksumAsString(bundle1.openStream(), "MD5"), true);
      Configuration invalidConfiguration = new Configuration();
      invalidConfiguration.addItem(config4);
      statusFuture = oteApi.downloadConfigurationJars(invalidConfiguration, callable);
      status = statusFuture.get();
      Assert.assertFalse(status.isSuccess());

      //      Assert.assertEquals(validConfiguration, status.getConfiguration());
      //      Assert.assertEquals(validConfiguration, oteApi.getConfiguration().get().getConfiguration());

      //      Bundle bundle = findBundle("loading.test1");
      //      Assert.assertNotNull(bundle);
      //      Assert.assertEquals(Bundle.ACTIVE, bundle.getState());
      //      
      //      bundle = findBundle("loading.test2");
      //      Assert.assertNotNull(bundle);
      //      Assert.assertEquals(Bundle.ACTIVE, bundle.getState());
      //      
      //      bundle = findBundle("loading.test3");
      //      Assert.assertNotNull(bundle);
      //      Assert.assertEquals(Bundle.ACTIVE, bundle.getState());

      //      Future<ConfigurationStatus> resetFeature = oteApi.resetConfiguration(callable);
      //      ConfigurationStatus resetStatus = resetFeature.get();
      //      Assert.assertTrue(resetStatus.isSuccess());
      //      Assert.assertNull(findBundle("loading.test1"));
      //      Assert.assertNull(findBundle("loading.test2"));
      //      Assert.assertNull(findBundle("loading.test3"));

      //      Configuration invalidConfiguration = new Configuration();
      //      invalidConfiguration.addItem(config1);
      //      invalidConfiguration.addItem(config3);
      //      Future<ConfigurationStatus> failStatusFuture = oteApi.loadConfiguration(invalidConfiguration, callable);
      //      ConfigurationStatus failStatus = failStatusFuture.get();
      //      Assert.assertFalse(failStatus.isSuccess());
      //      System.out.println(failStatus.getMessage());
      //      Assert.assertNull(findBundle("loading.test1"));
      //      Assert.assertNull(findBundle("loading.test2"));
      //      Assert.assertNull(findBundle("loading.test3"));
      //      
      //      //test the doing load case
      //      Future<ConfigurationStatus> good = oteApi.loadConfiguration(validConfiguration, callable);
      //      Future<ConfigurationStatus> bad = oteApi.loadConfiguration(validConfiguration, callable);
      //      
      //      ConfigurationStatus goodStatus = good.get();
      //      ConfigurationStatus badStatus = bad.get();
      //      Assert.assertTrue(goodStatus.isSuccess());
      //      Assert.assertFalse(badStatus.isSuccess());
      //      System.out.println(badStatus.getMessage());
      //      
      //      //test already configured
      //      bad = oteApi.loadConfiguration(validConfiguration, callable);
      //      badStatus = bad.get();
      //      Assert.assertFalse(badStatus.isSuccess());
      //      System.out.println(badStatus.getMessage());
   }

   private void clearJarCache() {
      oteApi.getRuntimeCache().clearJarCache();
   }

   private Bundle findBundle(String symbolicName) {
      Bundle[] bundles = FrameworkUtil.getBundle(OTEApiTest.class).getBundleContext().getBundles();
      for (Bundle bundle : bundles) {
         if (bundle.getSymbolicName().equals(symbolicName)) {
            return bundle;
         }
      }
      return null;
   }

   private URL findEntry(String path) {
      URL url = null;
      Bundle bundle = FrameworkUtil.getBundle(OTEApiTest.class);
      url = bundle.getEntry(path);
      Assert.assertNotNull(url);
      return url;
   }

}
