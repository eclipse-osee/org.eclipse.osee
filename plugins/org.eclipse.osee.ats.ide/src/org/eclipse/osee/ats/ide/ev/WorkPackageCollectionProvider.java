/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.ev;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.ide.column.WorkPackageFilterTreeDialog.IWorkPackageProvider;

/**
 * Provides work packages from give collection
 * 
 * @author Donald G. Dunne
 */
public class WorkPackageCollectionProvider implements IWorkPackageProvider {

   private final Collection<IAtsWorkPackage> workPackages;

   public WorkPackageCollectionProvider(Collection<IAtsWorkPackage> workPackages) {
      this.workPackages = workPackages;
   }

   @Override
   public Collection<IAtsWorkPackage> getActiveWorkPackages() {
      return filterInput(false);
   }

   @Override
   public Collection<IAtsWorkPackage> getAllWorkPackages() {
      return filterInput(true);
   }

   private Collection<IAtsWorkPackage> filterInput(boolean showAll) {
      java.util.List<IAtsWorkPackage> filtered = new ArrayList<>();
      for (IAtsWorkPackage workPkg : workPackages) {
         if (showAll || workPkg.isActive()) {
            filtered.add(workPkg);
         }
      }
      return filtered;
   }

}
