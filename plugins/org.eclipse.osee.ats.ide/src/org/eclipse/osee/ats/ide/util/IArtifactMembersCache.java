/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.util;

import java.util.List;
import org.eclipse.osee.ats.ide.workflow.CollectorArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IArtifactMembersCache<T extends CollectorArtifact> {

   List<Artifact> getMembers(T artifact);

   void decache(T artifact);

   void invalidate();

   String getMemberOrder(T memberArt, Artifact member);

}