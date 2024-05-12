/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Initialization class that populate the demo database with requirements, workflows and etc. This data can be used for
 * development or for demoing OSEE features without having to re-create manuall every time. It's also used for
 * integration testing.
 *
 * @author Donald G. Dunne
 */
public class AtsDbConfigPopulateDemoDbAndTestOp {

   private final AtsApi atsApi;
   private final XResultData rd;

   public AtsDbConfigPopulateDemoDbAndTestOp(XResultData rd, AtsApi atsApi) {
      this.rd = rd;
      this.atsApi = atsApi;
   }

   public XResultData run() {

      (new Pdd15CreateNotesAndAnnotations(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd20CreateCommittedAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd20CreateCommittedActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd21CreateUnCommittedAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd21CreateUnCommittedActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd22CreateUnCommittedConflictedAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd22CreateUnCommittedConflictedActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd23CreateNoBranchAction(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }
      (new Pdd23CreateNoBranchActionTest(rd, atsApi)).run();
      if (rd.isErrors()) {
         return rd;
      }

      return new XResultData();
   }

}
