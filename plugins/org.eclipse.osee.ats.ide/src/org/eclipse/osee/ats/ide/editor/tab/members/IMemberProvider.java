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

package org.eclipse.osee.ats.ide.editor.tab.members;

import java.util.List;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public interface IMemberProvider {

   String getCollectorName();

   KeyedImage getImageKey();

   List<Artifact> getMembers();

   Artifact getArtifact();

   void setArtifact(Artifact artifact);

   Long getId();

   void addMember(Artifact artifact);

   IXViewerFactory getXViewerFactory(Artifact awa);

   String getColumnName();

   RelationTypeSide getMemberRelationTypeSide();

   void promptChangeOrder(Artifact artifact, List<Artifact> selectedAtsArtifacts);

   Result isAddValid(List<Artifact> artifacts);

   String getMembersName();

   void deCacheAndReload(boolean pend, IJobChangeListener listener);

   boolean isBacklog();

   boolean isSprint();

}
