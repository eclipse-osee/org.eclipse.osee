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
package org.eclipse.osee.framework.ui.skynet.search.page.actions;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.search.page.ArtifactSearchComposite;
import org.eclipse.osee.framework.ui.skynet.search.page.manager.IDataListener;
import org.eclipse.swt.widgets.Display;

public class BranchRevisionListener implements IDataListener {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchRevisionListener.class);
   private static final ConfigurationPersistenceManager configurationManager =
         ConfigurationPersistenceManager.getInstance();
   private ArtifactSearchComposite parentWindow;

   public BranchRevisionListener(ArtifactSearchComposite parentWindow) {
      this.parentWindow = parentWindow;
      this.parentWindow.addRevisionDataListener(this);
   }

   public void dataChanged() {
      System.out.println("Branch/Revision Changed");
      refreshTypeList();
   }

   synchronized private void refreshTypeList() {
      Display.getCurrent().asyncExec(new Runnable() {
         public void run() {
            int branch =
                  parentWindow.getRevisionWidget().getBranchId(parentWindow.getRevisionDataManager().getBranchName());
            int revision = parentWindow.getRevisionDataManager().getRevision();

            Collection<ArtifactSubtypeDescriptor> descriptors = null;
            if (branch > 0 && revision > 0) {
               try {
                  descriptors =
                        configurationManager.getArtifactSubtypeDescriptors(BranchPersistenceManager.getInstance().getBranch(
                              branch));
               } catch (SQLException ex) {
                  logger.log(Level.SEVERE, ex.toString(), ex);
               }
            }

            if (descriptors != null) {
               parentWindow.getListWidget().getListViewer().setInput(descriptors);
               parentWindow.getTreeWidget().getInputManager().removeAll();
            } else {
               parentWindow.getListWidget().getListViewer().setInput(null);
               parentWindow.getTreeWidget().getInputManager().removeAll();
            }
         }
      });
   }
}
