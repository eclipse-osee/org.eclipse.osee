/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.mbse.cameo.browser;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.ProjectWindowsManager;
import com.nomagic.magicdraw.ui.WindowComponentInfo;
import com.nomagic.magicdraw.ui.browser.Browser;
import com.nomagic.magicdraw.ui.browser.WindowComponent;
import com.nomagic.magicdraw.ui.browser.WindowComponentContent;
import com.nomagic.ui.ExtendedPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

/**
 * @author David W. Miller
 */
public class OSEEPanelPlugin extends Plugin {

   @SuppressWarnings("ConstantConditions")
   private static final WindowComponentInfo info =
      new WindowComponentInfo("org.eclipse.osee.mbse.cameo.browser", "OSEE Branch Selection", null, //icon
         ProjectWindowsManager.SIDE_WEST, ProjectWindowsManager.STATE_DOCKED, true);

   @Override
   public void init() {
      Browser.addBrowserInitializer(new Browser.BrowserInitializer() {
         @Override
         public void init(Browser browser, Project project) {
            browser.addPanel(new BrowserPanel());
         }

         @Override
         public WindowComponentInfoRegistration getInfo() {
            return new WindowComponentInfoRegistration(info, null);
         }
      });
   }

   @Override
   public boolean close() {
      return true;
   }

   @Override
   public boolean isSupported() {
      return true;
   }

   /**
    * OSEE Branch Browser Panel
    */
   private class BrowserPanel extends ExtendedPanel implements WindowComponent {
      /**
       *
       */
      private static final long serialVersionUID = 3579367897567548297L;

      /**
       * Constructor.
       */
      public BrowserPanel() {

         JScrollPane scrollPane;
         JTable branchTable;
         JTextField filter;
         JButton setBranchProp;
         JLabel label;

         List<BranchData> data = ProjectBranchUtility.getBranchData();

         BranchTableModel model = new BranchTableModel(data);
         // setbackground of panel
         this.setBackground(Color.LIGHT_GRAY);

         GridBagLayout gbl_p = new GridBagLayout();
         gbl_p.columnWidths = new int[] {300};
         gbl_p.rowHeights = new int[] {330, 20, 20, 20};
         gbl_p.columnWeights = new double[] {Double.MIN_VALUE};
         gbl_p.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0};
         this.setLayout(gbl_p);

         scrollPane = new JScrollPane();
         this.add(scrollPane, gbcHelper(0));

         branchTable = new JTable(model);
         branchTable.setCellSelectionEnabled(true);
         scrollPane.setViewportView(branchTable);
         TableRowSorter<BranchTableModel> sorter = new TableRowSorter<BranchTableModel>(model);
         branchTable.setRowSorter(sorter);

         setBranchProp = new JButton("Set Selected Branch");
         setBranchProp.setVerticalAlignment(SwingConstants.BOTTOM);
         this.add(setBranchProp, gbcHelper(1));

         setBranchProp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               int location = branchTable.convertRowIndexToModel(branchTable.getSelectedRow());
               BranchData data = model.getBranchData(location);
               ProjectBranchUtility branchData = new ProjectBranchUtility();
               ProjectBranchUtility.setProjectProperty(data);
               Application.getInstance().getGUILog().showMessage(ProjectBranchUtility.listBranchProperties());
            }
         });
         filter = new JTextField();
         this.add(filter, gbcHelper(2));
         filter.setColumns(10);

         label = new JLabel("Input branch filter text above");
         label.setHorizontalAlignment(SwingConstants.CENTER);
         this.add(label, gbcHelper(3));

         filter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
               newFilter(filter, sorter);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
               newFilter(filter, sorter);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
               newFilter(filter, sorter);
            }
         });
      }

      private void newFilter(JTextField textField, TableRowSorter<BranchTableModel> sorter) {
         RowFilter<BranchTableModel, Object> rf = null;
         try {
            rf = RowFilter.regexFilter(textField.getText(), 0);
         } catch (java.util.regex.PatternSyntaxException e) {
            return;
         }
         sorter.setRowFilter(rf);
      }

      private GridBagConstraints gbcHelper(int row) {
         // assumes only one column (to match this panel
         // fill constraints 0 - Horizontal Fill, 1, Fill Both
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.fill = GridBagConstraints.BOTH;
         gbc.insets = new Insets(0, 0, 5, 0);
         gbc.gridx = 0;
         gbc.gridy = row;
         return gbc;
      }

      @Override
      public WindowComponentInfo getInfo() {
         return info;
      }

      @Override
      public WindowComponentContent getContent() {
         return new BrowserWindowComponentContext(this);
      }
   }

   /**
    * Real component which is added to window.
    */
   private static class BrowserWindowComponentContext implements WindowComponentContent {
      private final JPanel panel;

      /**
       * Constructor.
       *
       * @param panel which will be added to window.
       */
      public BrowserWindowComponentContext(JPanel panel) {
         this.panel = panel;
      }

      @Override
      public Component getWindowComponent() {
         return panel;
      }

      @Override
      public Component getDefaultFocusComponent() {
         return panel;
      }
   }
}
