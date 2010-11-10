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
package org.eclipse.osee.ote.core.log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.serialize.OutputFormat;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.log.record.ScriptInitRecord;
import org.eclipse.osee.ote.core.log.record.ScriptResultRecord;
import org.eclipse.osee.ote.core.log.record.TestCaseRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.eclipse.osee.ote.core.log.record.TraceRecord;
import org.eclipse.osee.ote.core.log.record.TraceRecordEnd;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Charles Shaw
 * @author Robert A. Fisher
 */
public class ScriptLogHandler extends Handler {
   private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   protected Element testCaseElement;
   protected Element parent;
   protected Element child;
   private final OutputFormat format;
   protected Element testScriptElement;
   protected Element scriptInitElement;
   protected Document document;
   private final File outFile;
   private final List<LogRecord> records;

   /**
    * ScriptLogHandler Constructor. Sets the outfile to log to and the test script that will be logged. It also
    * establishes the xml format to be used.
    * 
    * @param outFile Reference to the outfile that will be used to output the log.
    */
   public ScriptLogHandler(File outFile, TestEnvironment testEnvironment) {
      super();
      GCHelper.getGCHelper().addRefWatch(this);
      this.outFile = outFile;
      OseeLog.log(TestEnvironment.class, Level.FINE, outFile.getAbsolutePath());

      try {
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         document = builder.newDocument();
      } catch (ParserConfigurationException ex) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
      }

      // create an XMLOutputter that indents using 3 spaces and uses newlines
      format = new OutputFormat(document);
      format.setLineSeparator("\n");
      format.setIndenting(true);
      format.setIndent(3);

      //      Jaxp.setXslProperty(document, getXSLTransformName());

      ProcessingInstruction processingInstruction =
         document.createProcessingInstruction("xml-stylesheet",
            "type=\"text/xsl\" href=\"" + getXSLTransformName() + "\"");
      document.appendChild(processingInstruction);
      records = new ArrayList<LogRecord>();

      document.appendChild(document.createComment("INSERT DISTRO STATEMENT HERE"));

      this.testScriptElement = document.createElement("TestScript");
      this.scriptInitElement = document.createElement("ScriptInit");
      document.appendChild(testScriptElement);
      this.parent = this.testScriptElement;
   }

   public String getXSLTransformName() {
      return "outputNormal.xsl";
   }

   /**
    * Write out the log file to the outfile.
    */
   public void writeOutFile() {
      try {
         Jaxp.writeXmlDocument(document, outFile, format);
      } catch (TransformerException ex) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
      } catch (IOException ex) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
      } catch (Throwable th) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, th);
      }
   }

   @Override
   public synchronized void publish(LogRecord logRecord) {
      if (isLoggable(logRecord)) {
         records.add(logRecord);
      }
   }

   public synchronized void flushRecords() {
      try {
         for (int i = 0; i < records.size(); i++) {
            LogRecord logRecord = records.get(i);

            if (logRecord instanceof TestRecord) {
               TestRecord record = (TestRecord) logRecord;
               child = record.toXml(document);

               if (record instanceof TestCaseRecord) {
                  testCaseElement = child;
                  testScriptElement.appendChild(testCaseElement);
                  parent = testCaseElement;
               } else {

                  if (record instanceof ScriptResultRecord) {
                     testScriptElement.appendChild(child);
                  } else if (record instanceof ScriptInitRecord) {
                     if (((ScriptInitRecord) record).getStartFlag()) {
                        // We are doing it this way so that it is chronologically accurate in the xml.
                        if (scriptInitElement.getParentNode() == null) {
                           testScriptElement.appendChild(scriptInitElement);
                        }
                        parent = scriptInitElement;
                     } else {
                        parent = testScriptElement;
                     }
                  } else {
                     parent.appendChild(child);
                     if (record instanceof TraceRecord) {// method began
                        parent = child;
                     } else if (record instanceof TraceRecordEnd) {// method ended
                        if (parent.getParentNode() != null) {
                           parent = (Element) parent.getParentNode();
                        }
                     }
                  }
               }
            } else {
               if (parent != null) {
                  Element el = document.createElement("OteLog");
                  el.setAttribute("Level", logRecord.getLevel().getLocalizedName());
                  el.setAttribute("Logger", logRecord.getLoggerName());
                  Element msg = document.createElement("Message");
                  msg.appendChild(document.createTextNode(logRecord.getMessage()));
                  el.appendChild(msg);

                  if (logRecord.getThrown() != null) {

                     try {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        logRecord.getThrown().printStackTrace(pw);
                        pw.close();

                        Element thrown = document.createElement("Throwable");
                        thrown.appendChild(document.createTextNode(sw.toString()));
                        el.appendChild(thrown);
                     } catch (Exception ex) {
                     }
                  }
                  parent.appendChild(el);
               }
            }
         }
      }
      catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         records.clear();
      }
   }

   @Override
   public void close() throws SecurityException {
      writeOutFile();
   }

   @Override
   public void flush() {
      // don't call this method

   }
}