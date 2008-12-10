/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public class OpenWithSelectionListener extends SelectionAdapter {

   private IRenderer renderer;
   private Viewer viewer;

   public OpenWithSelectionListener(IRenderer renderer, Viewer viewer) {
      super();
      this.renderer = renderer;
      this.viewer = viewer;
   }

   @Override
   public void widgetSelected(SelectionEvent e) {
      IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
      Iterator<?> iterator = selection.iterator();
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(selection.size());

      while (iterator.hasNext()) {
         artifacts.add((Artifact) iterator.next());
      }

      try {
         renderer.open(artifacts);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

}
