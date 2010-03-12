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
package org.eclipse.osee.ote.message.enums;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.print.attribute.EnumSyntax;

import org.eclipse.osee.framework.jdk.core.util.EnumBase;


/**
 * @author Michael P. Masterson
 */
public class EmptyEnum extends EnumBase implements Comparable<EmptyEnum>{
   
   /**
    * 
    */
   private static final long serialVersionUID = 1305742348409814145L;
   private static final HashMap<Integer, EmptyEnum> valuesMap = new HashMap<Integer, EmptyEnum>(16);
   
   private final int value;
   private EmptyEnum(int value) {
      super(value);
      this.value = value;
   }
   
// public static EmptyEnum getEnum(String str) {
// return (EmptyEnum) getEnum(str, stringTable, enumValueTable);
// }
   
   public static EmptyEnum toEnum(int value) {
      EmptyEnum newValue = valuesMap.get(value);
      if( newValue == null )
      {
         newValue = new EmptyEnum(value);
         valuesMap.put(value, newValue);
      }
      return newValue;
   }
   
//   private static void printTable()
//   {
//      for( int value : valuesMap.keySet())
//      {
//         System.out.println(value + " -> " + valuesMap.get(value));
//      }
//   }
   public static EmptyEnum toEnum(EnumBase otherEnum) {
      return toEnum(otherEnum.getValue());
   }
   
   @Override
   protected String[] getStringTable() {
      List<String> retVal = new LinkedList<String>();
      for( EmptyEnum current : valuesMap.values())
      {
         retVal.add("EMPTY_ENUM_" + current.getValue());
      }
      return retVal.toArray(new String[16]);
   }
   
   @Override
   protected EnumSyntax[] getEnumValueTable() {
      EnumSyntax[] retVal = new EnumSyntax[0];
      return valuesMap.values().toArray(retVal);
   }
   
   public String getName()
   {
      return "EMPTY_ENUM_" + getValue();
   }
   
   public String toString()
   {
      return getName();
   }
   
   
   public int getValue()
   {
      return value;
   }

public int compareTo(EmptyEnum o) {
    return value - o.value;
}
   
   
}
