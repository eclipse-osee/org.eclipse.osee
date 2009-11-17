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
package org.eclipse.osee.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.coverage.blam.AbstractCoverageBlam;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamContributionManager;

/**
 * @author Donald G. Dunne
 */
public class CoverageManager {

   public static void importCoverage(ICoverageImporter coverageImporter) throws OseeCoreException {
      CoverageImport coverageImport = coverageImporter.run();
      CoverageEditor.open(new CoverageEditorInput(coverageImport.getName(), null, coverageImport, false));
   }

   public static Collection<AbstractCoverageBlam> getCoverageBlams() {
      List<AbstractCoverageBlam> blams = new ArrayList<AbstractCoverageBlam>();
      for (AbstractBlam blam : BlamContributionManager.getBlamOperations()) {
         if (blam instanceof AbstractCoverageBlam) {
            blams.add((AbstractCoverageBlam) blam);
         }
      }
      return blams;
   }

}
