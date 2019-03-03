/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.io.Writer;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Morgan E. Cook
 */
public interface TraceabilityOperations {

   public void generateTraceReport(BranchId branchId, String codeRoot, String traceRoot, Writer providedWriter, ArtifactTypeToken artifactType, AttributeTypeToken attributeType);

   public TraceData getSrsToImpd(BranchId branch, ArtifactTypeId excludeType);
}
