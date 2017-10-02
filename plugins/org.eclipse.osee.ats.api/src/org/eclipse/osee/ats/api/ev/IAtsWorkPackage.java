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

   public String getActivityId() ;

   public String getActivityName() ;

   public String getGuid();

   public String getWorkPackageId() ;

   public String getWorkPackageProgram() ;

   public AtsWorkPackageType getWorkPackageType() ;

   public int getWorkPackagePercent() ;

   public Date getStartDate() ;

   public Date getEndDate() ;

}
