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
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 */
public class TraceRecord extends TestRecord implements Xmlizable {
   /**
    * 
    */
   private static final long serialVersionUID = 8567378567805515775L;
   private String objectName;
   private String methodName;
   private MethodFormatter methodArguments;
   private boolean startFlag;
   private ArrayList<Xmlizable> additionalElements;

   private static final String additionalString = "AdditionalInfo";

   /**
    * TraceRecord Constructor. Sets up a Trace log message.
    * 
    * @param source The object requesting the logging.
    * @param objectName
    * @param methodName
    * @param methodArguments
    * @param timeStamp <b>True </b> if a timestamp should be recorded, <b>False </b> if not.
    * @param startFlag
    */
   public TraceRecord(ITestEnvironmentAccessor source, String objectName, String methodName,
         MethodFormatter methodArguments, boolean timeStamp, boolean startFlag) {
      super(source, TestLevel.TRACE, "", timeStamp);
      this.objectName = objectName;
      this.methodName = methodName;
      this.methodArguments = methodArguments;
      this.startFlag = startFlag;
      this.additionalElements = new ArrayList<Xmlizable>();
   }

   /**
    * TraceRecord Constructor. Sets up a Trace log message.
    * 
    * @param source The object requesting the logging.
    * @param objectName
    * @param methodName
    * @param methodArguments
    * @param startFlag
    */
   public TraceRecord(ITestEnvironmentAccessor source, String objectName, String methodName,
         MethodFormatter methodArguments, boolean startFlag) {
      this(source, objectName, methodName, methodArguments, true, startFlag);
   }

   /**
    * Get the start flag.
    * 
    * @return <b>true</b> if its the beginning of a method, or <b>false</b> if its the end of a
    *         method.
    */
   public boolean getStartFlag() {
      return startFlag;
   }

   public void addAdditionalElement(Xmlizable object) {
      if (object != null)
         additionalElements.add(object);
   }

   /**
    * Converts element to XML formating.
    * 
    * @return Element XML formated element.
    */
   public Element toXml(Document doc) {
      Element trElement = doc.createElement("Trace");
      trElement.appendChild(Jaxp.createElement(doc, "ObjectName", objectName));
      trElement.appendChild(Jaxp.createElement(doc, "MethodName", methodName));
      trElement.appendChild(methodArguments.toXml(doc));
      if (!additionalElements.isEmpty()) {
         Element additional = doc.createElement(additionalString);
         trElement.appendChild(additional);
         for (Xmlizable object : additionalElements) {
            additional.appendChild(object.toXml(doc));
         }
      }
      trElement.appendChild(getLocation(doc));
      return trElement;
   }
}