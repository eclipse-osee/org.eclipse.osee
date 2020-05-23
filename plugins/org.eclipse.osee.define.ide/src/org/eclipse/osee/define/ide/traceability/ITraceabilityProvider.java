/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.define.ide.traceability;

import java.util.Collection;
import java.util.Set;
import org.eclipse.osee.define.ide.traceability.data.RequirementData;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author John R. Misinco
 */
public interface ITraceabilityProvider {

   public RequirementData getRequirementData();

   public HashCollectionSet<Artifact, String> getRequirementToCodeUnitsMap();

   public Set<String> getCodeUnits();

   public Collection<Artifact> getTestUnitArtifacts(Artifact requirement);

   public Artifact getTestUnitByName(String name);
}
