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
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.StackedViewer;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public abstract class XStackedWidget<T> extends XLabel {

   protected StackedControl stackedControl;
   private StyledText currentPageLabel;
   private Composite container;
   private Label messageLabel;
   private Label messageIcon;
   private int minPage;
   private int maxPage;
   private Composite mainComp;
   private int horizontalSpan;
   protected boolean loadPageValues = true;

   public XStackedWidget(String displayLabel) {
      super(displayLabel);
      setToolTip("Navigate pages by clicking forward and backward buttons.");
      minPage = 0;
      maxPage = 0;
   }

   @Override
   public void dispose() {
      stackedControl.dispose();
      super.dispose();
   }

   protected void setPageRange(int minPage, int maxPage) throws OseeArgumentException {
      if (minPage < 0) {
         throw new OseeArgumentException("Min Number of Pages must be greater than 0");
      }
      if (maxPage < 1) {
         throw new OseeArgumentException("Max Number of Pages must be at least 1");
      }

      if (maxPage < minPage) {
         throw new OseeArgumentException(
            String.format("Invalid required number of pages [%s] < [%s]", maxPage, minPage));
      }
      this.minPage = minPage;
      this.maxPage = maxPage;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         if (stackedControl.getTotalPages() > maxPage) {
            status = constructStatus(maxPage, "maximum allowed");
         } else if (stackedControl.getTotalPages() < minPage) {
            status = constructStatus(minPage, "minimum required");
         }
      }
      return status;
   }

   private IStatus constructStatus(int limit, String limitNoun) {
      String message =
         String.format("Has %d page(s) but the %s is %d.", stackedControl.getTotalPages(), limitNoun, limit);
      return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
   }

   @Override
   public Control getControl() {
      if (stackedControl == null) {
         return null;
      }
      return stackedControl.stackedViewer;
   }

   @Override
   public void setToolTip(String toolTip) {
      if (Strings.isValid(toolTip)) {
         super.setToolTip(toolTip);
      }
   }

   @Override
   public void refresh() {
      updateCurrentPageLabel();
      stackedControl.refresh();
   }

   @Override
   protected void createControls(final Composite parent, int horizontalSpan) {
      container = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(isDisplayLabel() ? 2 : 1, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      layout.marginRight = 0;
      container.setLayout(layout);
      container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      if (isDisplayLabel() && Strings.isValid(getLabel())) {
         labelWidget = new Label(container, SWT.NONE);
         labelWidget.setText(String.format("%s:", getLabel()));
         labelWidget.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      mainComp = new Composite(container, SWT.NONE);
      GridLayout layout1 = new GridLayout(1, false);
      layout1.marginHeight = 0;
      layout1.marginWidth = 0;
      layout1.verticalSpacing = 0;
      layout1.horizontalSpacing = 0;
      mainComp.setLayout(layout1);
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      createToolBar(mainComp);
      stackedControl = new StackedControl();
      stackedControl.createControl(mainComp);

      this.horizontalSpan = horizontalSpan;
      createMessageArea(mainComp);
      addToolTip(container, getToolTip());
      updateCurrentPageLabel();
      createPages();
   }

   protected void createPages() {
      createPages(mainComp, horizontalSpan);
      if (!stackedControl.getPages().isEmpty()) {
         stackedControl.setCurrentPage(stackedControl.getPages().iterator().next());
      }
   }

   abstract protected void createPages(Composite parent, int horizontalSpan);

   abstract void createPageWidget(XStackedWidgetPage page, Composite parent);

   private void createMessageArea(Composite parent) {
      Composite messageArea = new Composite(parent, SWT.BORDER);
      GridLayout layout = new GridLayout(2, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      layout.marginLeft = 5;
      layout.horizontalSpacing = 0;
      layout.verticalSpacing = 0;
      layout.marginBottom = 5;

      messageArea.setLayout(layout);
      messageArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      messageArea.setBackground(Displays.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      messageIcon = new Label(messageArea, SWT.NONE);
      messageIcon.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

      messageLabel = new Label(messageArea, SWT.NONE);
      messageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
   }

   private void createToolBar(Composite parent) {
      Composite composite = new Composite(parent, SWT.BORDER);
      GridLayout layout = new GridLayout(2, false);
      layout.marginHeight = 0;
      layout.marginLeft = 5;
      layout.marginWidth = 2;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      composite.setBackground(Displays.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

      currentPageLabel = new StyledText(composite, SWT.READ_ONLY | SWT.SINGLE | SWT.WRAP);
      currentPageLabel.setAlignment(SWT.CENTER);
      currentPageLabel.setFont(JFaceResources.getBannerFont());
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
      gd.minimumWidth = 10;
      currentPageLabel.setLayoutData(gd);
      currentPageLabel.setText("0 of 0");

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

   private void updateCurrentPageLabel() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(currentPageLabel)) {
               int totalPages = stackedControl.getTotalPages();
               int currentPageIndex = stackedControl.getCurrentPageIndex() + 1;
               if (currentPageIndex > totalPages) {
                  currentPageIndex = totalPages;
               }
               currentPageLabel.setText(String.format("Page %s of %s%s", currentPageIndex, totalPages,
                  getPostfixPageLabel(getCurrentPage())));
            }
         }

      });
   }

   protected String getPostfixPageLabel(XStackedWidgetPage page) {
      return "";
   }

   public void addPage() {
      addPage(new XStackedWidgetPage());
   }

   public void addPage(XStackedWidgetPage page) {
      stackedControl.addPage(page);
   }

   protected XStackedWidgetPage getCurrentPage() {
      return stackedControl.getCurrentPage();
   }

   private void setMessage(final int severity, final String format, final Object... args) {
      if (Widgets.isAccessible(messageLabel)) {
         Composite parent = messageLabel.getParent();

         String text = String.format(format, args);
         boolean isVisible = Strings.isValid(text);

         String imageName = null;
         switch (severity) {
            case IStatus.INFO:
               imageName = ISharedImages.IMG_OBJS_INFO_TSK;
               break;
            case IStatus.ERROR:
               imageName = ISharedImages.IMG_OBJS_ERROR_TSK;
               break;
            case IStatus.WARNING:
               imageName = ISharedImages.IMG_OBJS_WARN_TSK;
               break;
            default:
               break;
         }
         Image image =
            Strings.isValid(imageName) ? PlatformUI.getWorkbench().getSharedImages().getImage(imageName) : null;
         messageIcon.setImage(image);
         messageLabel.setText(isVisible ? text : "");

         messageIcon.setVisible(isVisible);
         messageLabel.setVisible(isVisible);
         parent.setVisible(isVisible);
         parent.layout();
      }
   }

   protected abstract void onRemovePage(XStackedWidgetPage page);

   protected abstract void onPageChange(XStackedWidgetPage page);

   /**
    * Called upon page change for sub-class to perform whatever operations are needed to ensure the page text is
    * updated.
    */
   protected abstract void updatePageText(XStackedWidgetPage page);

   private final class Back extends Action {
      public Back() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.NAV_BACKWARD));
         setToolTipText("Back to previous page");
      }

      @Override
      public void run() {
         stackedControl.previous();
         onPageChange(getCurrentPage());
      }
   }

   private final class Forward extends Action {
      public Forward() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.NAV_FORWARD));
         setToolTipText("Forward to next page");
      }

      @Override
      public void run() {
         stackedControl.next();
         onPageChange(getCurrentPage());
      }
   }

   private final class AddPage extends Action {
      public AddPage() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ADD_GREEN));
         setToolTipText("Adds a page");
      }

      @Override
      public void run() {
         if (stackedControl.getTotalPages() >= maxPage) {
            MessageDialog.openError(AWorkbench.getActiveShell(), "Add Attribute", "Already at maximum allowed.");
         } else {
            addPage();
         }
      }
   }

   private final class RemovePage extends Action {
      public RemovePage() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.X_RED));
         setToolTipText("Removes a page");
      }

      @Override
      public void run() {
         if (stackedControl.getTotalPages() <= minPage) {
            MessageDialog.openError(AWorkbench.getActiveShell(), "Remove Attribute", "Already at minimum allowed.");
         } else {
            try {
               stackedControl.removePage();
            } catch (OseeCoreException ex) {
               OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }

   final class StackedControl {
      private StackedViewer stackedViewer;
      private XStackedWidgetPage currentPage;
      private final List<XStackedWidgetPage> pages;

      public StackedControl() {
         this.stackedViewer = null;
         this.currentPage = null;
         this.pages = new ArrayList<>();
      }

      public XStackedWidgetPage getCurrentPage() {
         return currentPage;
      }

      public void clearCurrentPage() {
         currentPage = null;
      }

      private void createControl(Composite parent) {
         pages.clear();
         stackedViewer = new StackedViewer(parent, SWT.BORDER);
         stackedViewer.setLayout(ALayout.getZeroMarginLayout());
         GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
         gd.minimumHeight = 60;
         gd.minimumWidth = 60;
         stackedViewer.setLayoutData(gd);
         stackedViewer.setCurrentControl(StackedViewer.DEFAULT_CONTROL);
         stackedViewer.layout();
      }

      public void dispose() {
         pages.clear();
         Widgets.disposeWidget(stackedViewer);
      }

      public void refresh() {
         stackedViewer.getStackComposite().layout();
         stackedViewer.getStackComposite().getParent().layout();
      }

      private int getTotalPages() {
         return Widgets.isAccessible(stackedViewer) ? stackedViewer.getControlCount() : 0;
      }

      private int getCurrentPageIndex() {
         return pages.indexOf(currentPage) > 0 ? pages.indexOf(currentPage) : 0;
      }

      private void next() {
         XStackedWidgetPage next = null;
         Iterator<XStackedWidgetPage> iterator = pages.iterator();
         while (iterator.hasNext()) {
            XStackedWidgetPage page = iterator.next();
            if (page.getGuid().equals(currentPage.getGuid())) {
               if (iterator.hasNext()) {
                  next = iterator.next();
               } else if (pages.size() > 1) {
                  next = pages.iterator().next();
               }
            }
         }
         if (next != null) {
            setCurrentPage(next);
         }
      }

      private void previous() {
         XStackedWidgetPage previous = null;
         for (XStackedWidgetPage page : pages) {
            if (page.getGuid().equals(currentPage.getGuid())) {
               if (previous == null) {
                  if (pages.size() > 1) {
                     previous = pages.get(pages.size() - 1);
                  }
               }
               break;
            } else {
               previous = page;
            }
         }
         if (previous != null) {
            setCurrentPage(previous);
         }
      }

      public void setCurrentPage(XStackedWidgetPage page) {
         String pageId = null;
         setMessage(IStatus.OK, "");
         if (page == null) {
            setMessage(IStatus.ERROR, "Page not found.");
            pageId = StackedViewer.DEFAULT_CONTROL;
         } else {
            pageId = page.getGuid();
         }
         this.currentPage = page;
         stackedViewer.setCurrentControl(pageId);
         if (loadPageValues) {
            updatePageText(page);
         }
         updateCurrentPageLabel();
      }

      public void addPage(XStackedWidgetPage page) {
         XStackedWidgetPage existingPage = getPage(page.getGuid());
         if (existingPage == null) {
            Composite composite = new Composite(stackedViewer.getStackComposite(), SWT.WRAP);
            composite.setLayout(new GridLayout());
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            createPageWidget(page, composite);
            pages.add(page);
            stackedViewer.addControl(page.getGuid(), composite);
            setCurrentPage(page);
            validate();
            notifyXModifiedListeners();
         } else {
            setMessage(IStatus.WARNING, "Add page error - page at index [%s] already exists", page.getGuid());
         }
      }

      private XStackedWidgetPage getPage(String pageId) {
         for (XStackedWidgetPage page : pages) {
            if (page.getGuid().equals(pageId)) {
               return page;
            }
         }
         return null;
      }

      public void removeAllPages() {
         if (!pages.isEmpty()) {
            for (XStackedWidgetPage page : pages) {
               Control control = stackedViewer.removeControl(page.getGuid());
               Widgets.disposeWidget(control);
            }
            pages.clear();
            validate();
         }
      }

      private void removePage() {
         XStackedWidgetPage currentPage2 = getCurrentPage();
         boolean removed = pages.remove(currentPage2);
         if (removed) {
            onRemovePage(currentPage2);
            Control control = stackedViewer.removeControl(currentPage2.getGuid());
            Widgets.disposeWidget(control);
            previous();
            validate();
            notifyXModifiedListeners();
         } else {
            setMessage(IStatus.WARNING, "Remove page error - page at index [%s] does not exist", getCurrentPageIndex());
         }
      }

      public List<XStackedWidgetPage> getPages() {
         return pages;
      }

      public StackedViewer getStackedViewer() {
         return stackedViewer;
      }

   }

}