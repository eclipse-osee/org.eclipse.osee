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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.StackedViewer;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

public abstract class XStackedWidget extends XLabel {

   private StackedViewer stackedViewer;
   private StyledText currentPageLabel;
   private Composite container;
   private int minPage;
   private int maxPage;
   private int currentPage;
   private int totalPages;

   public XStackedWidget(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
      setToolTip("Navigate pages by clicking forward and backward buttons.");
      minPage = 0;
      maxPage = 0;
      currentPage = 0;
      totalPages = 0;
   }

   public void dispose() {
      super.dispose();
   }

   public XStackedWidget(String displayLabel) {
      this(displayLabel, "");
   }

   protected void setPageRange(int minPage, int maxPage) throws OseeArgumentException {
      if (minPage < 0) throw new OseeArgumentException("Min Number of Pages must be greater than 0");
      if (maxPage < 1) throw new OseeArgumentException("Max Number of Pages must be at least 1");

      if (maxPage < minPage) {
         throw new OseeArgumentException(
               String.format("Invalid required number of pages [%s] < [%s]", maxPage, minPage));
      }
      this.minPage = minPage;
      this.maxPage = maxPage;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setToolTip(java.lang.String)
    */
   @Override
   public void setToolTip(String toolTip) {
      if (Strings.isValid(toolTip)) {
         super.setToolTip(toolTip);
      }
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#refresh()
    */
   @Override
   public void refresh() {
      updateCurrentPageLabel();
      stackedViewer.getStackComposite().layout();
   }

   @Override
   public void createWidgets(final Composite parent, int horizontalSpan) {
      container = new Composite(parent, SWT.NONE);
      container.setLayout(ALayout.getZeroMarginLayout(isDisplayLabel() ? 2 : 1, false));
      container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (isDisplayLabel() && Strings.isValid(getLabel())) {
         labelWidget = new Label(container, SWT.NONE);
         labelWidget.setText(String.format("%s:", getLabel()));
         labelWidget.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }
      createStackedControl(container);

      addToolTip(container, getToolTip());
      refresh();
   }

   private void createStackedControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createToolBar(composite);

      stackedViewer = new StackedViewer(composite, SWT.BORDER);
      stackedViewer.setLayout(ALayout.getZeroMarginLayout());
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.minimumHeight = 60;
      gd.minimumWidth = 60;
      stackedViewer.setLayoutData(gd);
      stackedViewer.displayArea(StackedViewer.DEFAULT_CONTROL);
   }

   private void createToolBar(Composite parent) {
      Composite composite = new Composite(parent, SWT.BORDER);
      GridLayout layout = new GridLayout(3, false);
      layout.marginHeight = 1;
      layout.marginLeft = 5;
      layout.marginWidth = 2;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      currentPageLabel = new StyledText(composite, SWT.READ_ONLY | SWT.SINGLE | SWT.WRAP);
      currentPageLabel.setAlignment(SWT.RIGHT);
      currentPageLabel.setFont(JFaceResources.getBannerFont());
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
      gd.minimumWidth = 10;
      currentPageLabel.setLayoutData(gd);
      currentPageLabel.setText("0 of 0");

      Composite filler = new Composite(composite, SWT.NONE);
      GridLayout layout1 = new GridLayout(1, false);
      filler.setLayout(layout1);
      filler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      ToolBar toolbar = new ToolBar(composite, SWT.FLAT | SWT.HORIZONTAL);
      toolbar.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
      ToolBarManager manager = new ToolBarManager(toolbar);
      manager.add(new Separator());
      manager.add(new Back());
      manager.add(new Forward());
      manager.add(new Separator());
      manager.add(new AddPage());
      manager.add(new RemovePage());
      manager.update(true);
   }

   private void updateCurrentPageLabel() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            if (Widgets.isAccessible(currentPageLabel)) {
               currentPageLabel.setText(String.format("%s of %s", getCurrentPageIndex(), getTotalPages()));
            }
         }
      });
   }

   private void addToolTip(Control control, String toolTipText) {
      if (Strings.isValid(toolTipText)) {
         control.setToolTipText(toolTipText);
         if (control instanceof Composite) {
            for (Control child : ((Composite) control).getChildren()) {
               child.setToolTipText(toolTipText);
            }
         }
      }
   }

   public void setDisplay(int index) {
      stackedViewer.displayArea(String.valueOf(index));
      setCurrentPage(index);
   }

   protected abstract void createPage(String id, Composite parent);

   private int getCurrentPageIndex() {
      return currentPage;
   }

   private int getTotalPages() {
      return Widgets.isAccessible(stackedViewer) ? stackedViewer.getNumberOfControls() : 0;
   }

   private void setTotalPages(int index) {
      //      this.totalPages = index;
   }

   private void setCurrentPage(int index) {
      if (index >= 0 && index < getTotalPages()) {
         this.currentPage = index;
         updateCurrentPageLabel();
      }
   }

   private int getNextPageIndex() {
      if (getCurrentPageIndex() + 1 < getTotalPages()) {
         return getCurrentPageIndex() + 1;
      } else {
         return 0;
      }
   }

   private int getPreviousPageIndex() {
      if (getCurrentPageIndex() - 1 >= 0) {
         return getCurrentPageIndex() - 1;
      } else {
         return getTotalPages();
      }
   }

   private void disposeControl(Control control) {
      if (control != null && !control.isDisposed()) {
         if (control instanceof Composite) {
            for (Control child : ((Composite) control).getChildren()) {
               disposeControl(child);
            }
         }
         control.dispose();
      }
   }

   private void handlePageCreation() {
      System.out.println("Add Page");
      Composite composite = new Composite(stackedViewer.getStackComposite(), SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      String id = String.valueOf(getTotalPages());
      createPage(id, composite);
      stackedViewer.addControl(id, composite);
   }

   private void handlePageDeletion() {
      System.out.println("Delete Page");
      int current = getCurrentPageIndex();
      int previous = getPreviousPageIndex();
      setCurrentPage(previous);
      setDisplay(previous);

      Control control = stackedViewer.removeControl(String.valueOf(current));
      disposeControl(control);
   }

   private final class Back extends Action {
      public Back() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("nav_backward.gif"));
         setToolTipText("Back to previous page");
      }

      public void run() {
         if (isEnabled()) {
            int previousPage = getPreviousPageIndex();
            setDisplay(previousPage);
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.action.Action#isEnabled()
       */
      @Override
      public boolean isEnabled() {
         return getCurrentPageIndex() != 0;
      }
   }

   private final class Forward extends Action {
      public Forward() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("nav_forward.gif"));
         setToolTipText("Forward to next page");
      }

      public void run() {
         if (isEnabled()) {
            int nextPage = getNextPageIndex();
            setDisplay(nextPage);
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.action.Action#isEnabled()
       */
      @Override
      public boolean isEnabled() {
         return getCurrentPageIndex() != getTotalPages();
      }
   }

   private final class AddPage extends Action {
      public AddPage() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("add.gif"));
         setToolTipText("Adds a page");
      }

      public void run() {
         handlePageCreation();
      }
   }

   private final class RemovePage extends Action {
      public RemovePage() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("remove.gif"));
         setToolTipText("Removes a page");
      }

      public void run() {
         if (isEnabled()) {
            handlePageDeletion();
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.action.Action#isEnabled()
       */
      @Override
      public boolean isEnabled() {
         return getTotalPages() == 0;
      }
   }

   private final class PageStatusValidator implements ISelectionStatusValidator {

      @Override
      public IStatus validate(Object[] selection) {
         IStatus status = null;
         int numberSelected = selection.length;
         if (minPage <= numberSelected && maxPage >= numberSelected) {
            status = Status.OK_STATUS;
         } else {
            List<String> message = new ArrayList<String>();
            if (numberSelected < minPage) {
               message.add(String.format("Must have at least [%s] pages", minPage));
            }
            if (numberSelected > maxPage) {
               message.add(String.format("Can't add more than [%s] pages", maxPage));
            }
            status = new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, Collections.toString(" &&", message));
         }
         return status;
      }
   }
}
