/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.account.admin.internal;

import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class AccountArtifact extends BaseId implements Account, ArtifactId {

   private static final String NOT_AVAILABLE = "N/A";

   private final ArtifactReadable artifact;
   private final AccountPreferences preferences;
   private final AccountWebPreferences webPreferences;

   public AccountArtifact(ArtifactReadable artifact, AccountPreferences preferences, AccountWebPreferences webPreferences) {
      super(artifact.getId());
      this.artifact = artifact;
      this.preferences = preferences;
      this.webPreferences = webPreferences;
   }

   @Override
   public boolean isActive() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.Active, false);
   }

   @Override
   public String getName() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.Name, NOT_AVAILABLE);
   }

   @Override
   public String getUserName() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.UserId, NOT_AVAILABLE);
   }

   @Override
   public String getEmail() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.Email, NOT_AVAILABLE);
   }

   @Override
   public AccountWebPreferences getWebPreferences() {
      return webPreferences;
   }

   @Override
   public AccountPreferences getPreferences() {
      return preferences;
   }

   @Override
   public String toString() {
      return "AccountArtifact [artifact=" + artifact + ", preferences=" + preferences + "]";
   }
}