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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerTextFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class FilterDataUI {

   private Text filterText;
   private final XViewerTextFilter xViewerFilter;
   private Label filterLabel;
   private CustomizeData custData;
   private final XViewer xViewer;

   public FilterDataUI(XViewer xViewer) {
      this.xViewer = xViewer;
      this.xViewerFilter = new XViewerTextFilter(xViewer);
   }

   public void createWidgets(Composite comp) {
      Label label = new Label(comp, SWT.NONE);
      label.setText("Filter:");
      label.setToolTipText("Type string and press enter to filter.\nClear field to un-filter.");
      GridData gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      label.setLayoutData(gd);

      filterText = new Text(comp, SWT.SINGLE | SWT.BORDER);
      gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      gd.widthHint = 100;
      filterText.setLayoutData(gd);

      filterText.addKeyListener(new KeyListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
          */
         public void keyPressed(KeyEvent e) {
         }

         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
          */
         public void keyReleased(KeyEvent e) {
            // System.out.println(e.keyCode);
            custData.getFilterData().setFilterText(filterText.getText());
            if (filterText.getText().equals("") || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) refresh();
         }
      });

      filterLabel = new Label(comp, SWT.NONE);
      filterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
      filterLabel.addListener(SWT.MouseUp, new Listener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         public void handleEvent(Event event) {
            custData.getFilterData().setFilterText("");
            refresh();
         }
      });
   }

   public void dispose() {
   }

   public String getStatusLabelAddition() {
      if (isXViewerTextFiltered()) return "Text FILTERED - ";
      return "";
   }

   private void refresh() {
      if (xViewer.getTree() == null || xViewer.getTree().isDisposed()) return;
      if (custData.getFilterData().getFilterText().equals("")) {
         xViewer.removeFilter(xViewerFilter);
         if (filterLabel != null) {
            filterLabel.setImage(null);
            filterLabel.getParent().layout();
         }
      } else {
         if (!isXViewerTextFiltered()) xViewer.addFilter(xViewerFilter);
         xViewerFilter.setFilterText(custData.getFilterData().getFilterText());
         if (filterLabel != null) {
            if (SkynetGuiPlugin.getInstance() != null) filterLabel.setImage(SkynetGuiPlugin.getInstance().getImage(
                  "clear.gif"));
            filterLabel.setText("clear");
            filterLabel.getParent().layout();
         }
      }
      if (filterText != null) filterText.setText(custData.getFilterData().getFilterText());
      xViewer.refresh();
   }

   private boolean isXViewerTextFiltered() {
      for (ViewerFilter filter : xViewer.getFilters()) {
         if (filter instanceof XViewerTextFilter) return true;
      }
      return false;
   }

   /**
    * @return the custData
    */
   public CustomizeData getCustData() {
      return custData;
   }

   /**
    * @param custData the custData to set
    */
   public void setCustData(CustomizeData custData) {
      this.custData = custData;
      refresh();
   }

}
