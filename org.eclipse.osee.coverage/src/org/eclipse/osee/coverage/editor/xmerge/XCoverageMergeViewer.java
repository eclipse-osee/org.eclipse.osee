/*
 * Created on Oct 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor.xmerge;

import org.eclipse.osee.coverage.editor.xcover.CoverageContentProvider;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XCoverageMergeViewer extends XCoverageViewer implements IRefreshActionHandler {

   CoverageMergeXViewer mergeXViewer;
   private final CoverageMergeXViewerFactory coverageMergeXViewerFactory;
   private final MergeManager mergeManager;

   public XCoverageMergeViewer(MergeManager mergeManager, ISaveable saveable, CoverageMergeXViewerFactory coverageMergeXViewerFactory, TableType tableType, TableType... types) {
      super(saveable, tableType, types);
      this.mergeManager = mergeManager;
      this.coverageMergeXViewerFactory = coverageMergeXViewerFactory;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      // Create Text Widgets
      if (isDisplayLabel() && !getLabel().equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      Composite mainComp = new Composite(parent, SWT.BORDER);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      if (toolkit != null) toolkit.paintBordersFor(mainComp);

      createTaskActionBar(mainComp);

      xViewer =
            new CoverageMergeXViewer(mergeManager, mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION,
                  coverageMergeXViewerFactory, this);
      mergeXViewer = (CoverageMergeXViewer) xViewer;
      xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      xViewer.setContentProvider(new CoverageContentProvider(mergeXViewer));
      xViewer.setLabelProvider(new CoverageMergeLabelProvider(mergeXViewer));

      if (toolkit != null) toolkit.adapt(xViewer.getStatusLabel(), false, false);

      // NOTE: Don't adapt the tree using xToolkit cause will loose xViewer's context menu
      updateExtraLabel();
   }

   @Override
   public void refreshActionHandler() {
      refresh();
   }

}
