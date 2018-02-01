/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.navigate;

import java.util.Random;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class GenerateIdsAndArtId extends XNavigateItemAction {

   public GenerateIdsAndArtId(XNavigateItem parent) {
      super(parent, "Generate Id and ArtId", AtsImage.REPORT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      XResultData resultData = new XResultData();
      Random r = new Random();
      for (int i = 0; i < 10; i++) {
         long next = r.nextLong();
         if (next > 0) {
            resultData.log(String.valueOf(next));
         } else {
            --i;
         }
      }

      resultData.log("\nArtifact Ids");
      for (int i = 0; i < 10; i++) {
         resultData.log(String.valueOf(Lib.generateArtifactIdAsInt()));
      }
      XResultDataUI.report(resultData, getName());
   }
}
