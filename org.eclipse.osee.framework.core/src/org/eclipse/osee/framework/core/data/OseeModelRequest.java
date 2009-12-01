/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelRequest {

   public enum RequestType {
      IMPORT_TYPES;
   }

   private final RequestType requestType;
   private final String model;
   private final boolean isPersistAllowed;

   public OseeModelRequest(RequestType requestType, String model, boolean isPersistAllowed) {
      this.requestType = requestType;
      this.model = model;
      this.isPersistAllowed = isPersistAllowed;
   }

   public String getModel() {
      return model;
   }

   public boolean isPersistAllowed() {
      return isPersistAllowed;
   }

   public RequestType getRequestType() {
      return requestType;
   }
}
