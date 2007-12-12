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
package org.eclipse.osee.framework.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.update.core.BaseInstallHandler;
import org.eclipse.update.core.ContentReference;
import org.eclipse.update.core.IFeatureContentConsumer;
import org.eclipse.update.core.IFeatureContentProvider;
import org.eclipse.update.core.INonPluginEntry;
import org.eclipse.update.core.IVerificationListener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeInstallHandler extends BaseInstallHandler {

   private Logger logger = ConfigUtil.getConfigFactory().getLogger(OseeInstallHandler.class);
   private File pluginFile = null;

   /**
    * @throws InterruptedException
    * @throws IOException
    * @throws InvalidRegistryObjectException
    * @throws ClassNotFoundException
    * @throws IllegalAccessException
    * @throws InstantiationException
    * @throws SAXException
    * @throws ParserConfigurationException
    */
   @SuppressWarnings("unchecked")
   private void processInstallTaskExtensionPoints() throws InvalidRegistryObjectException, IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParserConfigurationException, SAXException {

      if (pluginFile != null) {
         Document doc = Jaxp.readXmlDocument(pluginFile);
         NodeList list = doc.getElementsByTagName("extension");
         for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            Node point = node.getAttributes().getNamedItem("point");
            if (point.getTextContent().equals("org.eclipse.osee.framework.updater.InstallJob")) {
               NodeList installJobElements = node.getChildNodes();

               for (int j = 0; j < installJobElements.getLength(); j++) {
                  Node installJobElement = installJobElements.item(j);
                  System.out.println(installJobElement.getNodeType());
                  if (Node.ELEMENT_NODE == installJobElement.getNodeType()) {
                     System.out.println(installJobElement);
                     Node classNode = installJobElement.getAttributes().getNamedItem("class");
                     if (classNode != null) {
                        System.out.println(String.format("Searching for class [%s].", classNode.getTextContent()));
                        Class clazz =
                              Platform.getBundle("org.eclipse.osee.framework.updater").loadClass(
                                    classNode.getTextContent());
                        IInstallJob job = (IInstallJob) clazz.newInstance();
                        System.out.println(String.format("Class found and instantiated."));
                        job.run();
                        System.out.println(String.format("Job Complete."));
                     }
                  }
               }
            }
         }
      }
   }

   public void nonPluginDataDownloaded(INonPluginEntry[] nonPluginData, IVerificationListener listener) throws CoreException {

      if (nonPluginData == null || nonPluginData.length == 0) return;

      try {
         URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("dlResources"), null);
         url = FileLocator.resolve(url);
         File copyToFolder = new File(url.getFile());

         this.nonPluginEntries = nonPluginData;

         IFeatureContentProvider provider = this.feature.getFeatureContentProvider();

         for (int i = 0; i < this.nonPluginEntries.length; i++) {
            ContentReference[] archives =
                  provider.getNonPluginEntryArchiveReferences(nonPluginEntries[i], this.monitor);
            for (ContentReference archive : archives) {
               try {
                  File destination = new File(copyToFolder, archive.getIdentifier());
                  destination.deleteOnExit();
                  File source = new File(archive.asFile().getAbsolutePath());

                  FileInputStream in = new FileInputStream(source);
                  destination.delete(); // to work around some file permission
                  destination.getParentFile().mkdirs();
                  FileOutputStream out = new FileOutputStream(destination);
                  byte[] bytes = new byte[(int) source.length()];
                  in.read(bytes);
                  out.write(bytes);
                  in.close();
                  out.close();

                  Lib.copyFile(new File(archive.asFile().getAbsolutePath()), destination);
                  System.out.println(archive.getIdentifier());
                  System.out.println(destination.getAbsolutePath());

                  if ("plugin.xml".equalsIgnoreCase(archive.getIdentifier())) {
                     this.pluginFile = destination;
                  }

               } catch (IOException ex) {
                  logger.log(Level.SEVERE, ex.toString(), ex);
               }
            }
         }
      } catch (IOException ex1) {
         logger.log(Level.SEVERE, ex1.toString(), ex1);
      } catch (CoreException ex) {
         ex.printStackTrace();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.update.core.BaseInstallHandler#completeInstall(org.eclipse.update.core.IFeatureContentConsumer)
    */
   @Override
   public void completeInstall(IFeatureContentConsumer consumer) throws CoreException {
      super.completeInstall(consumer);
      try {
         processInstallTaskExtensionPoints();
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
         consumer.abort();
      }
   }
}
