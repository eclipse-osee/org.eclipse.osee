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
package org.eclipse.osee.ats.api.ev;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsEarnedValueService {

   public IAtsWorkPackage getWorkPackage(IAtsWorkItem workItem) ;

   public Collection<IAtsWorkPackage> getWorkPackageOptions(IAtsObject object) ;

   public ArtifactId getWorkPackageId(IAtsWorkItem atsObject);

   public void setWorkPackage(IAtsWorkPackage workPackage, Collection<IAtsWorkItem> workItems);

   public void removeWorkPackage(IAtsWorkPackage workPackage, Collection<IAtsWorkItem> workItems);

   public IAtsWorkPackage getWorkPackage(ArtifactToken artifact);

   public Collection<String> getColorTeams();

   public Collection<IAtsWorkPackage> getWorkPackages(IAtsInsertionActivity insertionActivity);

   double getEstimatedHoursFromArtifact(IAtsWorkItem workItem);

   double getEstimatedHoursFromTasks(IAtsWorkItem workItem, IStateToken relatedToState);

   double getEstimatedHoursFromTasks(IAtsWorkItem workItem);

   double getEstimatedHoursFromReviews(IAtsWorkItem workItem) ;

   double getEstimatedHoursFromReviews(IAtsWorkItem workItem, IStateToken relatedToState) ;

   double getEstimatedHoursTotal(IAtsWorkItem workItem, IStateToken relatedToState) ;

   double getEstimatedHoursTotal(IAtsWorkItem workItem) ;

   void setWorkPackage(IAtsWorkPackage workPackage, IAtsWorkItem workItem, IAtsChangeSet changes);

}
