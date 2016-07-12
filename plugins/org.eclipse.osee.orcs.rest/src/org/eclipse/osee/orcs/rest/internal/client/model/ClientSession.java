/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.client.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class ClientSession {

   private final IVariantData variantData;

   public ClientSession(IVariantData variantData) {
      this.variantData = variantData;
   }

   public String getClientAddress() {
      return variantData.get("CLIENT_ADDRESS");
   }

   public String getClientPort() {
      return variantData.get("CLIENT_PORT");
   }

   public String getUserId() {
      return variantData.get("USER_ID");
   }

   public String getClientVersion() {
      return variantData.get("CLIENT_VERSION");
   }

   public String getSessionId() {
      return variantData.get("SESSION_ID");
   }

   public String getCreatedOn() {
      return DateUtil.get(variantData.getDate("CREATED_ON"), DateUtil.MMDDYYHHMM);
   }

}
