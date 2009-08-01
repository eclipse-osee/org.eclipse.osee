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
package org.eclipse.osee.framework.jdk.core.text;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RulesLogHandler extends Handler {

   private Document document;
   private File outFile;
   private Element rootElement;

   /**
    * RulesLogHandler Constructor. Sets the file to log to and the test script that will be logged. It also establishes
    * the XML format to be used.
    * 
    * @param outFile Reference to the file that will be used to output the log.
    * @throws ParserConfigurationException
    */
   public RulesLogHandler(File outFile) throws ParserConfigurationException {
      super();
      this.outFile = outFile;
      document = Jaxp.newDocument();
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
            // create an XMLOutputter that indents using 3 spaces and uses new lines
            OutputFormat format = new OutputFormat(document);
            format.setIndenting(true);
            format.setIndent(3);
            Jaxp.writeXmlDocument(document, outFile, format);
         } catch (IOException ex) {
            ex.printStackTrace();
         } catch (TransformerException ex) {
            ex.printStackTrace();
         }
      }
   }

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

   public void close() throws SecurityException {
      writeOutFile();
   }

   public void flush() {
      throw new UnsupportedOperationException();
   }
}