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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.test.tags.BaseTestTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class BaseTestRecord extends LogRecord implements BaseTestTags, Xmlizable {
   private Object source;
   private long timeStamp;
   private boolean printTimeStamp;

   /**
    * TestRecord Constructor. This is an abstract class so this constructor is called via the super() call from the
    * extended class. This sets the source, the logging level, the log message and whether a timestamp should be
    * included.
    * 
    * @param source The object requesting the logging.
    * @param level The logging level.
    * @param msg The log message.
    */
   public BaseTestRecord(ITestEnvironmentAccessor source, Level level, String msg, boolean timeStamp) {
      super(level, msg);

      this.printTimeStamp = timeStamp;

      if (timeStamp) {
         if (source != null) {
            if (source instanceof TestEnvironment) {
               TestEnvironment env = (TestEnvironment) source;
               this.timeStamp = System.currentTimeMillis() - (env.getTestScript().getStartTime().getTime());
            }
         } else {
            this.timeStamp = (new Date()).getTime();
            System.out.println("source was null?");
            try {
               throw new Exception("source was null");
            } catch (Exception e) {
               OseeLog.log(TestEnvironment.class,
                     Level.SEVERE, e.getMessage(), e);
            }
         }
      }
   }

   /**
    * Converts log element to XML format.
    * 
    * @return xml formated element.
    */
   public Element toXml(Document doc) {
      Element recordElement = doc.createElement(getLevel().getName());

      if (this.printTimeStamp) {
         Element timeElement = doc.createElement(BaseTestTags.TIME_FIELD);
         timeElement.setTextContent(Long.toString(this.timeStamp));

         recordElement.appendChild(timeElement);
      }
      Element msgElement = doc.createElement(BaseTestTags.MESSAGE_FIELD);
      msgElement.setTextContent(getMessage());

      recordElement.appendChild(msgElement);
      additionalXml(recordElement);
      return recordElement;
   }

   /**
    * This method is to be overriden by subclasses of TestRecord that wish to log additional information to the XML
    * destination.
    * 
    * @param baseElement This is the element that is being submitted by TestRecord for toXml().
    */
   protected void additionalXml(//@SuppressWarnings("unused") 
   Element baseElement) {
      // This is intended to be overridden by subclasses that
      // want to supply additional XML information.
   }

   public Object getSource() {
      return source;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public boolean isTimeStampAvailable() {
      return this.printTimeStamp;
   }

}