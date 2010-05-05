/*
 * Created on Apr 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.change;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeXViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.XChangeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.XChangeLabelProvider;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class ChangeReportTable implements EditorSection.IWidget {

   private ChangeXViewer xChangeViewer;
   private final ChangeUiData changeData;

   public ChangeReportTable(ChangeUiData changeData) {
      this.changeData = changeData;
   }

   public ChangeXViewer getXViewer() {
      return xChangeViewer;
   }

   @Override
   public void onCreate(IManagedForm managedForm, Composite parent) {
      FormToolkit toolkit = managedForm.getToolkit();
      ScrolledForm form = managedForm.getForm();
      form.getBody().setLayout(new GridLayout());
      form.getBody().setBackground(parent.getBackground());

      Composite composite = toolkit.createComposite(parent, SWT.NONE);

      GridLayout layout = new GridLayout();
      layout.marginBottom = 5;
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      composite.setLayout(layout);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.widthHint = 300;
      composite.setLayoutData(gd);

      int viewerStyle = SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION;
      xChangeViewer = new ChangeXViewer(composite, viewerStyle, new ChangeXViewerFactory());
      xChangeViewer.setContentProvider(new XChangeContentProvider());
      xChangeViewer.setLabelProvider(new XChangeLabelProvider(xChangeViewer));

      Tree tree = xChangeViewer.getTree();
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      Label label = xChangeViewer.getStatusLabel();
      toolkit.adapt(label, false, false);

      new ChangeDragAndDrop(tree, ChangeXViewerFactory.NAMESPACE);
      onUpdate();
   }

   @Override
   public void onLoading() {
      xChangeViewer.setInput(Arrays.asList("Loading..."));
   }

   @Override
   public void onUpdate() {
      Collection<?> input;
      if (changeData.isLoaded()) {
         input = changeData.getChanges();
      } else {
         input = Arrays.asList("Not Loaded");
      }
      xChangeViewer.setInput(input);
   }

   private final class ChangeDragAndDrop extends SkynetDragAndDrop {

      public ChangeDragAndDrop(Tree tree, String viewId) {
         super(tree, viewId);
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         event.detail = DND.DROP_NONE;
      }

      @Override
      public Artifact[] getArtifacts() {
         IStructuredSelection selection = (IStructuredSelection) xChangeViewer.getSelection();
         ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

         if (selection != null && !selection.isEmpty()) {
            for (Object object : selection.toArray()) {

               if (object instanceof IAdaptable) {
                  Artifact artifact = (Artifact) ((IAdaptable) object).getAdapter(Artifact.class);

                  if (artifact != null) {
                     artifacts.add(artifact);
                  }
               }
            }
         }
         return artifacts.toArray(new Artifact[artifacts.size()]);
      }
   }

}
