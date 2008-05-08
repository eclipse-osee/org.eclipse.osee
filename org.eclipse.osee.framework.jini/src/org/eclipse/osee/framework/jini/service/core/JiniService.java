/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jini.service.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import net.jini.core.entry.Entry;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.lookup.entry.Comment;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jini.discovery.RelaxedSecurity;
import org.eclipse.osee.framework.jini.service.interfaces.IService;
import org.eclipse.osee.framework.plugin.core.server.ClassServer;
import org.eclipse.osee.framework.plugin.core.server.PathResourceFinder;

public abstract class JiniService implements IService {
   protected JiniJoinManager joinManager;
   protected Object keepAlive;
   protected ClassServer messageClassServer;
   protected String codeBase;
   protected String hostName;
   private final ServiceID serviceID;

   /**
    * Creates a Service with the serviceID specified. Note, this does not register the service.
    * 
    * @param serviceID the serviceID to assign to the service.
    */
   public JiniService(ServiceID serviceID) {
      this.serviceID = serviceID;
   }

   /**
    * Creates a Service with the serviceID specified. Note, this does not register the service.
    * 
    * @param uuid The unique identifier used for the serviceID.
    */
   public JiniService(Uuid uuid) {
      Long lsb = new Long(uuid.getLeastSignificantBits());
      Long msb = new Long(uuid.getMostSignificantBits());
      serviceID = new ServiceID(msb.longValue(), lsb.longValue());
   }

   /**
    * Creates a Service. The serviceID will be generated automatically. Note, this does not register the service.
    */
   public JiniService() {
      this(UuidFactory.generate());
   }

   public void stayAlive() {
      keepAlive = new Object();
      synchronized (keepAlive) {
         try {
            keepAlive.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   public void commitSuicide() {
      if (keepAlive != null) {
         synchronized (keepAlive) {
            keepAlive.notify();
         }
      }
   }

   public Entry[] registerService(Entry[] entry) {
      return registerService(entry, null);
   }

   @SuppressWarnings("unchecked")
   public Entry[] registerService(Entry[] entry, Dictionary dictionary) {
      try {
         System.setSecurityManager(new RelaxedSecurity());
         // find ServiceInfo
         List<Entry> entries = new ArrayList<Entry>();
         entries.add(new OwnerEntry());
         GroupEntry group = null;

         if (dictionary != null) {
            ServiceInfo info = null;
            Name name = null;
            Comment comment = null;
            VersionEntry version = null;

            if (entry != null) {
               for (int i = 0; i < entry.length; i++) {
                  if (entry[i] instanceof ServiceInfo) {
                     info = (ServiceInfo) entry[i];
                  }
                  if (entry[i] instanceof Name) {
                     name = (Name) entry[i];
                  }
                  if (entry[i] instanceof Comment) {
                     comment = (Comment) entry[i];
                  }
                  if (entry[i] instanceof VersionEntry) {
                     version = (VersionEntry) entry[i];
                  }
                  if (entry[i] instanceof GroupEntry) {
                     group = (GroupEntry) entry[i];
                  }
                  entries.add(entry[i]);
               }
            }
            if (info == null) {
               info = new ServiceInfo();
               entries.add(info);
            }
            if (name == null) {
               name = new Name();
               entries.add(name);
            }
            if (comment == null) {
               comment = new Comment();
               entries.add(comment);
            }
            if (version == null) {
               version = new VersionEntry();
               entries.add(version);
            }
            Object obj = null;
            obj = dictionary.get("Bundle-Name");
            if (obj != null) {
               info.name = obj.toString();
            }
            obj = dictionary.get("Bundle-Vendor");
            if (obj != null) {
               info.vendor = obj.toString();
            }
            obj = dictionary.get("Bundle-Description");
            if (obj != null) {
               comment.comment = obj.toString();
            }
            info.version = dictionary.get("Bundle-Version").toString();
            info.serialNumber = info.version;
            version.version = info.version;
            name.name = dictionary.get("Bundle-Name").toString();
         } else {
            if (entry != null) {
               for (int i = 0; i < entry.length; i++) {
                  entries.add(entry[i]);
               }
            }
         }

         if (group == null) {
            group = new GroupEntry();
            entries.add(group);
         }
         group.group = OseeProperties.getInstance().getOseeJiniServiceGroups();

         entry = new Entry[entries.size()];
         for (int i = 0; i < entries.size(); i++) {
            entry[i] = entries.get(i);
         }
         joinManager = new JiniJoinManager(serviceID, this, entry);
         joinManager.addGroup(group.group);
         Runtime.getRuntime().addShutdownHook(new KillService(joinManager));
      } catch (UnknownHostException ex) {
         ex.printStackTrace();
         System.exit(1);
      } catch (Exception ex) {
         ex.printStackTrace();
         System.exit(1);
      }
      return entry;
   }

   protected void startClassServer(String[] jarsToServe) throws Exception {
      messageClassServer = new ClassServer(0, InetAddress.getLocalHost());
      PathResourceFinder resource = new PathResourceFinder(jarsToServe, true);
      messageClassServer.addResourceFinder(resource);
      messageClassServer.start();
      codeBase =
            "http://" + InetAddress.getLocalHost().getCanonicalHostName() + ":" + messageClassServer.getPort() + "/";
      hostName = InetAddress.getLocalHost().getHostAddress();
      System.setProperty("java.rmi.server.hostname", hostName);
      System.setProperty("java.rmi.server.codebase", codeBase);
   }

   public void deregisterService() {
      try {
         if (joinManager != null) {
            joinManager.terminate();
            joinManager = null;

         } else {
            System.out.println("service already removed JiniService.deregister");
         }
      } catch (UnknownLeaseException ex) {
         ex.printStackTrace();
      } catch (RemoteException ex) {
         ex.printStackTrace();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void cleanup() {
      if (messageClassServer != null) messageClassServer.terminate();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jini.service.interfaces.IService#getServiceID()
    */
   public ServiceID getServiceID() throws RemoteException {
      return serviceID;
   }

   public boolean equals(Object object) {
      if (object instanceof JiniService) {
         return ((JiniService) object).serviceID.equals(this.serviceID);
      }
      return false;
   }

   public int hashCode() {
      return serviceID.hashCode();
   }

   public JiniJoinManager getJoinManager() {
      return joinManager;
   }

}
