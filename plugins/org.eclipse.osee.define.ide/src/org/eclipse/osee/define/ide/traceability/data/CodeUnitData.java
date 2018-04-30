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
package org.eclipse.osee.define.ide.traceability.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
public class CodeUnitData extends BaseTraceDataCache {

   private final Map<String, Artifact> codeUnitMap;

   public CodeUnitData(BranchId branch) {
      super("Code Unit Data", branch);
      this.codeUnitMap = new HashMap<>();
   }

   @Override
   protected void doBulkLoad(IProgressMonitor monitor) throws Exception {
      List<Artifact> codeUnits = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.CodeUnit, getBranch());
      populateTraceMap(monitor, codeUnits, codeUnitMap);
      monitor.worked(30);
   }

   @Override
   public void reset() {
      super.reset();
      codeUnitMap.clear();
   }

   public Collection<Artifact> getAllCodeUnits() {
      return codeUnitMap.values();
   }

   public Artifact getCodeUnitByName(String codeUnitName) {
      return codeUnitMap.get(codeUnitName);
   }
}
