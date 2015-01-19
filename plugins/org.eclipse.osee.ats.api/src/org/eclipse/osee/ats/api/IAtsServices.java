/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse  License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api;

import java.util.Collection;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsServices {

   IRelationResolver getRelationResolver();

   IAttributeResolver getAttributeResolver();

   IAtsUserService getUserService();

   IAtsWorkItemService getWorkItemService();

   IAtsReviewService getReviewService();

   IAtsBranchService getBranchService();

   IAtsWorkDefinitionService getWorkDefService();

   IAtsVersionService getVersionService();

   Object getArtifact(Object object) throws OseeCoreException;

   void setChangeType(IAtsObject atsObject, ChangeType changeType, IAtsChangeSet changes);

   ChangeType getChangeType(IAtsAction fromAction);

   String getAtsId(IAtsAction action);

   Collection<IArtifactType> getArtifactTypes();

   IAtsWorkItemFactory getWorkItemFactory();

   Object getArtifactById(String id);

   IAtsConfigItemFactory getConfigItemFactory();

}
