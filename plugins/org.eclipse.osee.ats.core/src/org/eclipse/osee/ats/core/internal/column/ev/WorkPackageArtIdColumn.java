/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.internal.column.ev;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageArtIdColumn extends AbstractRelatedWorkPackageColumn {

   public WorkPackageArtIdColumn(IAtsEarnedValueServiceProvider earnedValueServiceProvider, AtsApi atsApi) {
      super(earnedValueServiceProvider, atsApi);
   }

   @Override
   protected String getColumnValue(IAtsWorkPackage workPkg) {
      return workPkg.getIdString();
   }

   @Override
   protected String getColumnValue(ArtifactToken wpArt) {
      return wpArt.getIdString();
   }

}
