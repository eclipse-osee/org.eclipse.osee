/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.util;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Return or set the configurable ATS Id based on Team Definition attributes. First check related team definition, then
 * check team definition holding versions (if any) and last use the default configured sequence and prefix.
 *
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public final class AtsIdProvider {

   protected static final String DEFAULT_ACTION_SEQ_NAME = "ATS_ACTION_ID_SEQ";
   protected static final String DEFAULT_ACTION_ID_PREFIX = "ACT";

   protected static final String DEFAULT_TASK_SEQ_NAME = "ATS_TASK_ID_SEQ";
   protected static final String DEFAULT_TASK_ID_PREFIX = "TSK";

   protected static final String DEFAULT_REVIEW_SEQ_NAME = "ATS_REVIEW_ID_SEQ";
   protected static final String DEFAULT_REVIEW_ID_PREFIX = "RVW";

   protected static final String DEFAULT_WORKFLOW_SEQ_NAME = "ATS_WORKFLOW_ID_SEQ";
   protected static final String DEFAULT_WORKFLOW_ID_PREFIX = "TW";

   protected static final String DEFAULT_SEQ_NAME = "ATS_ID_SEQ";
   protected static final String DEFAULT_ID_PREFIX = "ATS";
   private final IAtsObject newObject;
   private final IAtsTeamDefinition teamDef;
   private final IAttributeResolver attrResolver;
   private final ISequenceProvider sequenceProvider;

   public AtsIdProvider(ISequenceProvider sequenceProvider, IAttributeResolver attrResolver, IAtsObject newObject, IAtsTeamDefinition teamDef) {
      this.sequenceProvider = sequenceProvider;
      this.attrResolver = attrResolver;
      this.newObject = newObject;
      this.teamDef = teamDef;
   }

   public String getNextAtsId() {
      String seqName = getAttrValueFromTeamDef(AtsAttributeTypes.AtsIdSequenceName);
      if (!Strings.isValid(seqName)) {
         if (newObject.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            seqName = DEFAULT_WORKFLOW_SEQ_NAME;
         } else if (newObject.isOfType(AtsArtifactTypes.Action)) {
            seqName = DEFAULT_ACTION_SEQ_NAME;
         } else if (newObject.isOfType(AtsArtifactTypes.ReviewArtifact)) {
            seqName = DEFAULT_REVIEW_SEQ_NAME;
         } else if (newObject.isOfType(AtsArtifactTypes.Task)) {
            seqName = DEFAULT_TASK_SEQ_NAME;
         } else {
            seqName = DEFAULT_SEQ_NAME;
         }
      }
      String prefixName = getAttrValueFromTeamDef(AtsAttributeTypes.AtsIdPrefix);
      if (!Strings.isValid(prefixName)) {
         if (newObject.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            prefixName = DEFAULT_WORKFLOW_ID_PREFIX;
         } else if (newObject.isOfType(AtsArtifactTypes.Action)) {
            prefixName = DEFAULT_ACTION_ID_PREFIX;
         } else if (newObject.isOfType(AtsArtifactTypes.ReviewArtifact)) {
            prefixName = DEFAULT_REVIEW_ID_PREFIX;
         } else if (newObject.isOfType(AtsArtifactTypes.Task)) {
            prefixName = DEFAULT_TASK_ID_PREFIX;
         } else {
            prefixName = DEFAULT_ID_PREFIX;
         }
      }
      return getNextId(prefixName, seqName);
   }

   public void setAtsId(IAtsChangeSet changes) {
      String atsId = attrResolver.getSoleAttributeValueAsString(newObject, AtsAttributeTypes.AtsId, null);
      if (!Strings.isValid(atsId) || atsId.equals("0")) {
         String id = getNextAtsId();
         attrResolver.setSoleAttributeValue(newObject, AtsAttributeTypes.AtsId, id, changes);
      }
   }

   protected String getNextId(String prefix, String seqName)  {
      return String.format("%s%d", prefix, sequenceProvider.getNext(seqName));
   }

   protected String getAttrValueFromTeamDef(AttributeTypeId attrType) {
      String attrValue = attrResolver.getSoleAttributeValueAsString(teamDef, attrType, (String) null);
      if (!Strings.isValid(attrValue)) {
         IAtsTeamDefinition parentTeamDef = teamDef.getTeamDefinitionHoldingVersions();
         if (parentTeamDef != null) {
            attrValue = attrResolver.getSoleAttributeValueAsString(parentTeamDef, attrType, (String) null);
         }
      }
      return attrValue;
   }

}
