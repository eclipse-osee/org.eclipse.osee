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
package org.eclipse.osee.ote.messaging.dds.status;

/**
 * Enumeration class for the reasons a sample was rejected by a {@link org.eclipse.osee.ote.messaging.dds.entity.DataReader}.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class SampleRejectedStatusKind {
   public static final SampleRejectedStatusKind REJECTED_BY_INSTANCE_LIMIT = new SampleRejectedStatusKind(1, "Rejected by instance limit");
   public static final SampleRejectedStatusKind REJECTED_BY_TOPIC_LIMIT = new SampleRejectedStatusKind(2, "Rejected by topic limit");

   private int value;
   private String description;

   private SampleRejectedStatusKind(int value, String description) {
      super();
      this.value = value;
      this.description = description;
   }

   /**
    * Gets the description of the rejected status.
    * 
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Gets the value used to identify the rejected status.
    * 
    * @return Returns the value.
    */
   public int getValue() {
      return value;
   }
}
