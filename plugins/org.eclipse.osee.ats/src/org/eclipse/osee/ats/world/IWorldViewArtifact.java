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
package org.eclipse.osee.ats.world;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.swt.graphics.Image;

public interface IWorldViewArtifact {

   Image getAssigneeImage() throws OseeCoreException;

   String getAssigneeStr() throws OseeCoreException;

   /**
    * @return estimated hours from workflow attribute, tasks and reviews
    */
   double getWorldViewHoursSpentState() throws OseeCoreException;

   double getWorldViewHoursSpentStateReview() throws OseeCoreException;

   double getWorldViewHoursSpentStateTask() throws OseeCoreException;

   double getWorldViewHoursSpentStateTotal() throws OseeCoreException;

   double getWorldViewHoursSpentTotal() throws OseeCoreException;

   int getWorldViewPercentCompleteState() throws OseeCoreException;

   int getWorldViewPercentCompleteStateReview() throws OseeCoreException;

   int getWorldViewPercentCompleteStateTask() throws OseeCoreException;

   int getWorldViewPercentCompleteTotal() throws OseeCoreException;

}
