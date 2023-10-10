/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.xchild;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWithFilteredDialog;

/**
 * Widget backed by a single parent artifact (eg: folder) with it's children UserGroups as the selectable items for the
 * filtered dialog.
 *
 * @author Donald G. Dunne
 */
public class AbstractXHyperlinkWfdSelectedChild extends XHyperlinkWithFilteredDialog<ArtifactToken> {

   private final ArtifactToken parentArt;

   public AbstractXHyperlinkWfdSelectedChild(String label, ArtifactToken parentArt) {
      super(label);
      this.parentArt = parentArt;
   }

   @Override
   public Collection<ArtifactToken> getSelectable() {
      List<ArtifactToken> selectable = new ArrayList<>();
      Artifact parent = ArtifactQuery.getArtifactFromId(parentArt.getId(),
         (parentArt.getBranch().isValid() ? parentArt.getBranch() : COMMON));
      for (Artifact art : parent.getChildren()) {
         if (isSelectable(art)) {
            selectable.add(art);
         }
      }
      return selectable;
   }

   // Override to apply criteria to applicable children
   protected boolean isSelectable(Artifact art) {
      return true;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         if (isRequiredEntry() && getSelected() == null) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
         }
      }
      return status;
   }

}
