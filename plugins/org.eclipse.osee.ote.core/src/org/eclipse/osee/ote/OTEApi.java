package org.eclipse.osee.ote;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.model.IModelManager;

/**
 * This is an API that exposes some of the key test environment interfaces and operations.  It is expected
 * that clients of OTE will operate either directly through this API or indirectly using a remote REST or 
 * Event API that makes use of this API.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public interface OTEApi {
   
   /**
    * Returns the OTEServerFolder service that enables management of the ote server folder area on
    * the machine local to the server.
    * 
    * @return the OTEServerFolder service
    */
   OTEServerFolder getServerFolder();
   
   /**
    * Returns the OTEServerJarCache that can be used for saving and getting files from the local
    * ote server data store.
    * 
    * @return the OTEServerJarCache service
    */
   OTEServerRuntimeCache getRuntimeCache();
   
   /**
    * Returns the IModelManager service that will give access to generic model controls.  It depends on the TestEnvironmentInterface exisiting or it 
    * will be a null value.
    * 
    * @return null or a valid IModelManager service
    */
   IModelManager getModelManager();
   
   /**
    * Returns the TestEnvironmentInterface service that is a top level object for interacting with the 
    * environment.  It depends on a valid TestEnvironmentInterface getting registered.  If we do not 
    * have a valid test environment it will return null.  Generally a valid test environment will exist
    * as long as a configuration has been loaded, this can happen if a user connects to a shell server
    * or if we have an RCP that contains all the required startup information and bundles.
    * 
    * @return null or a valid TestEnvironmentInterface service
    */
   TestEnvironmentInterface getTestEnvironment();
   
   
   /**
    * This method will make sure that the requested configuration is available to the server and then install them.  
    * Only jars that are proper OSGI bundles will be loaded.  This also means that if configuration items are 
    * not currently available to the server it will acquire them from the client using the URL in the configuration item.
    * 
    * @param configuration
    * @param callable
    * @return
    * @throws InterruptedException
    * @throws ExecutionException
    */
   Future<ConfigurationStatus> loadConfiguration(Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable) throws InterruptedException, ExecutionException;
   
   /**
    * This method will uninstall the previous bundle configuration.
    * 
    * @param callable
    * @return
    * @throws InterruptedException
    * @throws ExecutionException
    */
   Future<ConfigurationStatus> resetConfiguration(OTEStatusCallback<ConfigurationStatus> callable) throws InterruptedException, ExecutionException;
   
   /**
    * Gets the current test environment configuration.
    * 
    * @return
    */
   Future<ConfigurationStatus> getConfiguration();
   
   /**
    * This method will make all bundles defined in the configuration available to the server.  This means the server will attempt to use the URL in the configuration item
    * to download any bundles not currently in its cache.
    * 
    * @param configuration
    * @param callable
    * @return
    * @throws InterruptedException
    * @throws ExecutionException
    */
   Future<ConfigurationStatus> downloadConfigurationJars(Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable) throws InterruptedException, ExecutionException;
   
   /**
    * Attempt to load a class from currently installed bundles and then from the script classpath.
    * 
    * @param clazz
    * @return the requested Class
    * @throws ClassNotFoundException
    */
   Class<?> loadFromScriptClassLoader(String clazz) throws ClassNotFoundException;

   /**
    * Attempt to load a class from any currently installed bundled.
    * 
    * @param clazz
    * @return the requested Class
    * @throws ClassNotFoundException
    */
   Class<?> loadFromRuntimeLibraryLoader(String clazz) throws ClassNotFoundException;
}
