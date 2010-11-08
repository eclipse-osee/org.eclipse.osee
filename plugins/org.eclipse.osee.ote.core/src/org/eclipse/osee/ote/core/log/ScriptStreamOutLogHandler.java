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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.log.record.ScriptInitRecord;
import org.eclipse.osee.ote.core.log.record.ScriptResultRecord;
import org.eclipse.osee.ote.core.log.record.TestCaseRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.eclipse.osee.ote.core.log.record.TraceRecordEnd;

/**
 * @author Andrew M. Finkbeiner
 */
public class ScriptStreamOutLogHandler extends Handler {
   private final List<LogRecord> records;
   private FileOutputStream outputStream;
   private XMLStreamWriter writer;

   /**
    * ScriptLogHandler Constructor. Sets the outfile to log to and the test script that will be logged. It also
    * establishes the xml format to be used.
    * 
    * @param outFile Reference to the outfile that will be used to output the log.
    */
   public ScriptStreamOutLogHandler(File outFile) {
      super();
      records = new ArrayList<LogRecord>();
      try {
         outputStream = new FileOutputStream(outFile);
         XMLOutputFactory factory = XMLOutputFactory.newInstance();
         writer = factory.createXMLStreamWriter(outputStream);
         writer.writeStartDocument("1.0");
         writer.writeComment("DISTRO_STATEMENT_HERE");
         writer.writeStartElement("TestScript");
         OseeLog.log(TestEnvironment.class, Level.FINE, outFile.getAbsolutePath());
      } catch (FileNotFoundException ex) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
      } catch (XMLStreamException ex) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
      }
   }

   public String getXSLTransformName() {
      return "outputNormal.xsl";
   }

   @Override
   public synchronized void publish(LogRecord logRecord) {
      if (isLoggable(logRecord)) {
         records.add(logRecord);
      }
   }

   public synchronized void flushRecords() {
      boolean started = false;
      try {
         for (int i = 0; i < records.size(); i++) {
            LogRecord logRecord = records.get(i);

            if (logRecord instanceof TestRecord) {
               TestRecord record = (TestRecord) logRecord;
               if (started && isTopLevelElement(record)) {
                  started = true;
                  record.toXml(writer);
               } else if (isTopLevelElement(record)) {
                  writer.writeEndElement();
                  record.toXml(writer);
               } else if (record instanceof TraceRecordEnd) {
                  record.toXml(writer);
                  writer.writeEndElement();
               } else {
                  record.toXml(writer);
               }
            } else {
               writer.writeStartElement("OteLog");
               writer.writeAttribute("Level", logRecord.getLevel().getLocalizedName());
               writer.writeAttribute("Logger", logRecord.getLoggerName());
               writer.writeStartElement("Message");
               writer.writeCharacters(logRecord.getMessage());
               writer.writeEndElement();
               if (logRecord.getThrown() != null) {
                  StringWriter sw = new StringWriter();
                  PrintWriter pw = new PrintWriter(sw);
                  logRecord.getThrown().printStackTrace(pw);
                  pw.close();
                  writer.writeStartElement("Throwable");
                  writer.writeCharacters(sw.toString());
                  writer.writeEndElement();
               }
               writer.writeEndElement();
            }
         }
      } catch (XMLStreamException ex) {
         OseeLog.log(ScriptStreamOutLogHandler.class, Level.SEVERE, ex);
      } finally {
         records.clear();
      }
   }

   private boolean isTopLevelElement(TestRecord record) {
      return record instanceof TestCaseRecord || record instanceof ScriptResultRecord || record instanceof ScriptInitRecord;
   }

   @Override
   public void close() throws SecurityException {
      try {
         writer.writeEndDocument();
         writer.flush();
         writer.close();
         outputStream.close();
      } catch (IOException ex) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
      } catch (XMLStreamException ex) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void flush() {
      // don't call this method
   }
}