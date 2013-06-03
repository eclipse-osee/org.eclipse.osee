package org.eclipse.osee.ote.master.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.eclipse.osee.ote.master.OTELookup;
import org.eclipse.osee.ote.master.OTELookupServerEntry;
import org.eclipse.osee.ote.master.OTEServerType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class OTELookupImplTest {

   OTEServerType basicType = new OTEServerType() {
      @Override
      public String getName() {
         return "test";
      }
   };
   
   private OTELookupServerEntry create(String name) throws URISyntaxException {
      UUID uuid = UUID.randomUUID();
      URI uri = new URI("http://localhost:8085/ote");
      OTELookupServerEntry entry = new OTELookupServerEntry(uuid, uri, name, basicType, new Date());
      return entry;
   }
   
   @Test
   public void testAdd() throws URISyntaxException {
      OTELookup lookup = new OTELookupImpl();
      
      OTELookupServerEntry entry = create("test1");
      lookup.addServer(entry);
      List<OTELookupServerEntry> servers = lookup.getAvailableServers();
      
      Assert.assertTrue(servers.contains(entry));
      Assert.assertEquals(1, servers.size());
   }
   
   @Test
   public void testUpdate() throws URISyntaxException {
      OTELookup lookup = new OTELookupImpl();
      
      OTELookupServerEntry entry = create("oldname");
      lookup.addServer(entry);
      
      Assert.assertEquals(1, lookup.getAvailableServers().size());

      lookup.addServer(entry);
      
      Assert.assertEquals(1, lookup.getAvailableServers().size());
      
   }
   
   @Test
   public void testRemove() throws URISyntaxException {
      OTELookup lookup = new OTELookupImpl();

      OTELookupServerEntry entry1 = create("name1");
      lookup.addServer(entry1);
      
      OTELookupServerEntry entry2 = create("name2");
      lookup.addServer(entry2);
      
      OTELookupServerEntry entry3 = create("name3");
      lookup.addServer(entry3);
      
      Assert.assertEquals(3, lookup.getAvailableServers().size());
      
      lookup.removeServer(entry1);
      
      Assert.assertEquals(2, lookup.getAvailableServers().size());
      
      lookup.removeServer(entry1);
      
      Assert.assertEquals(2, lookup.getAvailableServers().size());
      
      lookup.removeServer(entry2);
      
      Assert.assertEquals(1, lookup.getAvailableServers().size());
      
      lookup.removeServer(entry3);
      
      Assert.assertEquals(0, lookup.getAvailableServers().size());
   }
   
   @Test
   public void testTimeoutRemoval() throws URISyntaxException, InterruptedException {
      OTELookupImpl lookup = new OTELookupImpl();
      lookup.setTimeoutSeconds(10);
      lookup.start();
      
      OTELookupServerEntry entry1 = create("name1");
      lookup.addServer(entry1);
      
      Assert.assertEquals(1, lookup.getAvailableServers().size());
      
      Thread.sleep(1000*15);
      
      Assert.assertEquals(0, lookup.getAvailableServers().size());
      
   }
   
   @Ignore
   @Test
   public void testTimeoutRemovalLong() throws URISyntaxException, InterruptedException {
      OTELookupImpl lookup = new OTELookupImpl();
      lookup.setTimeoutSeconds(60*3);
      lookup.start();
      
      OTELookupServerEntry entry1 = create("name1");
      lookup.addServer(entry1);
      
      Assert.assertEquals(1, lookup.getAvailableServers().size());
      
      Thread.sleep(1000*60);
      
      Assert.assertEquals(1, lookup.getAvailableServers().size());
      
      Thread.sleep(1000*60);
      
      Assert.assertEquals(1, lookup.getAvailableServers().size());
      
      Thread.sleep(1000*40);
      
      Assert.assertEquals(1, lookup.getAvailableServers().size());
      
      Thread.sleep(1000*40);
      
      Assert.assertEquals(0, lookup.getAvailableServers().size());
      
   }
   
   @Ignore
   @Test
   public void testTimeoutRemovalLonger() throws URISyntaxException, InterruptedException {
      OTELookupImpl lookup = new OTELookupImpl();
      lookup.setTimeoutSeconds(30);
      lookup.start();
      
      OTELookupServerEntry entry1 = create("name1");
      lookup.addServer(entry1);
      
      Assert.assertEquals(1, lookup.getAvailableServers().size());
      
      Thread.sleep((1000*15));
      
      lookup.addServer(entry1);
      
      Thread.sleep((1000*15));
      
      lookup.addServer(entry1);
      
      Thread.sleep((1000*15));

      lookup.addServer(entry1);
      
      Thread.sleep((1000*15));
      
      lookup.addServer(entry1);
      
      Thread.sleep((1000*15));
      
      lookup.addServer(entry1);
      
      Thread.sleep((1000*45));
      
      Assert.assertEquals(0, lookup.getAvailableServers().size());
      
   }

}
