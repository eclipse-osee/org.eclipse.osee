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
package org.eclipse.osee.ote.message.log.record;

import java.io.File;
import java.util.Map;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class MessageJarConfigrecord extends TestRecord {

   private String[] jarVersions;

   /**
    * 
    */
   private static final long serialVersionUID = 6919229589873467398L;

   /**
    * ScriptConfigRecord Constructor. Constructs test script configuration log message with timestamp.
    * 
    * @param script The test script who's configuration is to be recorded.
    */
   public MessageJarConfigrecord(TestScript script, String[] jarVersions, Map<String, File> availableJars) {
      super(script.getTestEnvironment(), TestLevel.CONFIG, script.getClass().getName(), false);
      this.jarVersions = jarVersions;
   }

   /**
    * Convert an element to XML format.
    * 
    * @return XML formated config element.
    */
   public Element toXml(Document doc) {
      Element jarConfig = doc.createElement("JarConfig");
      doc.appendChild(jarConfig);

      for (String version : jarVersions) {
         Element el = doc.createElement("Jar");
         el.setTextContent(version);
         jarConfig.appendChild(el);
      }
      return jarConfig;
   }
}