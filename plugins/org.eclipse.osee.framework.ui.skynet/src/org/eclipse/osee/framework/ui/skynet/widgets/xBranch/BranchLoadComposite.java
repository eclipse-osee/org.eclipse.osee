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
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchQueryData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class BranchLoadComposite extends Composite {

   private final BranchView branchView;
   private final BranchQueryData branchData;
   private XText nameText;
   private Button asIdButton;

   public BranchLoadComposite(BranchView branchView, Composite parent, int style) {
      super(parent, style);
      this.branchView = branchView;
      setLayout(ALayout.getZeroMarginLayout(8, false));
      setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      this.branchData = branchView.getBranchData();

      Button favoritesButton = new Button(this, SWT.PUSH);
      favoritesButton.setText("Favorites");
      favoritesButton.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            handleFavorites();
         }
      });

      (new Label(this, SWT.NONE)).setText(" or ");

      Button searchButton = new Button(this, SWT.PUSH);
      searchButton.setText("Search");
      searchButton.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            branchView.handleQuerySearch();
         }
      });

      nameText = new XText("Srch String: ");
      nameText.setToolTip("Search Branch names for String");
      nameText.createWidgets(this, 1);
      nameText.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            setSearchString();
         }
      });
      nameText.getStyledText().addKeyListener(new KeyListener() {

         @Override
         public void keyReleased(KeyEvent e) {
            if (e.character == '\r') {
               branchView.handleQuerySearch();
               return;
            }
         }

         @Override
         public void keyPressed(KeyEvent e) {
            // do nothing
         }
      });

      asIdButton = new Button(this, SWT.TOGGLE);
      asIdButton.setToolTipText("Search by long id in search str. Other criteria is ignored.\n" //
         + "Note: Will not show Archived or Deleted unless OSEE Admin.");
      asIdButton.setImage(ImageManager.getImage(FrameworkImage.ID));
      asIdButton.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            boolean byId = asIdButton.getSelection();
            branchData.setAsIds(byId);
            refreshNameText();
         }

      });

      Button resetButton = new Button(this, SWT.PUSH);
      resetButton.setText("Reset");
      resetButton.setToolTipText("Reset search criteria to defaults");
      resetButton.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            nameText.setText("");
            asIdButton.setSelection(false);
            refreshNameText();
            branchView.getXBranchWidget().resetButtons();
            branchView.loadData(Collections.emptyList());
         }
      });

      Button showSearchDataButton = new Button(this, SWT.PUSH);
      showSearchDataButton.setImage(ImageManager.getImage(FrameworkImage.GEAR));
      showSearchDataButton.setToolTipText("Show Branch Query Data");
      showSearchDataButton.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            BranchQueryData showQueryData = branchView.getShowQueryData();
            ResultsEditor.open("Branch Query Data", "Branch Query Data", showQueryData.getHtml());
         }
      });

   }

   private void refreshNameText() {
      boolean byId = asIdButton.getSelection();
      if (byId) {
         nameText.setLabel("Srch Id: ");
      } else {
         nameText.setLabel("Srch String: ");
      }
      nameText.getLabelWidget().getParent().layout(true);
      setSearchString();
   }

   private void setSearchString() {
      if (Strings.isValid(nameText.getText())) {
         if (branchData.isAsIds()) {
            branchData.setNamePatternIgnoreCase("");
            branchData.setNamePattern(nameText.getText());
         } else {
            branchData.setNamePatternIgnoreCase("%" + nameText.getText() + "%");
            branchData.setNamePattern("");
         }
      } else {
         branchData.setNamePatternIgnoreCase("");
         branchData.setNamePattern(".*");
      }
   }

   protected void handleFavorites() {
      BranchQueryData branchData = new BranchQueryData();
      Collection<String> attributes =
         UserManager.getUser().getAttributesToStringList(CoreAttributeTypes.FavoriteBranch);
      for (String value : attributes) {
         try {
            branchData.getBranchIds().add(BranchId.valueOf(value));
         } catch (Exception ex) {
            // do nothing
         }
      }
      if (branchData.getBranchIds().isEmpty()) {
         AWorkbench.popup("No Branches Marked as Favorite");
         branchView.loadData(Collections.emptyList());
      } else {
         OseeClient oseeClient = ServiceUtil.getOseeClient();
         BranchEndpoint endpoint = oseeClient.getBranchEndpoint();
         List<Branch> branches = new LinkedList<>();
         for (BranchId branchId : branchData.getBranchIds()) {
            Branch branch = endpoint.getBranchById(branchId);
            if (branch.isValid()) {
               branches.add(branch);
            }
         }
         branchView.loadData(branches);
      }
   }

}
