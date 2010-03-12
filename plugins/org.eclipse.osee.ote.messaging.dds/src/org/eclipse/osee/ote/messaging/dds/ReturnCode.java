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
package org.eclipse.osee.ote.messaging.dds;

/**
 * Provides function status for many of the DDS specification routines. These are used
 * to communicate the result of function calls, and the usage of them is specified in the
 * DDS specification.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class ReturnCode {
   public static final ReturnCode OK = new ReturnCode(1, "Successful return");
   public static final ReturnCode ERROR = new ReturnCode(2, "Generic unspecified error");
   public static final ReturnCode BAD_PARAMETER = new ReturnCode(3, "Illegal parameter value");
   public static final ReturnCode UNSUPPORTED = new ReturnCode(4, "Unsupported operation");
   public static final ReturnCode ALREADY_DELETED = new ReturnCode(5, "The object target of this operation has already been deleted");
   public static final ReturnCode OUT_OF_RESOURCES = new ReturnCode(6, "Service ran out of resources needed to complete the operation");
   public static final ReturnCode NOT_ENABLED = new ReturnCode(7, "Operation invoked on an entity not yet enabled");
   public static final ReturnCode IMMUTABLE_POLICY = new ReturnCode(8, "Application attempted to modify an immutable QoS policy");
   public static final ReturnCode INCONSISTENT_POLICY = new ReturnCode(9, "Application specified a set of polices that are not consistent with each other");
   public static final ReturnCode PRECONDITION_NOT_MET = new ReturnCode(10, "A pre-condition for the operation was not met");
   public static final ReturnCode TIMEOUT = new ReturnCode(11, "The operation timed out");
   public static final ReturnCode NO_DATA = new ReturnCode(12, "Indicates a transient situation where the operation did not return any data but there is no inherent error");

   private int value;
   private String description;

   /**
    * Creates a new <code>ReturnCode</code> with a given value and description.
    * 
    * @param value
    * @param description
    */
   private ReturnCode(int value, String description) {
      super();
      this.value = value;
      this.description = description;
   }

   /**
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return Returns the value.
    */
   public int getValue() {
      return value;
   }
}
