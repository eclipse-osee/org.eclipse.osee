/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
