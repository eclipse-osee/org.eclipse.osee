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
package org.eclipse.osee.framework.ui.skynet.artifact;

import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * Provide UI components to ArtifactPersistenceManager
 *
 * @author Donald G. Dunne
 */
public class ArtifactPersistenceManagerUI {

   private ArtifactPersistenceManagerUI() {
      // Utility Class
   }

   public static void cancelTxAndExceptionUiIfErrors(XResultData rd, String title, SkynetTransaction transaction) {
      if (rd.isErrors()) {
         transaction.cancel();
         XResultDataUI.report(rd, title);
      }
   }

}
