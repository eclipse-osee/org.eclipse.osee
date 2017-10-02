/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast;

import java.util.Collection;
import org.eclipse.osee.vcast.model.VCastBranchCoverage;
import org.eclipse.osee.vcast.model.VCastBranchData;
import org.eclipse.osee.vcast.model.VCastFunction;
import org.eclipse.osee.vcast.model.VCastInstrumentedFile;
import org.eclipse.osee.vcast.model.VCastMcdcCoverage;
import org.eclipse.osee.vcast.model.VCastMcdcCoverageCondition;
import org.eclipse.osee.vcast.model.VCastMcdcCoveragePair;
import org.eclipse.osee.vcast.model.VCastMcdcCoveragePairRow;
import org.eclipse.osee.vcast.model.VCastMcdcData;
import org.eclipse.osee.vcast.model.VCastMcdcDataCondition;
import org.eclipse.osee.vcast.model.VCastProject;
import org.eclipse.osee.vcast.model.VCastProjectFile;
import org.eclipse.osee.vcast.model.VCastResult;
import org.eclipse.osee.vcast.model.VCastSetting;
import org.eclipse.osee.vcast.model.VCastSourceFile;
import org.eclipse.osee.vcast.model.VCastSourceFileJoin;
import org.eclipse.osee.vcast.model.VCastStatementCoverage;
import org.eclipse.osee.vcast.model.VCastStatementData;
import org.eclipse.osee.vcast.model.VCastVersion;
import org.eclipse.osee.vcast.model.VCastWritable;

/**
 * @author Shawn F. Cook
 */
public interface VCastDataStore {

   Collection<VCastBranchCoverage> getAllBranchCoverages() ;

   Collection<VCastBranchData> getAllBranchData() ;

   Collection<VCastFunction> getAllFunctions() ;

   Collection<VCastInstrumentedFile> getAllInstrumentedFiles() ;

   Collection<VCastMcdcCoverage> getAllMcdcCoverages() ;

   Collection<VCastMcdcCoverageCondition> getAllMcdcCoverageConditions() ;

   Collection<VCastMcdcCoveragePairRow> getAllMcdcCoveragePairRows() ;

   Collection<VCastMcdcCoveragePair> getAllMcdcCoveragePairs() ;

   Collection<VCastMcdcData> getAllMcdcData() ;

   Collection<VCastMcdcDataCondition> getAllMcdcDataConditions() ;

   Collection<VCastProjectFile> getAllProjectFiles() ;

   Collection<VCastProject> getAllProjects() ;

   Collection<VCastResult> getAllResults() ;

   Collection<VCastSetting> getAllSettings() ;

   Collection<VCastSourceFile> getAllSourceFiles() ;

   Collection<VCastStatementCoverage> getAllStatementCoverages() ;

   Collection<VCastStatementData> getAllStatementData() ;

   VCastVersion getVersion() ;

   VCastWritable getWritable() ;

   VCastSourceFileJoin getSourceFileJoin(VCastInstrumentedFile instrumentedFile) ;

   Collection<VCastFunction> getFunctions(VCastInstrumentedFile instrumentedFile) ;

   Collection<VCastStatementCoverage> getStatementCoverageLines(VCastFunction function) ;

   Collection<VCastStatementData> getStatementData(VCastStatementCoverage statementCoverage) ;

   Collection<VCastResult> getResults(VCastStatementData statementDataItem) ;

   void setIsMCDC();

   boolean getIsMCDC();

   void setIsBranch();

   boolean getIsBranchCoverage();
}
