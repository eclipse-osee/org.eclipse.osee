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
package org.eclipse.osee.framework.ui.data.model.editor.part;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.osee.framework.ui.data.model.editor.command.DeleteCommand;
import org.eclipse.osee.framework.ui.data.model.editor.figure.SelectableLabel;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.IModelListener;
import org.eclipse.osee.framework.ui.data.model.editor.model.NodeModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMDiagram;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ContainerModel;
import org.eclipse.osee.framework.ui.data.model.editor.policy.LabelSelectionEditPolicy;
import org.eclipse.osee.framework.ui.data.model.editor.property.PropertySourceFactory;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseEditPart extends AbstractGraphicalEditPart {

   protected DirectEditManager manager;

   protected IModelListener modelListener = new IModelListener() {

      @Override
      public void onModelEvent(Object object) {
         handleModelEvent(object);
      }
   };

   public BaseEditPart(NodeModel obj) {
      super();
      setModel(obj);
   }

   protected abstract void handleModelEvent(Object object);

   public void activate() {
      super.activate();
      ((NodeModel) getModel()).addListener(modelListener);
   }

   protected abstract DirectEditPolicy createDirectEditPolicy();

   public boolean canDeleteFromDiagram() {
      return false;
   }

   protected void createEditPolicies() {
      installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, createDirectEditPolicy());
      installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new LabelSelectionEditPolicy());
      installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
         protected Command createDeleteCommand(GroupRequest deleteRequest) {
            Command toReturn = UnexecutableCommand.INSTANCE;

            Object model = ((AbstractGraphicalEditPart) getParent()).getModel();
            if (model != null) {
               Boolean booleanObject =
                     (Boolean) deleteRequest.getExtendedData().get(DeleteCommand.DELETE_FROM_ODM_DIAGRAM);
               boolean isDeleteFromDiagram = booleanObject == null ? false : booleanObject.booleanValue();

               if (model instanceof ContainerModel) {
                  DeleteCommand cmd = new DeleteCommand();
                  cmd.setPartToBeDeleted(getHost().getModel(), ((ContainerModel) model).getArtifact(),
                        isDeleteFromDiagram);
                  toReturn = cmd;
               } else if (model instanceof ODMDiagram && getHost().getModel() instanceof ArtifactDataType) {
                  DeleteCommand cmd = new DeleteCommand();
                  cmd.setPartToBeDeleted(getHost().getModel(), model, isDeleteFromDiagram);
                  toReturn = cmd;
               }
            }
            return toReturn;
         }
      });
   }

   protected IFigure createFigure() {
      IFigure fig = new SelectableLabel();
      fig.setBorder(new MarginBorder(0, 1, 0, 0));
      return fig;
   }

   public void deactivate() {
      ((NodeModel) getModel()).removeListener(modelListener);
      super.deactivate();
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class key) {
      if (IPropertySource.class == key) {
         return PropertySourceFactory.getPropertySource(getModel());
      }
      return super.getAdapter(key);
   }

   IFigure getDirectEditFigure() {
      return getFigure();
   }

   String getDirectEditText() {
      IFigure fig = getDirectEditFigure();
      if (fig instanceof Label) {
         return ((Label) fig).getText();
      } else if (fig instanceof TextFlow) {
         return ((TextFlow) fig).getText();
      }
      return "";
   }

   public DragTracker getDragTracker(Request request) {
      return new SelectEditPartTracker(this);
   }

   protected void performDirectEdit() {
      if (manager == null) {
         manager = new LabelDirectEditManager(this, new LabelCellEditorLocator(getDirectEditFigure()));
      }
      manager.show();
   }

   public void performRequest(Request request) {
      if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
         performDirectEdit();
      } else {
         super.performRequest(request);
      }
   }

   private final class LabelCellEditorLocator implements CellEditorLocator {

      protected IFigure fig;

      public LabelCellEditorLocator(IFigure figure) {
         fig = figure;
      }

      public void relocate(CellEditor celleditor) {
         Text text = (Text) celleditor.getControl();

         Rectangle rect = fig.getClientArea(Rectangle.SINGLETON);
         if (fig instanceof Label) {
            rect = ((Label) fig).getTextBounds().intersect(rect);
         }
         fig.translateToAbsolute(rect);

         org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
         rect.translate(trim.x, trim.y);
         rect.width += trim.width;
         rect.height += trim.height;

         text.setBounds(rect.x, rect.y, rect.width, rect.height);
      }
   }
}
