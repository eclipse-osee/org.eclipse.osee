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

package org.eclipse.osee.ats.ide.workdef.config.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G Dunne
 */
public abstract class AtsConfigObject extends AtsObject implements IAtsConfigObject {
   protected final Artifact artifact;
   private final AtsApiIde atsClient;

   public AtsConfigObject(AtsApiIde atsClient, Artifact artifact) {
      super(artifact.getName(), artifact.getId());
      this.atsClient = atsClient;
      this.artifact = artifact;
      setStoreObject(artifact);
   }

   public void setFullName(String fullName) {
      artifact.setName(fullName);
   }

   public abstract String getTypeName();

   public String getFullName() {
      return getTypeName();
   }

   public void setActionable(boolean actionable) {
      artifact.setSoleAttributeValue(AtsAttributeTypes.Actionable, actionable);
   }

   public boolean isActionable() {
      return getAttributeValue(AtsAttributeTypes.Actionable, false);
   }

   @SuppressWarnings("unchecked")
   protected <T> T getAttributeValue(AttributeTypeId attributeType, Object defaultValue) {
      T value = null;
      try {
         value = (T) artifact.getSoleAttributeValue(attributeType, defaultValue);
      } catch (OseeCoreException ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error getting attribute value for - attributeType[%s]",
            attributeType);
      }
      return value;
   }

   public void setActive(boolean active) {
      artifact.setSoleAttributeValue(AtsAttributeTypes.Actionable, active);
   }

   @Override
   public boolean isActive() {
      return getAttributeValue(AtsAttributeTypes.Active, false);
   }

   public Collection<AtsUser> getLeads() {
      return getRelatedUsers(AtsRelationTypes.TeamLead_Lead);
   }

   public Collection<AtsUser> getSubscribed() {
      return getRelatedUsers(AtsRelationTypes.SubscribedUser_User);
   }

   protected Collection<AtsUser> getRelatedUsers(RelationTypeSide relation) {
      Set<AtsUser> results = new HashSet<>();
      try {
         for (Artifact userArt : artifact.getRelatedArtifacts(relation)) {
            AtsUser lead = getAtsClient().getUserService().getUserByUserId(
               (String) userArt.getSoleAttributeValue(CoreAttributeTypes.UserId));
            results.add(lead);
         }
      } catch (OseeCoreException ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error getting related Users for relationTypeSide[%s]",
            relation);
      }
      return results;
   }

   protected AtsApiIde getAtsClient() {
      return atsClient;
   }

   @Override
   public ArtifactToken getStoreObject() {
      return artifact != null ? artifact : super.getStoreObject();
   }

   @Override
   public Long getId() {
      return artifact.getId();
   }
}