/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.internal;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class ReleaseTypeStatus {

   private String type;
   private int numberClients = 0;

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public int getNumberClients() {
      return numberClients;
   }

   public void setNumberClients(int numberClients) {
      this.numberClients = numberClients;
   }

   public void incClient() {
      numberClients++;
   }

}
