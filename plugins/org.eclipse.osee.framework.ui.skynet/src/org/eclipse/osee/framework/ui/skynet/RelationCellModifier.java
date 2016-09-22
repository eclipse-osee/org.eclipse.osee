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
package org.eclipse.osee.framework.ui.skynet;

import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Item;

/**
 * @author Ryan D. Brooks
 */
public class RelationCellModifier implements ICellModifier {
   private final TreeViewer treeViewer;
   private final IDirtiableEditor editor;

   public RelationCellModifier(TreeViewer treeViewer, IDirtiableEditor editor) {
      this.treeViewer = treeViewer;
      this.editor = editor;
   }

   @Override
   public boolean canModify(Object element, String property) {
      if (element instanceof WrapperForRelationLink) {
         WrapperForRelationLink relLink = (WrapperForRelationLink) element;
         RelationTypeSide rts = new RelationTypeSide(relLink.getRelationType(), relLink.getRelationSide());
         boolean canModify = false;
         AccessPolicy policyHandlerService = null;
         try {
            policyHandlerService = ServiceUtil.getAccessPolicy();
         } catch (OseeCoreException ex1) {
            OseeLog.log(Activator.class, Level.SEVERE, ex1);
         }
         if (policyHandlerService != null) {
            try {
               canModify = policyHandlerService.canRelationBeModified(relLink.getArtifactA(),
                  Arrays.asList(relLink.getArtifactB()), rts, Level.INFO).matched();
            } catch (OseeCoreException ex) {
               canModify = false;
            }
         }
         return canModify;
      }
      return false;
   }

   @Override
   public Object getValue(Object element, String property) {
      WrapperForRelationLink relLink = (WrapperForRelationLink) element;
      RelationLink link = getRelationLink(relLink);
      return link != null ? link.getRationale() : Strings.emptyString();
   }

   @Override
   public void modify(Object element, String property, Object value) {
      // Note that it is possible for an SWT Item to be passed instead of the model element.
      if (element instanceof Item) {
         element = ((Item) element).getData();
      }
      WrapperForRelationLink relLink = (WrapperForRelationLink) element;
      RelationLink link = getRelationLink(relLink);
      if (link != null) {
         link.setRationale(value.toString());
      }
      treeViewer.update(element, null);
      editor.onDirtied();
   }

   private RelationLink getRelationLink(WrapperForRelationLink relLink) {
      try {
         return RelationManager.getRelationLink(relLink.getArtifactA(), relLink.getArtifactB(),
            relLink.getRelationType());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }
}
