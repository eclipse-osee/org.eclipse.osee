/*
 * Created on Jul 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact.prompt;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.window.Window;
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
   private final String attributeName;
   private final boolean persist;

   public EnumeratedHandlePromptChange(Collection<? extends Artifact> artifacts, String attributeName, String displayName, boolean persist) {
      super();
      this.artifacts = artifacts;
      this.attributeName = attributeName;
      this.persist = persist;
      this.dialog = new EnumSelectionDialog(attributeName, artifacts);
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
            List<String> current = artifact.getAttributesToStringList(attributeName);
            if (dialog.getSelected() == Selection.AddSelection) {
               current.addAll(selected);
               artifact.setAttributeValues(attributeName, current);
            } else if (dialog.getSelected() == Selection.DeleteSelected) {
               current.removeAll(selected);
               artifact.setAttributeValues(attributeName, current);
            } else if (dialog.getSelected() == Selection.ReplaceAll) {
               artifact.setAttributeValues(attributeName, selected);
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