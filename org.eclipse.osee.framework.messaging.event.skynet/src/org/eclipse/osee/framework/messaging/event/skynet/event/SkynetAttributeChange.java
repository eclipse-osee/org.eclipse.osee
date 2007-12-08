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

   /**
    * 
    */
   private static final long serialVersionUID = 7269483275150734396L;
   private final String name;
   private final int attributeId;
   private final int gammaId;
   private final String value;

   /**
    * @param name
    * @param value
    */
   public SkynetAttributeChange(String name, String value, int attributeId, int gammaId) {
      super();
      this.name = name;
      this.value = value;
      this.attributeId = attributeId;
      this.gammaId = gammaId;
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
   public String getValue() {
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
