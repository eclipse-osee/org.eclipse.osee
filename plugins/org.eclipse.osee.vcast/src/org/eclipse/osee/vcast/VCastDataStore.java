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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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

   Collection<VCastBranchCoverage> getAllBranchCoverages() throws OseeCoreException;

   Collection<VCastBranchData> getAllBranchData() throws OseeCoreException;

   Collection<VCastFunction> getAllFunctions() throws OseeCoreException;

   Collection<VCastInstrumentedFile> getAllInstrumentedFiles() throws OseeCoreException;

   Collection<VCastMcdcCoverage> getAllMcdcCoverages() throws OseeCoreException;

   Collection<VCastMcdcCoverageCondition> getAllMcdcCoverageConditions() throws OseeCoreException;

   Collection<VCastMcdcCoveragePairRow> getAllMcdcCoveragePairRows() throws OseeCoreException;

   Collection<VCastMcdcCoveragePair> getAllMcdcCoveragePairs() throws OseeCoreException;

   Collection<VCastMcdcData> getAllMcdcData() throws OseeCoreException;

   Collection<VCastMcdcDataCondition> getAllMcdcDataConditions() throws OseeCoreException;

   Collection<VCastProjectFile> getAllProjectFiles() throws OseeCoreException;

   Collection<VCastProject> getAllProjects() throws OseeCoreException;

   Collection<VCastResult> getAllResults() throws OseeCoreException;

   Collection<VCastSetting> getAllSettings() throws OseeCoreException;

   Collection<VCastSourceFile> getAllSourceFiles() throws OseeCoreException;

   Collection<VCastStatementCoverage> getAllStatementCoverages() throws OseeCoreException;

   Collection<VCastStatementData> getAllStatementData() throws OseeCoreException;

   VCastVersion getVersion() throws OseeCoreException;

   VCastWritable getWritable() throws OseeCoreException;

   VCastSourceFileJoin getSourceFileJoin(VCastInstrumentedFile instrumentedFile) throws OseeCoreException;

   Collection<VCastFunction> getFunctions(VCastInstrumentedFile instrumentedFile) throws OseeCoreException;

   Collection<VCastStatementCoverage> getStatementCoverageLines(VCastFunction function) throws OseeCoreException;

   Collection<VCastStatementData> getStatementData(VCastStatementCoverage statementCoverage) throws OseeCoreException;

   Collection<VCastResult> getResults(VCastStatementData statementDataItem) throws OseeCoreException;

   void setIsMCDC();

   boolean getIsMCDC();

   void setIsBranch();

   boolean getIsBranchCoverage();
}
