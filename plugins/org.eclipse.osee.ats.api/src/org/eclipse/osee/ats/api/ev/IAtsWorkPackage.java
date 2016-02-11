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

import java.util.Date;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkPackage extends IAtsConfigObject {

   public String getActivityId() throws OseeCoreException;

   public String getActivityName() throws OseeCoreException;

   public String getGuid();

   public String getWorkPackageId() throws OseeCoreException;

   public String getWorkPackageProgram() throws OseeCoreException;

   public AtsWorkPackageType getWorkPackageType() throws OseeCoreException;

   public int getWorkPackagePercent() throws OseeCoreException;

   public Date getStartDate() throws OseeCoreException;

   public Date getEndDate() throws OseeCoreException;

}
