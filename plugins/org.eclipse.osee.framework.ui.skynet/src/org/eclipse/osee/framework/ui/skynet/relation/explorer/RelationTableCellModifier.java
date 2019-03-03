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
package org.eclipse.osee.framework.ui.skynet.relation.explorer;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.swt.widgets.TableItem;

public class RelationTableCellModifier implements ICellModifier {
   private final RelationTableViewer relationTableViewer;

   public RelationTableCellModifier(RelationTableViewer relationTableViewer) {
      super();
      this.relationTableViewer = relationTableViewer;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
    */
   @Override
   public boolean canModify(Object element, String property) {
      // Find the index of the column
      int columnIndex = relationTableViewer.getColumnNames().indexOf(property);

      ArtifactModel model = (ArtifactModel) element;
      switch (columnIndex) {
         case RelationTableViewer.ARTIFACT_NAME_NUM:
            if (model.isArtifactFound()) {
               return false;
            }
            break;
         case RelationTableViewer.ARTIFACT_TYPE_NUM:
            if (model.isArtifactFound()) {
               return false;
            }
            break;
      }
      return true;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
    */
   @Override
   public Object getValue(Object element, String property) {

      // Find the index of the column
      int columnIndex = relationTableViewer.getColumnNames().indexOf(property);

      Object result = null;
      ArtifactModel model = (ArtifactModel) element;

      switch (columnIndex) {
         case RelationTableViewer.ADD_NUM:
            result = new Boolean(model.isAdd());
            break;
         case RelationTableViewer.ARTIFACT_NAME_NUM:
            result = new String(model.getName());
            break;
         case RelationTableViewer.ARTIFACT_TYPE_NUM:
            result = model.getDescriptor();
            break;
         case RelationTableViewer.RATIONALE_NUM:
            result = new String(model.getRationale());
            break;
         default:
            result = "";
      }
      return result;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
    */
   @Override
   public void modify(Object element, String property, Object value) {

      // Find the index of the column
      int columnIndex = relationTableViewer.getColumnNames().indexOf(property);

      TableItem item = (TableItem) element;
      ArtifactModel model = (ArtifactModel) item.getData();

      switch (columnIndex) {
         case RelationTableViewer.ADD_NUM:
            model.setAdd(((Boolean) value).booleanValue());
            break;
         case RelationTableViewer.ARTIFACT_NAME_NUM:
            if (!model.isArtifactFound()) {
               model.setName((String) value);
            }
            break;
         case RelationTableViewer.ARTIFACT_TYPE_NUM:
            if (!model.isArtifactFound()) {
               model.setDescriptor((ArtifactTypeToken) value);
            }
            break;
         case RelationTableViewer.RATIONALE_NUM:
            model.setRationale((String) value);
            break;
         default:
      }
      relationTableViewer.getArtifactList().artifactChanged(model);
      relationTableViewer.refresh();
   }
}