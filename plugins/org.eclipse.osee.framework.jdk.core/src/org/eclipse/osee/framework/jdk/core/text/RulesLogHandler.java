/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.text;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RulesLogHandler extends Handler {

   private final Document document;
   private final File outFile;
   private final Element rootElement;

   /**
    * RulesLogHandler Constructor. Sets the file to log to and the test script that will be logged. It also establishes
    * the XML format to be used.
    * 
    * @param outFile Reference to the file that will be used to output the log.
    */
   public RulesLogHandler(File outFile) throws ParserConfigurationException {
      this.outFile = outFile;
      document = Jaxp.newDocumentNamespaceAware();
      rootElement = document.createElement("Rule");
      document.appendChild(rootElement);
   }

   /**
    * Write out the log records as XML.
    */
   public void writeOutFile() {
      // only write the XML file if there is something to write
      if (rootElement.hasChildNodes()) {
         try {
            Jaxp.writeXmlDocument(document, outFile, Jaxp.getPrettyFormat());
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }

   @Override
   public void publish(LogRecord logRecord) {
      if (!isLoggable(logRecord)) {
         return;
      }

      if (logRecord instanceof RuleRecord) {
         RuleRecord record = (RuleRecord) logRecord;
         rootElement.appendChild(record.toXml(document));
      } else {
         rootElement.appendChild(Jaxp.createElement(document, logRecord.getLevel().getName(), logRecord.getMessage()));
      }
   }

   @Override
   public void close() throws SecurityException {
      writeOutFile();
   }

   @Override
   public void flush() {
      throw new UnsupportedOperationException();
   }
}