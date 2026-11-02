/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.program;

import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.ide.config.copy.CopyAtsValidation;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class CopyCommonAtsValidation extends CopyAtsValidation {

   private final AtsConfigData AtsConfigData;

   public CopyCommonAtsValidation(AtsConfigData configData, XResultData resultData) {
      super(configData, resultData);
      AtsConfigData = configData;
   }

   @Override
   public void validate() {
      IAtsProgram fromProgram = AtsConfigData.getFromProgram();
      if (fromProgram == null) {
         resultData.error("Must Select Program to Copy From");
         return;
      }
      IAtsProgram newProgram = AtsConfigData.getNewProgram();
      if (newProgram == null) {
         resultData.error("Must Select New Program");
         return;
      }

      if (fromProgram.getName().equals(newProgram.getName())) {
         resultData.errorf("Program names can't be same [%s]", newProgram.getName());
         return;
      }
      if (AtsApiService.get().getProgramService().getNamespace(fromProgram).equals(
         AtsApiService.get().getProgramService().getNamespace(newProgram))) {
         resultData.errorf("Program static id prefix can't be same [%s]",
            AtsApiService.get().getProgramService().getNamespace(newProgram));
         return;
      }
      if (fromProgram.getName() != null && fromProgram.getName().equals(newProgram.getName())) {
         resultData.errorf("Copy from and to Program name can't be same [%s]", newProgram.getName());
         return;
      }
      if (fromProgram.getStoreObject().getName().equals(newProgram.getStoreObject().getName())) {
         resultData.errorf("Program name can't be same [%s]", fromProgram.getStoreObject().getName());
         return;
      }

      super.validate();
   }

}
