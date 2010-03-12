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

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 */
public class ReturnStatus implements Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = -7774073812320127561L;

   
   private boolean status;
   private String message;
   
   public ReturnStatus(String message, boolean status){
      this.status = status;
      this.message = message;
   }
   
   public boolean getStatus(){
      return status;
   }
   
   public String getMessage(){
      return message;
   }
}
