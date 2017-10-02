/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ev;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.column.WorkPackageFilterTreeDialog.IWorkPackageProvider;

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

   private Collection<IAtsWorkPackage> filterInput(boolean showAll)  {
      java.util.List<IAtsWorkPackage> filtered = new ArrayList<>();
      for (IAtsWorkPackage workPkg : workPackages) {
         if (showAll || workPkg.isActive()) {
            filtered.add(workPkg);
         }
      }
      return filtered;
   }

}
