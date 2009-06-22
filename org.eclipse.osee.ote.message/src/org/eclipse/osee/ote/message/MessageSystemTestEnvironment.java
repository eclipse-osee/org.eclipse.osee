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
package org.eclipse.osee.ote.message;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.BasicTimeout;
import org.eclipse.osee.ote.core.environment.interfaces.IEnvironmentFactory;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.log.Env;
import org.eclipse.osee.ote.core.model.IModelManager;
import org.eclipse.osee.ote.message.instrumentation.IOInstrumentation;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 * @author Andrew M. Finkbeiner
 */
public abstract class MessageSystemTestEnvironment extends TestEnvironment implements ITestEnvironmentMessageSystemAccessor, IMessageTestContext {
   protected URL[] clientClasses;
   private final List<IPreScriptInstantiation> preInstantiation = new ArrayList<IPreScriptInstantiation>();
   protected boolean promptResponse= false;
   private IOInstrumentationDB ioInstrumentation;


   /**
    * @throws IOException
    * @throws UnknownHostException
    * @throws MalformedURLException
    */
   protected MessageSystemTestEnvironment(IEnvironmentFactory factory) {
      super(factory);
      getScriptCtrl().setScriptReady(false);
   }

   private void setupIOInstrumentation(){
      if(ioInstrumentation == null){
         ioInstrumentation = new IOInstrumentationDB();
      }
   }

   public void envWait(int milliseconds) throws InterruptedException {
      envWait(new BasicTimeout(), milliseconds);
   }

   public void envWait(ITimeout obj, int milliseconds) throws InterruptedException {
      setTimerFor(obj, milliseconds);
      synchronized (obj) {
         obj.wait();
      }
   }

   public IMessageManager getMsgManager() {
      ServiceTracker tracker = getServiceTracker(IMessageManager.class.getName());
      return (IMessageManager)tracker.getService();
   }

   public IModelManager getModelManager() {
      ServiceTracker tracker = getServiceTracker(IModelManager.class.getName());
      return (IModelManager)tracker.getService();
   }

   @Deprecated
   public boolean isMessageJarAvailable(String version) {
      return getRuntimeManager().isMessageJarAvailable(version);
   }

   public boolean isBundleAvailable(String symbolicName, String version, byte[] md5Digest) {
      return getRuntimeManager().isBundleAvailable(symbolicName, version, md5Digest);
   }

   public void shutdown() {
      super.shutdown();
   }

   /**
    * provides a way for sub classes to instantiate test scripts in their own way.
    * 
    * @param scriptClass
    * @param connection
    * @return TestScript
    * @throws NoSuchMethodException
    * @throws InstantiationException
    * @throws IllegalAccessException
    * @throws InvocationTargetException
    */
   protected abstract TestScript instantiateScriptClass(Class<?> scriptClass, IUserSession connection) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;

   @Deprecated
   public void sendRuntimeJar(byte[] messageJar) throws IOException {
      getRuntimeManager().addJarToClassLoader(messageJar);
   }

   public void sendRuntimeBundle(Collection<BundleDescription> bundles) throws Exception {
      getRuntimeManager().loadBundles(bundles);
   }

   public void updateRuntimeBundle(Collection<BundleDescription> bundles) throws Exception {
      getRuntimeManager().updateBundles(bundles);
   }

   public void cleanupRuntimeBundles() throws Exception {
      if(isNoBundleCleanup()){
         return;
      } else {
         getRuntimeManager().cleanup();
         cleanupClassReferences();
      }
   }

   private boolean isNoBundleCleanup() {
      return Boolean.valueOf(System.getProperty("osee.ote.nobundlecleanup"));
   }

   public abstract void singleStepEnv();

   public void wakeScript() {
      synchronized (getTestScript()) {
         System.out.println("notifying");
         getTestScript().notifyAll();
      }
   }

   protected synchronized boolean waitForPromptResponse() {
      this.promptResponse = false;
      int count = 0;
      while (count < 120) {
         if (this.promptResponse) {
            return true;
         }
         try {
            Thread.sleep(1000);
         } catch (InterruptedException ex) {
            Env.getInstance().exception(ex);
         }
         count++;
      }
      return false;
   }

   public void setClientClasses(URL[] urls) throws RemoteException {
      clientClasses = urls;
   }

   @Override
   protected void cleanupClassReferences() {
      IMessageManager msgMngr = getMsgManager();
      if(msgMngr != null){
         msgMngr.destroy();
      }
      IModelManager modelMngr = getModelManager();
      if(modelMngr != null) {
         modelMngr.destroy();
      }

      super.cleanupClassReferences();
   }

   public void resetScriptLoader(String[] strings) throws Exception {
      getRuntimeManager().resetScriptLoader(strings);
   }

   @Deprecated
   public Class<?> loadClassFromScriptLoader(String path) throws ClassNotFoundException {
      return getRuntimeManager().loadFromScriptClassLoader(path);
   }

   @Deprecated
   public void addPreInstantiationListener(IPreScriptInstantiation listener) {
      preInstantiation.add(listener);
   }

   @Deprecated
   public void removePreInstantiationListener(IPreScriptInstantiation listener) {
      preInstantiation.remove(listener);
   }

   @Deprecated
   public void notifyPreInstantiationListeners() {
      for (IPreScriptInstantiation pre : preInstantiation) {
         pre.run();
      }
   }

   @Deprecated
   public IOInstrumentation getIOInstrumentation(String name) {//, IOInstrumentation io){
      setupIOInstrumentation();
      return ioInstrumentation.getIOInstrumentation(name);
   }

   @Deprecated
   public IOInstrumentation registerIOInstrumentation(String name, IOInstrumentation io) {
      setupIOInstrumentation();
      return ioInstrumentation.registerIOInstrumentation(name, io);
   }

   public void deregisterIOInstrumentation(String name) {
      setupIOInstrumentation();
      ioInstrumentation.unregisterIOInstrumentation(name);
   }

   public void addInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) {
      setupIOInstrumentation();
      ioInstrumentation.addRegistrationListener(listener);
   }

   public void removeInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) {
      setupIOInstrumentation();
      ioInstrumentation.removeRegistrationListener(listener);
   }

   @Deprecated
   public Class<?> loadClassFromMessageLoader(String path) throws ClassNotFoundException {
      return getRuntimeManager().loadFromRuntimeLibraryLoader(path);
   }
}