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
package org.eclipse.osee.orcs.account.admin.internal;

import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class AccountSubscriptionGroupImpl extends BaseIdentity<String> implements SubscriptionGroup {

   private static final String NOT_AVAILABLE = "N/A";
   private final ArtifactReadable artifact;
   private final SubscriptionGroupId id;

   public AccountSubscriptionGroupImpl(ArtifactReadable artifact) {
      super(artifact.getGuid());
      this.artifact = artifact;
      this.id = new SubscriptionGroupId(artifact.getUuid());
   }

   @Override
   public SubscriptionGroupId getId() {
      return id;
   }

   @Override
   public String getName() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.Name, NOT_AVAILABLE);
   }

   @Override
   public String toString() {
      return "AccountSubscriptionGroupImpl [artifact=" + artifact + "]";
   }

}
