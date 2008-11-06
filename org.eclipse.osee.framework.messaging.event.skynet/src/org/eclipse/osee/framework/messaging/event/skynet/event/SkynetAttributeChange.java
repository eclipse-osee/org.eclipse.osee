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
   private final int typeId;
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
    * @param typeId
    * @param value
    */
   public SkynetAttributeChange(int typeId, Object[] value, boolean deleted, int attributeId, int gammaId) {
      super();
      this.typeId = typeId;
      this.value = value;
      this.deleted = deleted;
      this.attributeId = attributeId;
      this.gammaId = gammaId;
   }

   @Override
   public String toString() {
      return typeId + "(" + attributeId + ")" + " => " + value;
   }

   /**
    * @return Returns the typeId.
    */
   public int getTypeId() {
      return typeId;
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
