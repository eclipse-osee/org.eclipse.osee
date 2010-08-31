/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew M. Finkbeiner
 */
public class ElementPath {
   private static final String UNKNOWN = "UNKNOWN";
   private final List<Object> elementPath;
   private String asString = "";
   private String messageName = UNKNOWN;
   private String elementName = UNKNOWN;
   private boolean modified;
   private boolean isHeader = false;

   public ElementPath(Object... objs) {
      this.elementPath = new ArrayList<Object>();
      if (objs.length == 1 && objs[0] instanceof String && ((String) objs[0]).contains("+")) {//then we have an asString to parse
         String[] items = ((String) objs[0]).split("\\+");
         for (String item : items) {
            try {
               int index = Integer.parseInt(item);
               elementPath.add(index);
            } catch (Exception ex) {
               elementPath.add(item);
            }
         }
      } else {
         add(objs);
      }
      modified = true;
      recomputeStrings();
   }

   public ElementPath(boolean isHeader, List<Object> elementPath) {
      this.elementPath = elementPath;
      modified = true;
      this.isHeader = isHeader;
      recomputeStrings();
   }

   public ElementPath(List<Object> elementPath) {
      this(false, elementPath);
   }

   public ElementPath() {
      this.elementPath = new ArrayList<Object>();
      modified = true;
      recomputeStrings();
   }

   private void recomputeStrings() {
      if (modified) {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < elementPath.size(); i++) {
            sb.append(elementPath.get(i).toString());
            if (i + 1 < elementPath.size()) {
               sb.append("+");
            }
         }
         asString = sb.toString();

         if (elementPath.size() > 0) {
            Object obj = elementPath.get(0);
            if (obj instanceof String) {
               messageName = (String) obj;
            }
         }

         if (elementPath.size() > 1) {
            Object obj = elementPath.get(elementPath.size() - 1);
            if (obj instanceof Integer) {
               elementName = (String) elementPath.get(elementPath.size() - 2) + "[" + ((Integer) obj).intValue() + "]";
            } else if (obj instanceof String) {
               elementName = (String) obj;
            }
         }

         modified = false;
      }
   }

   public void add(Object... objs) {
      modified = true;
      for (Object obj : objs) {
         elementPath.add(obj);
      }
   }

   public List<Object> getElementPath() {
      recomputeStrings();
      return elementPath;
   }

   public String asString() {
      recomputeStrings();
      return asString;
   }

   public Object get(int i) {
      recomputeStrings();
      return elementPath.get(i);
   }

   public int size() {
      return elementPath.size();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ElementPath) {
         return asString().equals(((ElementPath) obj).asString());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      recomputeStrings();
      return asString().hashCode();
   }

   public ElementPath subElementPath(int range) {
      recomputeStrings();
      ElementPath newElementPath = new ElementPath();
      for (int i = 0; i <= range; i++) {
         newElementPath.add(elementPath.get(i));
      }
      newElementPath.recomputeStrings();
      return newElementPath;
   }

   public String getMessageName() {
      recomputeStrings();
      return messageName;
   }

   public String getElementName() {
      recomputeStrings();
      return elementName;
   }

   public boolean isValidElement() {
      return elementName != UNKNOWN;
   }

   public boolean isHeader() {
      return isHeader;
   }
}