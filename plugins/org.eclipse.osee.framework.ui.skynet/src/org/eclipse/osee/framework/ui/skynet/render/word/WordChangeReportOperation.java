/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render.word;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

public class WordChangeReportOperation extends AbstractOperation {
   private final Collection<Change> changes;
   private final boolean suppressWord;
   private final String diffReportFolderName;

   public WordChangeReportOperation(Collection<Change> changes, boolean suppressWord, String diffReportFolderName) {
      super("Word Change Report", SkynetGuiPlugin.PLUGIN_ID);
      this.changes = changes;
      this.suppressWord = suppressWord;
      this.diffReportFolderName = diffReportFolderName;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      VariableMap variableMap = new VariableMap();
      variableMap.setValue("suppressWord", suppressWord);
      variableMap.setValue("diffReportFolderName", diffReportFolderName);

      Collection<ArtifactDelta> compareArtifacts = ChangeManager.getCompareArtifacts(changes);

      WordTemplateRenderer renderer = new WordTemplateRenderer();
      renderer.setOptions(variableMap);
      renderer.getComparator().compareArtifacts(monitor, PresentationType.DIFF, compareArtifacts);
   }
}