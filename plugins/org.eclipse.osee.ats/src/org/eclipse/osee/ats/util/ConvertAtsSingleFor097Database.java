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
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class ConvertAtsSingleFor097Database extends XNavigateItemAction {

   public ConvertAtsSingleFor097Database(XNavigateItem parent) {
      super(parent, "Convert ATS Single for 0.9.7 Database", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         EntryDialog ed = new EntryDialog("Convert Single ATS for 0.9.7 Database", "Enter ATS HRID to Convert");
         if (ed.open() == 0) {

            Artifact artifact =
               ArtifactQuery.getArtifactFromId(ed.getEntry().replaceAll(" ", ""), AtsUtil.getAtsBranch());
            if (artifact == null) {
               AWorkbench.popup("No artifact found with HRID " + ed.getEntry());
               return;
            }
            HashCollection<String, String> testNameToResultsMap = new HashCollection<String, String>();
            SkynetTransaction transaction =
               new SkynetTransaction(AtsUtil.getAtsBranch(), "Convert ATS for 0.9.7 Database");
            ConvertAtsFor097Database.convertWorkflowArtifacts(testNameToResultsMap, Arrays.asList(artifact),
               transaction);
            transaction.execute();
            XResultData xResultData = new XResultData(false);
            ValidateAtsDatabase.addResultsMapToResultData(xResultData, testNameToResultsMap);
            xResultData.report(getName());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
