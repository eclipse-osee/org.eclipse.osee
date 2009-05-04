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
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.StackedViewer;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public abstract class XStackedWidget<T> extends XLabel {

   private StackedControl stackedControl;
   private StyledText currentPageLabel;
   private Composite container;
   private Label messageLabel;
   private Label messageIcon;
   private int minPage;
   private int maxPage;

   public XStackedWidget(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
      setToolTip("Navigate pages by clicking forward and backward buttons.");
      minPage = 0;
      maxPage = 0;
   }

   public void dispose() {
      stackedControl.dispose();
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
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XLabel#getControl()
    */
   @Override
   public Control getControl() {
      return stackedControl.stackedViewer;
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

      Composite composite = new Composite(container, SWT.NONE);
      GridLayout layout1 = new GridLayout(1, false);
      layout1.marginHeight = 0;
      layout1.marginWidth = 0;
      layout1.verticalSpacing = 0;
      layout1.horizontalSpacing = 0;
      composite.setLayout(layout1);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      createToolBar(composite);
      stackedControl = new StackedControl();
      stackedControl.createControl(composite);
      createMessageArea(composite);

      addToolTip(container, getToolTip());
      stackedControl.next();
      refresh();
   }

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
      messageArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      messageIcon = new Label(messageArea, SWT.NONE);
      messageIcon.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

      messageLabel = new Label(messageArea, SWT.NONE);
      messageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
   }

   private void createToolBar(Composite parent) {
      Composite composite = new Composite(parent, SWT.BORDER);
      GridLayout layout = new GridLayout(3, false);
      layout.marginHeight = 0;
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
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            if (Widgets.isAccessible(currentPageLabel)) {
               int totalPages = stackedControl.getTotalPages();
               int currentPage = stackedControl.getCurrentPageIndex() + 1;
               if (currentPage > totalPages) {
                  currentPage = totalPages;
               }
               currentPageLabel.setText(String.format("%s of %s", currentPage, totalPages));
            }
         }
      });
   }

   public void addPage(T value) {
      stackedControl.addPage(value);
   }

   protected String getCurrentPageId() {
      return stackedControl.getCurrentPageId();
   }

   private void setMessage(final int severity, final String message) {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            if (Widgets.isAccessible(messageLabel)) {
               Composite parent = messageLabel.getParent();

               String text = message;
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
                     imageName = null;
                     break;
               }
               Image image =
                     Strings.isValid(imageName) ? PlatformUI.getWorkbench().getSharedImages().getImage(imageName) : null;
               messageIcon.setImage(image);
               messageLabel.setText(isVisible ? " " + text : text);

               messageIcon.setVisible(isVisible);
               messageLabel.setVisible(isVisible);
               parent.setVisible(isVisible);
               parent.layout();
            }
         }
      });
   }

   protected abstract void createPage(String id, Composite parent, T value);

   protected abstract void onRemovePage(String id);

   private final class Back extends Action {
      public Back() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("nav_backward.gif"));
         setToolTipText("Back to previous page");
      }

      public void run() {
         stackedControl.previous();
      }
   }

   private final class Forward extends Action {
      public Forward() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("nav_forward.gif"));
         setToolTipText("Forward to next page");
      }

      public void run() {
         stackedControl.next();
      }
   }

   private final class AddPage extends Action {
      public AddPage() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("add.gif"));
         setToolTipText("Adds a page");
      }

      public void run() {
         stackedControl.addPage((T) null);
      }
   }

   private final class RemovePage extends Action {
      public RemovePage() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("remove.gif"));
         setToolTipText("Removes a page");
      }

      public void run() {
         stackedControl.removePage();
      }
   }

   private final class StackedControl {
      private StackedViewer stackedViewer;
      private int currentPage;
      private final List<String> pageIds;

      public StackedControl() {
         this.stackedViewer = null;
         this.currentPage = -1;
         this.pageIds = new ArrayList<String>();
      }

      private void createControl(Composite parent) {
         pageIds.clear();
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
         pageIds.clear();
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
         return currentPage;
      }

      private String getCurrentPageId() {
         String toReturn = null;
         int index = getCurrentPageIndex();
         if (index >= 0 && index < pageIds.size()) {
            toReturn = pageIds.get(index);
         }
         return toReturn;
      }

      private void next() {
         int next = getCurrentPageIndex();
         if (next + 1 < getTotalPages()) {
            next++;
         } else {
            next = 0;
         }
         setCurrentPage(next);
      }

      private void previous() {
         int previous = getCurrentPageIndex();
         if (previous - 1 >= 0) {
            previous--;
         } else {
            previous = getTotalPages() - 1;
         }
         setCurrentPage(previous);
      }

      public void setCurrentPage(int index) {
         String pageId = null;
         setMessage(IStatus.OK, "");
         if (index >= 0 && index < pageIds.size()) {
            pageId = pageIds.get(index);
            if (pageId == null) {
               setMessage(IStatus.ERROR, String.format("Page [%s] not found.", index));
            }
         } else {
            setMessage(IStatus.ERROR, String.format("Page [%s] out of bounds.", index));
         }

         if (pageId == null) {
            index = 0;
            pageId = StackedViewer.DEFAULT_CONTROL;
         }
         this.currentPage = index;
         stackedViewer.setCurrentControl(pageId);
         updateCurrentPageLabel();
      }

      private void addPage(T value) {
         int numberOfPages = getTotalPages();
         IStatus status = validate(numberOfPages + 1);
         if (status.isOK()) {
            setMessage(IStatus.OK, "");
            String id = GUID.generateGuidStr();
            if (pageIds.add(id)) {
               Composite composite = new Composite(stackedViewer.getStackComposite(), SWT.WRAP);
               composite.setLayout(new GridLayout());
               composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

               createPage(id, composite, value);
               stackedViewer.addControl(id, composite);
               setCurrentPage(numberOfPages);
               notifyXModifiedListeners();
            } else {
               setMessage(IStatus.WARNING, String.format("Add page error - page at index [%s] already exists",
                     getCurrentPageIndex()));
            }
         } else {
            setMessage(IStatus.ERROR, status.getMessage());
         }
      }

      private void removePage() {
         int numberOfPages = getTotalPages();
         IStatus status = validate(numberOfPages - 1);
         if (status.isOK()) {
            setMessage(IStatus.OK, "");
            System.out.println("Delete Page");

            String pageId = pageIds.remove(getCurrentPageIndex());
            if (pageId != null) {
               onRemovePage(pageId);
               Control control = stackedViewer.removeControl(pageId);
               Widgets.disposeWidget(control);
               previous();
               notifyXModifiedListeners();
            } else {
               setMessage(IStatus.WARNING, String.format("Remove page error - page at index [%s] does not exist",
                     getCurrentPageIndex()));
            }
         } else {
            setMessage(IStatus.ERROR, status.getMessage());
         }
      }

      private IStatus validate(int numberOfPages) {
         IStatus status = null;
         if (minPage <= numberOfPages && maxPage >= numberOfPages) {
            status = Status.OK_STATUS;
         } else {
            List<String> message = new ArrayList<String>();
            if (numberOfPages < minPage) {
               message.add(String.format("Must have at least [%s] page%s", minPage, minPage == 1 ? "" : "s"));
            }
            if (numberOfPages > maxPage) {
               message.add(String.format("Can't add more than [%s] page%s", maxPage, maxPage == 1 ? "" : "s"));
            }
            status = new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, Collections.toString(" &", message));
         }
         return status;
      }
   }

}
