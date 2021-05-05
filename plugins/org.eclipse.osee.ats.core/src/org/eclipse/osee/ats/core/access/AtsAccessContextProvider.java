/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.access;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * This provider will only be used if all other providers return false for isAtsApplicable. Not provided through OSGI
 * cause it's used as default when no other providers are applicable.
 *
 * @author Donald G. Dunne
 */
public class AtsAccessContextProvider extends AbstractAtsAccessContextProvider {

   public AtsAccessContextProvider() {
      AtsAccessContexts.register();
   }

   public void setAtsApi(AtsApi atsApi) {
      if (atsApi == null) {
         atsApi = AtsApiService.get();
      }
      this.atsApi = atsApi;
   }

   /**
    * Should never be called cause this provider is used as default
    */
   @Override
   public boolean isAtsApplicable(BranchId branch, ArtifactToken assocArt) {
      throw new OseeArgumentException("Should never be called");
   }

   @Override
   public boolean isApplicableDb() {
      return true;
   }

}
