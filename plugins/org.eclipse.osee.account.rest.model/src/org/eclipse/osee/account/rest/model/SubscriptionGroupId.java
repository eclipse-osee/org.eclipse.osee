/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.rest.model;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;

/**
 * @author Angel Avila
 */
public class SubscriptionGroupId extends NamedIdentity<String> implements ArtifactId {

   private final Long uuid;

   public SubscriptionGroupId(Long uuid) {
      super(null, String.valueOf(uuid));
      this.uuid = uuid;
   }

   @Override
   public Long getUuid() {
      return uuid;
   }

   @Override
   public boolean equals(Object other) {
      boolean toReturn = false;
      if (other instanceof SubscriptionGroupId) {
         toReturn = this.getUuid().equals(((SubscriptionGroupId) other).getUuid());
      }
      return toReturn;
   }

   @Override
   public int hashCode() {
      return getUuid().hashCode();
   }

   @Override
   public int compareTo(Named arg0) {
      return getName().compareTo(arg0.getName());
   }

   @Override
   public String toStringWithId() {
      return String.format("[%s][%s]", getName(), getUuid());
   }
}
