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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters;

import com.google.common.io.ByteSource;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.jaxrs.server.security.OAuthClient;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationClient extends Client implements OAuthClient {

   private static final long serialVersionUID = -5666467776236248089L;

   private final long clientUuid;
   private final long subjectId;
   private final String guid;
   private ByteSource logoSupplier;

   public ApplicationClient(long clientUuid, long subjectId, String guid) {
      super();
      this.clientUuid = clientUuid;
      this.subjectId = subjectId;
      this.guid = guid;
   }

   @Override
   public long getClientUuid() {
      return clientUuid;
   }

   @Override
   public long getSubjectId() {
      return subjectId;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (guid == null ? 0 : guid.hashCode());
      return result;
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean equals(Object obj) {
      boolean equal = false;
      if (obj instanceof Identity) {
         Identity<String> identity = (Identity<String>) obj;
         if (getGuid() == identity.getGuid()) {
            equal = true;
         } else if (getGuid() != null) {
            equal = getGuid().equals(identity.getGuid());
         }
      }
      return equal;
   }

   @Override
   public boolean hasApplicationLogoSupplier() {
      return logoSupplier != null;
   }

   @Override
   public ByteSource getApplicationLogoSupplier() {
      return logoSupplier;
   }

   public void setApplicationLogoSupplier(ByteSource logoSupplier) {
      this.logoSupplier = logoSupplier;
   }

}
