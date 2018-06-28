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

import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Michael A. Winston
 */
public class RuleRecord extends LogRecord {

   private static final long serialVersionUID = 6974861818239720347L;

   /**
    * RuleRecord Constructor. This is an abstract class so this constructor is called via the super() call from the
    * extended class. This sets the source, the logging level, the log message and whether a timestamp should be
    * included.
    *
    * @param level The logging level.
    * @param msg The log message.
    */
   public RuleRecord(Level level, String msg) {
      super(level, msg);
   }

   /**
    * Converts log element to XML format.
    *
    * @return xml formated element.
    */
   public Element toXml(Document doc) {
      Element recordElement = Jaxp.createElement(doc, getLevel().getName(), getMessage());
      return recordElement;
   }
}