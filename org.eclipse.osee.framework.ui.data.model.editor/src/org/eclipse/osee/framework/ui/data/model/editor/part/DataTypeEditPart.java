/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Donald G. Dunne - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.data.model.editor.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditor;
import org.eclipse.osee.framework.ui.data.model.editor.figure.ODMFigureFactory;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.BaseModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.ModelElement;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMGraph;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart {

   private ODMEditor editor;

   public DataTypeEditPart(ODMEditor editor) {
      this.editor = editor;
   }

   //   protected void refreshVisuals() {
   //      Rectangle bounds = new Rectangle(0, 0, 220, 30);
   //      ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
   //   }

   private ConnectionAnchor anchor;

   /**
    * Upon activation, attach to the model element as a property change listener.
    */
   public void activate() {
      if (!isActive()) {
         super.activate();
         ((ModelElement) getModel()).addPropertyChangeListener(this);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
    */
   protected void createEditPolicies() {
      //      installEditPolicy(EditPolicy.COMPONENT_ROLE, new ShapeComponentEditPolicy());
   }

   /*(non-Javadoc)
    * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    */
   protected IFigure createFigure() {
      IFigure figure = null;
      Object model = getModel();
      if (model instanceof ArtifactDataType) {
         ODMGraph graph = (ODMGraph) ((GraphEditPart) getParent()).getModel();
         figure = ODMFigureFactory.createArtifactTypeFigure(graph, (ArtifactDataType) model);
      } else if (model instanceof AttributeDataType) {
         figure = ODMFigureFactory.createAttributeTypeFigure((AttributeDataType) model);
      } else if (model instanceof RelationDataType) {
         figure = ODMFigureFactory.createRelationTypeFigure((RelationDataType) model);
      }
      if (figure == null) {
         throw new RuntimeException("cannot create figure for " + model.getClass().getName() + " class");
      }

      figure.setOpaque(true);
      return figure;
   }

   /**
    * Upon deactivation, detach from the model element as a property change listener.
    */
   public void deactivate() {
      if (isActive()) {
         super.deactivate();
         ((ModelElement) getModel()).removePropertyChangeListener(this);
      }
   }

   private BaseModel getCastedModel() {
      return (BaseModel) getModel();
   }

   protected ConnectionAnchor getConnectionAnchor() {
      if (anchor == null) {
         if (getModel() instanceof BaseModel)
            anchor = new ChopboxAnchor(getFigure());
         else
            // if Shapes gets extended the conditions above must be updated
            throw new IllegalArgumentException("unexpected model");
      }
      return anchor;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
    */
   protected List getModelSourceConnections() {
      return getCastedModel().getSourceConnections();
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
    */
   protected List getModelTargetConnections() {
      return getCastedModel().getTargetConnections();
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
    */
   public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
      return getConnectionAnchor();
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
    */
   public ConnectionAnchor getSourceConnectionAnchor(Request request) {
      return getConnectionAnchor();
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
    */
   public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
      return getConnectionAnchor();
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
    */
   public ConnectionAnchor getTargetConnectionAnchor(Request request) {
      return getConnectionAnchor();
   }

   /* (non-Javadoc)
    * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
    */
   public void propertyChange(PropertyChangeEvent evt) {
      String prop = evt.getPropertyName();
      if (BaseModel.SIZE_PROP.equals(prop) || BaseModel.LOCATION_PROP.equals(prop)) {
         refreshVisuals();
      } else if (BaseModel.SOURCE_CONNECTIONS_PROP.equals(prop)) {
         refreshSourceConnections();
      } else if (BaseModel.TARGET_CONNECTIONS_PROP.equals(prop)) {
         refreshTargetConnections();
      }
   }

   protected void refreshVisuals() {
      // notify parent container of changed position & location
      // if this line is removed, the XYLayoutManager used by the parent container 
      // (the Figure of the ShapesDiagramEditPart), will not know the bounds of this figure
      // and will not draw it correctly.
      Rectangle bounds = new Rectangle(getCastedModel().getLocation(), getCastedModel().getSize());
      ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
   }
}