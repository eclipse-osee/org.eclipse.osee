/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.user;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxAtsUser extends JaxAtsObject implements IAtsUser {

   private String userId;
   private String email;

   @Override
   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   @Override
   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   @Override
   public String toStringWithId() {
      return String.format("[%s]-[%d]", getName(), getUserId());
   }

   @JsonIgnore
   @Override
   public ArtifactId getStoreObject() {
      return null;
   }

   @Override
   public void setStoreObject(ArtifactId artifact) {
      // do nothing
   }

   @Override
   public int compareTo(Object o) {
      return 0;
   }

}
