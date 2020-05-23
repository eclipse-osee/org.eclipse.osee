/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;

/**
 * Set a Artifact list
 * 
 * @author Donald G. Dunne
 */
public class XArtifactList extends XListViewer {

   public XArtifactList(String displayLabel) {
      super(displayLabel);
      setLabelProvider(new ArtifactLabelProvider());
      setContentProvider(new ArrayContentProvider());
   }

   public Collection<Artifact> getSelectedArtifacts() {
      return Collections.castMatching(Artifact.class, getSelected());
   }
}