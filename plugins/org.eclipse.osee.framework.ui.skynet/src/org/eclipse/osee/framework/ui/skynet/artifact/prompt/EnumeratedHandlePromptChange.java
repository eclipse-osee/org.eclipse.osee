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
package org.eclipse.osee.framework.ui.skynet.artifact.prompt;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.EnumSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.EnumSelectionDialog.Selection;

/**
 * @author Jeff C. Phillips
 */
public class EnumeratedHandlePromptChange implements IHandlePromptChange {
   private final EnumSelectionDialog dialog;
   private final Collection<? extends Artifact> artifacts;
   private final IAttributeType attributeType;
   private final boolean persist;

   public EnumeratedHandlePromptChange(Collection<? extends Artifact> artifacts, IAttributeType attributeType, String displayName, boolean persist) {
      super();
      this.artifacts = artifacts;
      this.attributeType = attributeType;
      this.persist = persist;
      this.dialog = new EnumSelectionDialog(attributeType, artifacts);
   }

   @Override
   public boolean promptOk() {
      return dialog.open() == Window.OK;
   }

   @Override
   public boolean store() throws OseeCoreException {
      Set<String> selected = new HashSet<String>();
      for (Object obj : dialog.getResult()) {
         selected.add((String) obj);
      }
      if (artifacts.size() > 0) {
         SkynetTransaction transaction =
            !persist ? null : new SkynetTransaction(artifacts.iterator().next().getBranch(),
               "Change enumerated attribute");
         for (Artifact artifact : artifacts) {
            List<String> current = artifact.getAttributesToStringList(attributeType);
            if (dialog.getSelected() == Selection.AddSelection) {
               current.addAll(selected);
               artifact.setAttributeValues(attributeType, current);
            } else if (dialog.getSelected() == Selection.DeleteSelected) {
               current.removeAll(selected);
               artifact.setAttributeValues(attributeType, current);
            } else if (dialog.getSelected() == Selection.ReplaceAll) {
               artifact.setAttributeValues(attributeType, selected);
            } else {
               AWorkbench.popup("ERROR", "Unhandled selection type => " + dialog.getSelected().name());
               return false;
            }
            if (persist) {
               artifact.persist(transaction);
            }
         }
         if (persist) {
            transaction.execute();
         }
      }
      return true;
   }
}