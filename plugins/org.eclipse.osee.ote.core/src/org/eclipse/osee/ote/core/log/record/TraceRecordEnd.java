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
import org.eclipse.osee.ote.core.ReturnFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TraceRecordEnd extends TestRecord implements Xmlizable {

   private static final long serialVersionUID = 8567378567805515775L;
   private final ReturnFormatter returnFormatter;
   private final ArrayList<Xmlizable> additionalElements;

   private static final String additionalString = "AdditionalInfo";

   public TraceRecordEnd(ITestEnvironmentAccessor source, ReturnFormatter returnFormatter) {
      super(source, TestLevel.TRACE, "", true);
      this.returnFormatter = returnFormatter;
      this.additionalElements = new ArrayList<Xmlizable>();
   }

   public void addAdditionalElement(Xmlizable object) {
      if (object != null) {
         additionalElements.add(object);
      }
   }

   /**
    * Converts element to XML formating.
    * 
    * @return Element XML formated element.
    */
   @Override
   public Element toXml(Document doc) {
      Element trElement = doc.createElement("TraceEnd");
      trElement.appendChild(returnFormatter.toXml(doc));
      if (!additionalElements.isEmpty()) {
         Element additional = doc.createElement(additionalString);
         trElement.appendChild(additional);
         for (Xmlizable object : additionalElements) {
            additional.appendChild(object.toXml(doc));
         }
      }
      return trElement;
   }
}