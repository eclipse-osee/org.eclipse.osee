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
package org.eclipse.osee.cluster.hazelcast.internal;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.cluster.Cluster;
import org.eclipse.osee.cluster.ClusterService;
import org.eclipse.osee.cluster.ClusterServiceUtils;
import org.eclipse.osee.cluster.DistributedExecutorService;
import org.eclipse.osee.cluster.Transaction;
import org.eclipse.osee.cluster.TransactionWork;
import org.eclipse.osee.distributed.AtomicNumber;
import org.eclipse.osee.distributed.DistributedId;
import org.eclipse.osee.distributed.DistributedLock;
import org.eclipse.osee.distributed.DistributedMap;
import org.eclipse.osee.distributed.DistributedMultiMap;
import org.eclipse.osee.distributed.DistributedObject;
import org.eclipse.osee.distributed.InstanceManager;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.logger.Log;
import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.Instance;
import com.hazelcast.core.InstanceEvent;
import com.hazelcast.core.InstanceListener;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.core.MultiMap;
import com.hazelcast.impl.GroupProperties;

/**
 * @author Roberto E. Escobar
 */
public class ClusterServiceImpl implements ClusterService, InstanceManager {

   private final Map<Object, DistributedObject> distributedObjects = new ConcurrentHashMap<>();
   private final ProxyCleaner proxyCleaner = new ProxyCleaner();
   private Log logger;
   private EventService eventService;
   private HazelcastInstance instance;
   private Thread thread;
   private ClusterEventNotifier eventNotifier;
   private ClusterProxy clusterProxy;
   private DistributedExecutorService executor;

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   private EventService getEventService() {
      return eventService;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   private Log getLogger() {
      return logger;
   }

   private Config getConfiguration(Map<String, Object> properties) {
      Config config = null;

      String loggerClass = System.getProperty("hazelcast.logging.class");
      if (loggerClass == null) {
         String loggerType = System.getProperty("hazelcast.logging.type");
         if (loggerType == null) {
            System.setProperty("hazelcast.logging.type", "slf4j");
         }
      }
      String configPath = ClusterServiceUtils.getConfigurationURL(properties);
      if (configPath != null && configPath.length() > 0) {
         config = loadConfiguration(configPath);
      } else {
         config = new XmlConfigBuilder().build();
      }
      Properties props = config.getProperties();
      props.put(GroupProperties.PROP_VERSION_CHECK_ENABLED, "false");
      //      props.put(GroupProperties.PROP_REST_ENABLED, "true");
      props.put(GroupProperties.PROP_ENABLE_JMX, "false");
      props.put(GroupProperties.PROP_ENABLE_JMX_DETAILED, "false");
      return config;
   }

   private Config loadConfiguration(String configPath) {
      // InputStream stream = null;
      try {
         //         URL configURL = new URL(configPath);
         //         stream = new BufferedInputStream(configURL.openStream());
         //         
         //         XmlConfigBuilder builder = new XmlConfigBuilder(stream);
         return new UrlXmlConfig(configPath);
      } catch (IOException ex) {
         throw new RuntimeException(ex);
         //      } finally {
         //         if (stream != null) {
         //            try {
         //               stream.close();
         //            } catch (IOException ex) {
         //               // Do Nothing 
         //            }
         //         }
      }
   }

   public synchronized void start(final Map<String, Object> properties) {
      thread = new Thread("Register Pending Rest Services") {
         @Override
         public void run() {
            Config config = getConfiguration(properties);
            instance = Hazelcast.init(config);
            clusterProxy = new ClusterProxy(instance);
            executor = new DistributedExecutorServiceImpl(instance);

            String componentName = ClusterServiceUtils.getComponentName(properties);
            String contextName = ClusterServiceUtils.getContextName(properties);
            eventNotifier = new ClusterEventNotifier(componentName, contextName, getEventService());
            registerEventListeners();
            eventNotifier.notifyRegistration();
         }
      };
      thread.start();
   }

   public synchronized void stop(Map<String, Object> properties) {
      if (thread != null && thread.isAlive()) {
         thread.interrupt();
         thread = null;
      }
      distributedObjects.clear();
      if (instance != null) {
         deregisterEventListeners();
         LifecycleService service = instance.getLifecycleService();
         service.shutdown();
         instance = null;
      }
      eventNotifier.notifyDeRegistration();
      eventNotifier = null;
   }

   private void registerEventListeners() {
      instance.addInstanceListener(proxyCleaner);
      instance.addInstanceListener(eventNotifier);
      instance.getLifecycleService().addLifecycleListener(eventNotifier);
      instance.getCluster().addMembershipListener(eventNotifier);
   }

   private void deregisterEventListeners() {
      instance.removeInstanceListener(proxyCleaner);
      instance.removeInstanceListener(eventNotifier);
      instance.getLifecycleService().removeLifecycleListener(eventNotifier);
      instance.getCluster().removeMembershipListener(eventNotifier);
   }

   protected HazelcastInstance getHazelcastInstance() {
      return instance;
   }

   @Override
   public DistributedExecutorService getExecutor() {
      return executor;
   }

   @Override
   public Cluster getCluster() {
      return clusterProxy;
   }

   @Override
   public String getName() {
      return getHazelcastInstance().getName();
   }

   @Override
   public Transaction getTransaction() {
      // Create a new one every time
      return new TransactionProxy(getHazelcastInstance().getTransaction());
   }

   @Override
   public <T> Callable<T> createTxCallable(TransactionWork<T> work) {
      Transaction txn = getTransaction();
      return new CallableTransactionImpl<T>(getLogger(), txn, work);
   }

   @Override
   public AtomicNumber getAtomicNumber(String name) {
      return getProxyObject(AtomicNumberProxy.class, com.hazelcast.core.AtomicNumber.class,
         getHazelcastInstance().getAtomicNumber(name));
   }

   @SuppressWarnings({"unchecked"})
   @Override
   public <E> BlockingQueue<E> getQueue(String name) {
      return getProxyObject(DistributedBlockingQueueProxy.class, IQueue.class, getHazelcastInstance().getQueue(name));
   }

   @SuppressWarnings({"unchecked"})
   @Override
   public <E> Set<E> getSet(String name) {
      return getProxyObject(DistributedSetProxy.class, ISet.class, getHazelcastInstance().getSet(name));
   }

   @SuppressWarnings({"unchecked"})
   @Override
   public <E> List<E> getList(String name) {
      return getProxyObject(DistributedListProxy.class, IList.class, getHazelcastInstance().getList(name));
   }

   @SuppressWarnings({"unchecked"})
   @Override
   public <K, V> DistributedMap<K, V> getMap(String name) {
      return getProxyObject(DistributedMapProxy.class, IMap.class, getHazelcastInstance().getMap(name));
   }

   @SuppressWarnings({"unchecked"})
   @Override
   public <K, V> DistributedMultiMap<K, V> getMultiMap(String name) {
      return getProxyObject(DistributedMultiMapProxy.class, MultiMap.class, getHazelcastInstance().getMultiMap(name));
   }

   @Override
   public DistributedId getIdGenerator(String name) {
      return getProxyObject(DistributedIdProxy.class, IdGenerator.class, getHazelcastInstance().getIdGenerator(name));
   }

   @Override
   public DistributedLock getLock(Object key) {
      return getProxyObject(DistributedLockProxy.class, ILock.class, getHazelcastInstance().getLock(key));
   }

   @SuppressWarnings("unchecked")
   private <T extends DistributedObject> T getProxyObject(Class<T> proxyClass, Class<?> hClazz, Instance instance) {
      Object key = instance.getId();
      T proxy = (T) distributedObjects.get(key);
      if (proxy == null) {
         try {
            Constructor<T> constructor = proxyClass.getConstructor(hClazz);
            proxy = constructor.newInstance(instance);
            distributedObjects.put(key, proxy);
         } catch (Exception ex) {
            logger.error(ex, "Error creating proxy object");
         }
      }
      return proxy;
   }

   private final class ProxyCleaner implements InstanceListener {

      @Override
      public void instanceCreated(InstanceEvent event) {
         // Do nothing
      }

      @Override
      public void instanceDestroyed(InstanceEvent event) {
         Instance instance = event.getInstance();
         distributedObjects.remove(instance.getId());
      }
   }
}
