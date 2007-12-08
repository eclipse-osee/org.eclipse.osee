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
package org.eclipse.osee.framework.ui.plugin.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * This class provides a front end to writing files to a common osee.data directory in the workspace. This dir is
 * invisible to Eclipse Navigator and Package Explorer. It is provided as a common repository for files that need to be
 * created and retained by any plugin, but don't need to be visible to the user. This class does nothing more than
 * ensure the directory is created and provide a way to get the path for other plugins to use.
 * 
 * @author Donald G. Dunne
 */
public class OseeData {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(OseeData.class);

   private static final IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation();

   private static String oseeDataPathName = ".osee.data";
   private static final IPath oseeDataPath = workspacePath.append(oseeDataPathName);

   private static final File oseeDir = oseeDataPath.toFile();

   private static final String dataStoreName = "OseeDataStore";

   private static IProject project;

   private static Map<String, String> keyValue;

   static {
      if (!oseeDir.exists()) {
         if (!oseeDir.mkdir()) {
            System.err.println("Can't create " + oseeDataPathName + " dir.");
         }
      }

      createProject();
      initKeyValueDataStore();
   }

   public static IPath getPath() {
      return oseeDataPath;
   }

   private static void initKeyValueDataStore() {
      keyValue = new HashMap<String, String>();
      loadFromFile();
   }

   public static File getFile(String filename) {
      return new File(oseeDir, filename);
   }

   public static IFile getIFile(String fileName) {
      return project.getFile(fileName);
   }

   public static IFile getIFile(String fileName, InputStream in) throws CoreException, IOException {
      return getIFile(fileName, in, false);
   }

   public static IFile getIFile(String fileName, InputStream in, boolean overwrite) throws CoreException, IOException {
      IFile iFile = project.getFile(fileName);
      if (!iFile.exists() || overwrite) {
         AIFile.writeToFile(iFile, in);
      }
      return iFile;
   }

   private static boolean createProject() {
      IWorkspaceRoot root = OseeUiActivator.getWorkspaceRoot();
      project = root.getProject(oseeDataPathName);
      if (!project.exists()) {
         try {
            project.create(null);
         } catch (CoreException ex) {
            ex.printStackTrace();
            return false;
         }
      }
      try {
         project.open(null);
      } catch (CoreException e) {
         e.printStackTrace();
         return false;
      }
      return true;
   }

   public static String getValue(String key) {
      return keyValue.get(key);
   }

   public static void setValue(String key, String value) {
      keyValue.put(key, value);
      try {
         saveToFile();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private static void loadFromFile() {
      keyValue.clear();

      try {
         Document doc = Jaxp.readXmlDocument(getFile());

         Element root = doc.getDocumentElement();
         List<Element> pairs = Jaxp.getChildDirects(root, "Pair");

         for (Element pair : pairs) {
            String key = Jaxp.getChildText(pair, "Key");
            String value = Jaxp.getChildText(pair, "Value");
            if (key != null && value != null && !key.equals("") && !value.equals("")) keyValue.put(key, value);
         }
      } catch (ParserConfigurationException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (SAXException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (IOException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (CoreException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   private static void saveToFile() throws ParserConfigurationException, TransformerException, IOException, CoreException {
      Document doc = Jaxp.newDocument();
      Element root = doc.createElement(dataStoreName);
      doc.appendChild(root);

      Set<Map.Entry<String, String>> keySet = keyValue.entrySet();
      for (Map.Entry<String, String> entry : keySet) {
         String key = entry.getKey();
         String value = entry.getValue();
         if (!key.equals("") && !value.equals("")) {
            Element pair = doc.createElement("Pair");
            pair.appendChild(Jaxp.createElement(doc, "Key", key));
            pair.appendChild(Jaxp.createElement(doc, "Value", value));
         }

      }
      Jaxp.writeXmlDocument(doc, getFile());
   }

   private static File getFile() throws IOException, CoreException {
      PipedOutputStream pos = new PipedOutputStream();
      PrintStream ps = new PrintStream(pos);
      InputStream in = new PipedInputStream(pos);
      ps.println("<" + dataStoreName + "></" + dataStoreName + ">");
      ps.close();
      return getIFile(OseeData.class.getCanonicalName() + "." + dataStoreName + ".xml", in).getLocation().toFile();
   }

   /**
    * @return Returns the project.
    */
   public static IProject getProject() {
      return project;
   }

   public static IFolder getFolder(String name) throws CoreException {
      IFolder folder = project.getFolder(name);

      if (!folder.exists()) {
         folder.create(true, true, null);
      }
      return folder;
   }
}