/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Megumi Telles
 */
public interface IArtifactEditorProvider {

   public void addOtherWidgets(Artifact artifact, boolean isEditable, ArtifactEditor editor, Composite composite,
      Collection<XWidget> widgets);

   public boolean isApplicable(Artifact artifact);

}
