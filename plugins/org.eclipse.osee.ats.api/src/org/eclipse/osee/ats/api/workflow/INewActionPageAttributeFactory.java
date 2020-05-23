/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import java.util.Date;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;

/**
 * @author Jeremy A. Midvidy
 */
public interface INewActionPageAttributeFactory {

   public void setArtifactIdentifyData(IAttributeResolver attrResolver, IAtsAction fromAction, IAtsTeamWorkflow toTeam, IAtsChangeSet changes);

   public void setArtifactIdentifyData(IAttributeResolver attrResolver, IAtsObject atsObject, String title, String desc, ChangeType changeType, String priority, Boolean validationRequired, Date needByDate, IAtsChangeSet changes);

   public boolean useFactory();

   public AtsAttributeValueColumn getPrioirtyColumnToken();

   public AttributeTypeEnum<?> getPrioirtyAttrToken();

   public ChangeType[] getChangeTypeValues();

}
