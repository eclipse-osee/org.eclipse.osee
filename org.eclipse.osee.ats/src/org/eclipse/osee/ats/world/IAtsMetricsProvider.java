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
package org.eclipse.osee.ats.world;

import java.util.Collection;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsMetricsProvider {

   public Collection<? extends Artifact> getMetricsArtifacts() throws OseeCoreException;

   public VersionArtifact getMetricsVersionArtifact() throws OseeCoreException;

   public double getManHoursPerDayPreference() throws OseeCoreException;
}
