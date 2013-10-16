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
package org.eclipse.osee.ats.core.internal.column.ev;

import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageIdUtility extends AbstractRelatedWorkPackageColumn {

   public WorkPackageIdUtility(IAtsEarnedValueServiceProvider earnedValueServiceProvider) {
      super(earnedValueServiceProvider);
   }

   @Override
   protected String getColumnValue(IAtsWorkPackage workPkg) throws OseeCoreException {
      return workPkg.getWorkPackageId();
   }

   @Override
   public String getDescription() {
      return "Provides Work Package Id from the selected Work Package related to the selected workflow.";
   }

}
