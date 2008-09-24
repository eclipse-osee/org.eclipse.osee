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
package org.eclipse.osee.framework.messaging.event.skynet.event;

/**
 * @author Jeff C. Phillips
 */
public class SkynetAttributeChange implements SkynetChange {
   private static final long serialVersionUID = 7269483275150734396L;
   private final String name;
   private final boolean deleted;
   private final int attributeId;
   private final int gammaId;
   private final Object[] value;

   /**
    * @return the deleted
    */
   public boolean isDeleted() {
      return deleted;
   }

   /**
    * @param name
    * @param value
    */
   public SkynetAttributeChange(String name, Object[] value, boolean deleted, int attributeId, int gammaId) {
      super();
      this.name = name;
      this.value = value;
      this.deleted = deleted;
      this.attributeId = attributeId;
      this.gammaId = gammaId;
   }

   @Override
   public String toString() {
      return name + "(" + attributeId + ")" + " => " + value;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @return Returns the value.
    */
   public Object[] getData() {
      return value;
   }

   public int getAttributeId() {
      return attributeId;
   }

   /**
    * @return Returns the gammaId.
    */
   public int getGammaId() {
      return gammaId;
   }
}
