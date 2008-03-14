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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Jeff C. Phillips
 */
public class RelationTypeContentProvider implements ITreeContentProvider {
   protected static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RelationTypeContentProvider.class);
   private static final RelationPersistenceManager relationPersistenceManager =
         RelationPersistenceManager.getInstance();

   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Branch) {
         ArrayList<Object> descriptors = new ArrayList<Object>();
         try {
			for (IRelationLinkDescriptor descriptor : relationPersistenceManager.getIRelationLinkDescriptors((Branch) parentElement)) {
			    descriptors.add((Object) descriptor);
			 }
			 return descriptors.toArray();
		} catch (SQLException ex) {
			OSEELog.logException(SkynetGuiPlugin.class, ex, true);
		}
      }
      return null;
   }

   public Object getParent(Object element) {
      return null;
   }

   public boolean hasChildren(Object element) {
      return false;
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }
}
