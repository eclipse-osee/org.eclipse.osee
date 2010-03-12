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
package org.eclipse.osee.ote.core;

import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.util.EnumBase;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Charles Shaw
 * @author Robert A. Fisher
 */
public class MethodFormatter implements Xmlizable {
   private final ArrayList<String> variableClass;
   private final ArrayList<String> variableValue;

   public MethodFormatter() {
      variableClass = new ArrayList<String>();
      variableValue = new ArrayList<String>();
   }

   public MethodFormatter add(float value) {
      variableClass.add(float.class.getName());
      variableValue.add(Double.toString(value));
      return this;
   }

   public MethodFormatter add(double value) {
      variableClass.add(double.class.getName());
      variableValue.add(Double.toString(value));
      return this;
   }

   public MethodFormatter add(byte value) {
      variableClass.add(byte.class.getName());
      variableValue.add(Double.toString(value));
      return this;
   }

   public MethodFormatter add(short value) {
      variableClass.add(short.class.getName());
      variableValue.add(Double.toString(value));
      return this;
   }

   public MethodFormatter add(int value) {
      variableClass.add(int.class.getName());
      variableValue.add(Integer.toString(value));
      return this;
   }

   public MethodFormatter add(long value) {
      variableClass.add(long.class.getName());
      variableValue.add(Double.toString(value));
      return this;
   }

   public MethodFormatter add(char value) {
      variableClass.add(char.class.getName());
      variableValue.add(Integer.toString(value));
      return this;
   }

   public MethodFormatter add(boolean value) {
      variableClass.add(boolean.class.getName());
      variableValue.add(Boolean.toString(value));
      return this;
   }

   public MethodFormatter add(EnumBase value) {
      variableClass.add(EnumBase.class.getName());
      variableValue.add(value.getName());
      return this;
   }

   public MethodFormatter add(EnumBase[] value) {
      variableClass.add(EnumBase[].class.getName());
      String arrayValues = "[";
      for (int i = 0; i < value.length; i++) {
         arrayValues.concat(value[i].getName() + ", ");
      }
      arrayValues = "]";
      variableValue.add(arrayValues);
      return this;
   }

   public MethodFormatter add(Object value) {
      variableClass.add(value.getClass().getName());
      variableValue.add(value.toString());
      return this;
   }

   public String toString() {
      String argumentString = "";

      for (int i = 0; i < variableValue.size(); i++) {

         if (i != 0) argumentString += ", ";
         argumentString += "<" + variableClass.get(i) + ">" + variableValue.get(i);

      }
      return argumentString;
   }

   public Element toXml(Document doc) {
      Element toReturn = doc.createElement("MethodArguments");

      for (int i = 0; i < variableValue.size(); i++) {
         Element element = doc.createElement("Argument");
         element.appendChild(Jaxp.createElement(doc, "Type", variableClass.get(i).toString()));

         Object obj = variableValue.get(i);
         String toLog = (obj != null ? XmlSupport.format(obj.toString()) : "null");
         element.appendChild(Jaxp.createElement(doc, "Value", toLog));

         toReturn.appendChild(element);
      }
      return toReturn;
   }
}
