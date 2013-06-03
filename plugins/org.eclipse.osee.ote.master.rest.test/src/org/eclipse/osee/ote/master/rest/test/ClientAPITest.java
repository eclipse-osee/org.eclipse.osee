package org.eclipse.osee.ote.master.rest.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.osee.ote.master.rest.client.OTEMasterServer;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerAvailableNodes;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerResult;
import org.eclipse.osee.ote.master.rest.model.OTEServer;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class ClientAPITest {

   private static final String TEST_URI = "http://localhost:8008/";
   
   @Test
   public void testAdd() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
      OTEMasterServer oteMaster = getService(OTEMasterServer.class);
      Assert.assertNotNull(oteMaster);
      OTEServer server = createServer();
      
      Future<OTEMasterServerResult> addServer = oteMaster.addServer(new URI(TEST_URI), server);
      OTEMasterServerResult oteMasterServerResult = addServer.get(15, TimeUnit.SECONDS);
      Assert.assertNotNull(oteMasterServerResult);
      Assert.assertTrue(oteMasterServerResult.isSuccess());
      
      Future<OTEMasterServerResult> removeServer = oteMaster.removeServer(new URI(TEST_URI), server);
      OTEMasterServerResult oteMasterServerResult2 = removeServer.get(15, TimeUnit.SECONDS);
      Assert.assertNotNull(oteMasterServerResult2);
      Assert.assertTrue(oteMasterServerResult2.isSuccess());
      
      Thread.sleep(1000);
      
      Future<OTEMasterServerAvailableNodes> availableServers = oteMaster.getAvailableServers(new URI(TEST_URI));
      OTEMasterServerAvailableNodes nodes = availableServers.get(45, TimeUnit.SECONDS);
      Assert.assertNotNull(nodes);
      Assert.assertTrue(nodes.isSuccess());
      Assert.assertEquals(0, nodes.getServers().size());
   }

   private OTEServer createServer() {
      OTEServer server = new OTEServer();
      server.setUUID(UUID.randomUUID().toString());
      server.setName("test");
      server.setStartTime(new Date().toString());
      server.setType("oteserver");
      server.setUri("tcp://localhost:8998");
      return server;
   }
   
   @Test
   public void testGet() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
      OTEMasterServer oteMaster = getService(OTEMasterServer.class);
      Assert.assertNotNull(oteMaster);
      
      Future<OTEMasterServerAvailableNodes> availableServers = oteMaster.getAvailableServers(new URI(TEST_URI));
      OTEMasterServerAvailableNodes nodes = availableServers.get(45, TimeUnit.SECONDS);
      Assert.assertNotNull(nodes);
      Assert.assertTrue(nodes.isSuccess());
      Assert.assertEquals(0, nodes.getServers().size());
      
      OTEServer server = createServer();
      Future<OTEMasterServerResult> addServer = oteMaster.addServer(new URI(TEST_URI), server);
      OTEMasterServerResult oteMasterServerResult = addServer.get(15, TimeUnit.SECONDS);
      Assert.assertNotNull(oteMasterServerResult);
      Assert.assertTrue(oteMasterServerResult.isSuccess());
      
      Thread.sleep(1000);
      
      availableServers = oteMaster.getAvailableServers(new URI(TEST_URI));
      nodes = availableServers.get(45, TimeUnit.SECONDS);
      Assert.assertNotNull(nodes);
      Assert.assertTrue(nodes.isSuccess());
      Assert.assertEquals(1, nodes.getServers().size());
      
      Future<OTEMasterServerResult> removeServer = oteMaster.removeServer(new URI(TEST_URI), server);
      OTEMasterServerResult oteMasterServerResult2 = removeServer.get(10, TimeUnit.SECONDS);
      Assert.assertNotNull(oteMasterServerResult2);
      Assert.assertTrue(oteMasterServerResult2.isSuccess());
      
      Thread.sleep(1000);
      
      availableServers = oteMaster.getAvailableServers(new URI(TEST_URI));
      nodes = availableServers.get(45, TimeUnit.SECONDS);
      Assert.assertNotNull(nodes);
      Assert.assertTrue(nodes.isSuccess());
      Assert.assertEquals(0, nodes.getServers().size());
      
   }
   

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static <T> T getService(Class<T> clazz){
      BundleContext context = getContext();
      if(context == null){
         return null;
      }
      ServiceReference serviceReference = context.getServiceReference(clazz.getName());
      if(serviceReference == null){
         return null;
      }
      return (T)getContext().getService(serviceReference);
   }

   public static BundleContext getContext(){
      return FrameworkUtil.getBundle(ClientAPITest.class).getBundleContext();
   }

}
