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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public abstract class XFlatWidget<T> extends XLabel {

   private FlatControl flatControl;
   private StyledText currentPageLabel;
   private Composite container;
   private Label messageLabel;
   private Label messageIcon;
   private int minPage;
   private int maxPage;

   public XFlatWidget(String displayLabel) {
      super(displayLabel);
      setToolTip("Navigate pages by clicking forward and backward buttons.");
      minPage = 0;
      maxPage = 0;
   }

   @Override
   public void dispose() {
      flatControl.dispose();
      super.dispose();
   }

   public Collection<String> getPageIds() {
      return flatControl.pageIds;
   }

   protected void setPageRange(int minPage, int maxPage) {
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
   public Control getControl() {
      return flatControl.flatComposite;
   }

   @Override
   public void setToolTip(String toolTip) {
      if (Strings.isValid(toolTip)) {
         super.setToolTip(toolTip);
      }
   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(flatControl.flatComposite)) {
         updateCurrentPageLabel();
         flatControl.refresh();
      }
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
      flatControl = new FlatControl();
      flatControl.createControl(composite);
      createMessageArea(composite);

      addToolTip(container, getToolTip());
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
      messageArea.setBackground(Displays.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

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
      currentPageLabel.setText("0 Items");

      Composite filler = new Composite(composite, SWT.NONE);
      GridLayout layout1 = new GridLayout(1, false);
      filler.setLayout(layout1);
      filler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      ToolBar toolbar = new ToolBar(composite, SWT.FLAT | SWT.HORIZONTAL);
      toolbar.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
      ToolBarManager manager = new ToolBarManager(toolbar);
      manager.add(new Separator());
      manager.add(new AddPage());
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

   protected int getTotalItems() {
      return flatControl.getTotalPages();
   }

   private void updateCurrentPageLabel() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(currentPageLabel)) {
               int totalPages = getTotalItems();
               currentPageLabel.setText(String.format("%s Items", totalPages));
            }
         }
      });
   }

   public void addPage(T value) {
      flatControl.addPage(value);
   }

   protected void setMessage(final int severity, final String message) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
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

   private final class AddPage extends Action {
      public AddPage() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ADD_GREEN));
         setToolTipText("Adds a page");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void run() {
         Object obj = null;
         T object = (T) obj;
         flatControl.addPage(object);
      }
   }

   private final class FlatControl {
      private final List<String> pageIds;
      private Composite flatComposite;
      private final Map<String, Composite> pages = new HashMap<>();

      public FlatControl() {
         this.pageIds = new ArrayList<>();
      }

      private void createControl(Composite parent) {
         pageIds.clear();
         flatComposite = new Composite(parent, SWT.BORDER);
         flatComposite.setLayout(ALayout.getZeroMarginLayout());
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
         gd.minimumWidth = 60;
         flatComposite.setLayoutData(gd);
         flatComposite.layout();
      }

      public void dispose() {
         pageIds.clear();
         Widgets.disposeWidget(flatComposite);
      }

      public void refresh() {
         flatComposite.layout();
         flatComposite.getParent().layout();
      }

      private int getTotalPages() {
         return Widgets.isAccessible(flatComposite) ? pages.size() : 0;
      }

      private void addPage(T value) {
         int numberOfPages = getTotalPages();
         IStatus status = validate(numberOfPages + 1);
         if (status.isOK()) {
            setMessage(IStatus.OK, "");
            final String pageId = GUID.create();
            if (pageIds.add(pageId)) {
               Composite composite = new Composite(flatComposite, SWT.WRAP | SWT.BORDER);
               composite.setLayout(new GridLayout(3, false));
               composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

               createPage(pageId, composite, value);

               Button button = new Button(composite, SWT.FLAT);
               button.setImage(ImageManager.getImage(FrameworkImage.DELETE));
               button.addSelectionListener(new SelectionAdapter() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                     removePage(pageId);
                  }
               });

               pages.put(pageId, composite);
               notifyXModifiedListeners();
            } else {
               setMessage(IStatus.WARNING, "Add page error");
            }
         } else {
            setMessage(IStatus.ERROR, status.getMessage());
         }
      }

      private void removePage(String pageId) {
         int numberOfPages = getTotalPages();
         IStatus status = validate(numberOfPages - 1);
         if (status.isOK()) {
            setMessage(IStatus.OK, "");

            pageIds.remove(pageId);
            if (pageId != null) {
               onRemovePage(pageId);
               Control control = pages.remove(pageId);
               Widgets.disposeWidget(control);
               notifyXModifiedListeners();
            } else {
               setMessage(IStatus.WARNING, "Remove page error");
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
            List<String> message = new ArrayList<>();
            if (numberOfPages < minPage) {
               message.add(String.format("Must have at least [%s] page%s", minPage, minPage == 1 ? "" : "s"));
            }
            if (numberOfPages > maxPage) {
               message.add(String.format("Can't add more than [%s] page%s", maxPage, maxPage == 1 ? "" : "s"));
            }
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, Collections.toString(" &", message));
         }
         return status;
      }
   }

}
