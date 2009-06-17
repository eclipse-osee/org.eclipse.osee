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
package org.eclipse.osee.ote.ui.test.manager.core;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Base Class for all TestManagers
 */
public class TestManagerModel {

   
   final static String CONFIGURATION = "configuration";
   final static String CONTACT = "contact";
   final static String DESCRIPTION = "description";
   final static String MACHINE = "machine";
   final static String OFP = "ofp";
   final static String PORT = "port";
   final static String ROOTNAME = "testManager";
   final static String TARGET = "target";
   public boolean finished = false;
   private String configuration = "";
   private String contact = "";
   private String description = "";
   private String[] ofps = null;
   private List<String> parseExceptions = new ArrayList<String>();
   private String rawXml = "";
   private String[] targets = null;
   protected String filename = "";

   public TestManagerModel() {
   }

   /**
    * @return Returns the configuration.
    */
   public String getConfiguration() {
      return configuration;
   }

   /**
    * @return Returns the contact.
    */
   public String getContact() {
      return contact;
   }

   /**
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return Returns the ofps.
    */
   public String[] getOfps() {
      return ofps;
   }

   /**
    * @return Returns the parseExceptions.
    */
   public String getParseExceptions() {
      String str = "";
      for (int i = 0; i < parseExceptions.size(); i++) {
         str += parseExceptions.get(i) + "\n";
      }
      return str;
   }

   /**
    * @return Returns the rawXml.
    */
   public String getRawXml() {
      return rawXml;
   }

   /**
    * @return Returns the targets.
    */
   public String[] getTargets() {
      return targets;
   }

   public boolean hasParseExceptions() {
      return !parseExceptions.isEmpty();
   }

   /**
    * @param configuration The configuration to set.
    */
   public void setConfiguration(String configuration) {
      this.configuration = configuration;
   }

   /**
    * @param contact The contact to set.
    */
   public void setContact(String contact) {
      this.contact = contact;
   }

   /**
    * @param description The description to set.
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * load model from xmlString; return true if successful
    */
   public boolean setFromXml(String xmlString) {
      parseExceptions.clear();
      try {
         // Create a DOM builder and parse the fragment
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

         // Call this to catch parse errors
         factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlString)));

         parseDocument(xmlString);

      }
      catch (ParserConfigurationException e) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, e.toString());
         parseExceptions.add(e.toString());
         return false;
      }
      catch (SAXException e) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, e.toString());
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, "xmlText *" + xmlString + "*");
         parseExceptions.add(e.toString());
         return false;
      }
      catch (IOException e) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, e.toString());
         parseExceptions.add(e.toString());
         return false;
      }
      rawXml = xmlString;
      return true;
   }

   /**
    * load model from filename
    * 
    * @param filename the Name of the file to be read
    */
   public void setFromXmlFile(String filename) {
      this.filename = filename;
      setFromXml(AFile.readFile(filename));
   }

   /**
    * @param ofps The ofps to set.
    */
   public void setOfps(String[] ofps) {
      this.ofps = ofps;
   }

   /**
    * @param targets The targets to set.
    */
   public void setTargets(String[] targets) {
      this.targets = targets;
   }

   private void parseDocument(String xmlString) {
      description = AXml.getTagData(xmlString, DESCRIPTION);
      contact = AXml.getTagData(xmlString, CONTACT);
      ofps = AXml.getTagDataArray(xmlString, OFP);
      String targetArray[] = AXml.getTagDataArray(xmlString, TARGET);
      List<String> targetVector = new ArrayList<String>();
      for (int i = 0; i < targetArray.length; i++) {
         String machine = AXml.getTagData(targetArray[i], MACHINE);
         String port = AXml.getTagData(targetArray[i], PORT);
         if (machine != null && port != null)
            targetVector.add(machine + "," + port);
      }
      targets = targetVector.toArray(new String[targetVector.size()]);

      OseeLog.log(TestManagerPlugin.class, Level.INFO, "description *" + description + "*");
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "contact *" + contact + "*");
      for (int i = 0; i < ofps.length; i++) {
         OseeLog.log(TestManagerPlugin.class, Level.INFO, "ofp *" + ofps[i] + "*");
      }
      for (int i = 0; i < targets.length; i++) {
         OseeLog.log(TestManagerPlugin.class, Level.INFO, "target *" + targets[i] + "*");
      }
   }
}
