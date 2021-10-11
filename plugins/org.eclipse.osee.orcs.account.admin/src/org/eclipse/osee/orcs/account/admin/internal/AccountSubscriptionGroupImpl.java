/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.account.admin.internal;

import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Roberto E. Escobar
 */
public class AccountSubscriptionGroupImpl extends NamedIdBase implements SubscriptionGroup {

   private static final String NOT_AVAILABLE = "N/A";
   private final ArtifactReadable artifact;
   private final SubscriptionGroupId id;

   public AccountSubscriptionGroupImpl(ArtifactReadable artifact) {
      super(artifact.getId(), artifact.getSoleAttributeValue(CoreAttributeTypes.Name, NOT_AVAILABLE));
      this.artifact = artifact;
      this.id = new SubscriptionGroupId(artifact.getId());
   }

   @Override
   public SubscriptionGroupId getGroupId() {
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

   @Override
   public String getGuid() {
      return artifact.getGuid();
   }
}