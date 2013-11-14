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
package org.eclipse.osee.ats.rest.internal.workitem;

import java.rmi.activation.Activator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.rest.internal.AtsServerImpl;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public abstract class AtsConfigObject extends org.eclipse.osee.ats.core.model.impl.AtsObject implements IAtsConfigObject {
   protected final ArtifactReadable artifact;

   public AtsConfigObject(ArtifactReadable artifact) {
      super(artifact.getGuid(), artifact.getName());
      this.artifact = artifact;
   }

   public void setFullName(String fullName) {
      OseeLog.log(TeamDefinition.class, Level.SEVERE, "TeamDefinition.setFullName not implemented");
   }

   public abstract String getTypeName();

   public String getFullName() {
      return getTypeName();
   }

   public void setActionable(boolean actionable) {
      OseeLog.log(TeamDefinition.class, Level.SEVERE, "TeamDefinition.setActionable not implemented");
   }

   public boolean isActionable() {
      return getAttributeValue(AtsAttributeTypes.Actionable, false);
   }

   @SuppressWarnings("unchecked")
   protected <T> T getAttributeValue(IAttributeType attributeType, Object defaultValue) {
      T value = null;
      try {
         value = (T) artifact.getSoleAttributeValue(attributeType, defaultValue);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return value;
   }

   public void setActive(boolean active) {
      OseeLog.log(TeamDefinition.class, Level.SEVERE, "TeamDefinition.setActive not implemented");
   }

   public boolean isActive() {
      return getAttributeValue(AtsAttributeTypes.Active, false);
   }

   public Collection<String> getStaticIds() {
      Collection<String> results = Collections.emptyList();
      try {
         results = artifact.getAttributeValues(CoreAttributeTypes.StaticId);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return results;
   }

   public Collection<IAtsUser> getLeads() {
      return getRelatedUsers(AtsRelationTypes.TeamLead_Lead);
   }

   public Collection<IAtsUser> getSubscribed() {
      return getRelatedUsers(AtsRelationTypes.SubscribedUser_User);
   }

   Collection<IAtsUser> getRelatedUsers(IRelationTypeSide relation) {
      Set<IAtsUser> results = new HashSet<IAtsUser>();
      try {
         for (ArtifactReadable userArt : artifact.getRelated(relation)) {
            IAtsUser lead = AtsServerImpl.get().getUserService().getUserById(userArt.getGuid());
            results.add(lead);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return results;
   }

   @Override
   public Object getStoreObject() {
      return artifact;
   }

}
