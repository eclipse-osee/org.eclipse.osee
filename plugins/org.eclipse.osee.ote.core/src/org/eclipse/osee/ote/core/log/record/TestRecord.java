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
package org.eclipse.osee.ote.core.log.record;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Michael A. Winston
 */
public abstract class TestRecord extends LogRecord implements Xmlizable, XmlizableStream {
   private static final long serialVersionUID = 2663140700880844240L;

   private static boolean filterTheStacktrace = true;

   private static final ArrayList<Pattern> stacktraceExcludes = new ArrayList<Pattern>(32);
   private static final ArrayList<Pattern> stacktraceIncludes = new ArrayList<Pattern>(32);

   private static boolean locationLogginOn = true;

   public static void setLocationLoggingOn(boolean on) {
      locationLogginOn = on;
   }

   public static boolean getLocationLoggingOn() {
      return locationLogginOn;
   }

   static {
      filterTheStacktrace = System.getProperty("org.eclipse.osee.ote.core.noStacktraceFilter") == null;
      stacktraceExcludes.add(Pattern.compile("org\\.eclipse\\.osee\\..*"));
   }

   private final ITestEnvironmentAccessor source;
   private long timeStamp;
   private final boolean printTimeStamp;
   private Throwable throwable;

   /**
    * TestRecord Constructor. This is an abstract class so this constructor is called via the super() call from the
    * extended class. This sets the source, the logging level, the log message and whether a timestamp should be
    * included.
    * 
    * @param source The object requesting the logging.
    * @param level The logging level.
    * @param msg The log message.
    * @param timeStamp <b>True </b> to include timestamp, <b>False </b> if not.
    */
   public TestRecord(ITestEnvironmentAccessor source, Level level, String msg, boolean timeStamp) {
      super(level, msg);
      this.throwable = new Throwable();
      this.printTimeStamp = timeStamp;
      this.source = source;
      if (this.printTimeStamp) {
         if (source != null) {
            this.timeStamp = source.getEnvTime();
         } else {
            this.timeStamp = System.currentTimeMillis();
            try {
               throw new Exception("source was null");
            } catch (Exception e) {
               OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
            }
         }
      }
   }

   public void setStackTrace(Throwable throwable) {
      this.throwable = throwable;
   }

   private Element calc(Document doc) {
      StackTraceElement[] stackElements = this.throwable.getStackTrace();
      Element locationElement = doc.createElement("Location");
      locationElement.setAttribute("id", Integer.toString(locationElement.hashCode()));
      for (StackTraceElement stackElement : stackElements) {
         addElement(doc, stackElement, locationElement);
      }
      return locationElement;
   }

   private void calc(XMLStreamWriter writer) throws XMLStreamException {
      StackTraceElement[] stackElements = this.throwable.getStackTrace();
      writer.writeStartElement("Location");
      writer.writeAttribute("id", Integer.toString(stackElements.hashCode()));
      for (StackTraceElement stackElement : stackElements) {
         addElement(writer, stackElement);
      }
   }

   private void addElement(XMLStreamWriter writer, StackTraceElement stackElement) throws XMLStreamException {
      if (filterTheStacktrace) {
         final String className = stackElement.getClassName();
         for (Pattern includes : stacktraceIncludes) {
            if (includes.matcher(className).matches()) {
               writer.writeEmptyElement("Stacktrace");
               writer.writeAttribute("source", stackElement.getClassName());
               writer.writeAttribute("line", Integer.toString(stackElement.getLineNumber()));
               //               writer.writeEndElement();
               return;
            }
         }
         for (Pattern excludes : stacktraceExcludes) {
            if (excludes.matcher(className).matches()) {
               return;
            }
         }
      }
      writer.writeEmptyElement("Stacktrace");
      writer.writeAttribute("source", stackElement.getClassName());
      writer.writeAttribute("line", Integer.toString(stackElement.getLineNumber()));
      //      writer.writeEndElement();
   }

   private void addElement(Document doc, StackTraceElement stackElement, Element locationElement) {
      if (filterTheStacktrace) {
         final String className = stackElement.getClassName();
         for (Pattern includes : stacktraceIncludes) {
            if (includes.matcher(className).matches()) {
               Element stackTrace = doc.createElement("Stacktrace");
               stackTrace.setAttribute("source", stackElement.getClassName());
               stackTrace.setAttribute("line", Integer.toString(stackElement.getLineNumber()));
               locationElement.appendChild(stackTrace);
               return;
            }
         }
         for (Pattern excludes : stacktraceExcludes) {
            if (excludes.matcher(className).matches()) {
               return;
            }
         }
      }
      Element stackTrace = doc.createElement("Stacktrace");
      stackTrace.setAttribute("source", stackElement.getClassName());
      stackTrace.setAttribute("line", Integer.toString(stackElement.getLineNumber()));
      locationElement.appendChild(stackTrace);
   }

   /**
    * Converts log element to XML format.
    * 
    * @return xml formated element.
    */
   @Override
   public Element toXml(Document doc) {
      Element recordElement = doc.createElement(getLevel().getName());
      if (TestRecord.getLocationLoggingOn()) {
         recordElement.appendChild(getLocation(doc));
      }
      recordElement.appendChild(Jaxp.createElement(doc, "Message", getMessage()));
      return recordElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement(getLevel().getName());
      writeLocationCheckLocationLoggingOn(writer);
      writeMessage(writer);
      writer.writeEndElement();
   }

   public Object getSource() {
      return source;
   }

   /**
    * @return Elements location.
    */
   protected Element getLocation(Document doc) {
      Element locationElement = calc(doc);
      if (this.printTimeStamp) {
         locationElement.appendChild(Jaxp.createElement(doc, "Time", Long.toString(timeStamp)));
      }
      return locationElement;
   }

   protected void writeLocation(XMLStreamWriter writer) throws XMLStreamException {
      calc(writer);
      writeTime(writer);
      writer.writeEndElement();
   }

   protected void writeLocationCheckLocationLoggingOn(XMLStreamWriter writer) throws XMLStreamException {
      if (TestRecord.getLocationLoggingOn()) {
    	  writeLocation(writer);
      }
   }
   
   protected void writeTime(XMLStreamWriter writer) throws XMLStreamException {
      if (this.printTimeStamp) {
         writer.writeStartElement("Time");
         writer.writeCharacters(Long.toString(timeStamp));
         writer.writeEndElement();
      }
   }

   protected void writeMessage(XMLStreamWriter writer) throws XMLStreamException {
      writeElement(writer, "Message", getMessage());
   }

   protected void writeElement(XMLStreamWriter writer, String elementName, String characterData) throws XMLStreamException {
      XMLStreamWriterUtil.writeElement(writer, elementName, characterData);
   }
}