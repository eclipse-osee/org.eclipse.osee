package org.eclipse.osee.framework.db.connection;

import org.eclipse.osee.framework.db.connection.impl.DbConnectionFactory;
import org.eclipse.osee.framework.db.connection.impl.DbConnectionInfoTracker;
import org.eclipse.osee.framework.db.connection.impl.DbConnectionInformationImpl;
import org.eclipse.osee.framework.db.connection.impl.DbConnectionProviderTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

   private DbConnectionProviderTracker tracker;
   private DbConnectionInfoTracker infoTracker;
   private static Activator me;
   private BundleContext context;
   private ServiceRegistration dbConnectionFactoryRegistration;
   private BindTracker connectionTracker;
   private BindTracker infoProviderTracker;
   private ServiceRegistration dbConnectionInfoProviderRegistration;

   public static Activator getInstance() {
      return me;
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      me = this;
      this.context = context;

      DbConnectionFactory dbConnectionFactory = new DbConnectionFactory();
      connectionTracker = new BindTracker(context, IConnection.class.getName(), dbConnectionFactory);
      connectionTracker.open();
      dbConnectionFactoryRegistration =
            context.registerService(IDbConnectionFactory.class.getName(), dbConnectionFactory, null);

      DbConnectionInformationImpl dbConnectionInfo = new DbConnectionInformationImpl();
      infoProviderTracker =
            new BindTracker(context, IDbConnectionInformationContributer.class.getName(), dbConnectionInfo);
      infoProviderTracker.open();
      dbConnectionInfoProviderRegistration =
            context.registerService(IDbConnectionInformation.class.getName(), dbConnectionInfo, null);

      tracker = new DbConnectionProviderTracker(context);
      tracker.open();
      infoTracker = new DbConnectionInfoTracker(context);
      infoTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      me = null;
      tracker.close();
      infoTracker.close();
      connectionTracker.close();
      infoProviderTracker.close();
      dbConnectionFactoryRegistration.unregister();
      dbConnectionInfoProviderRegistration.unregister();
   }

   public IDbConnectionFactory getDbConnectionFactory() {
      return tracker.getFactory();
   }

   public IDbConnectionInformation getDbConnectionInformation() {
      return infoTracker.get();
   }

   public BundleContext getBundleContext() {
      return this.context;
   }

}
