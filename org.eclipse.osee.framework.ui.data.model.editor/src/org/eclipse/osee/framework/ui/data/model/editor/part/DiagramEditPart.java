package org.eclipse.osee.framework.ui.data.model.editor.part;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.IModelListener;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMDiagram;
import org.eclipse.osee.framework.ui.data.model.editor.policy.DiagramLayoutEditPolicy;
import org.eclipse.swt.SWT;

/**
 * @author Roberto E. Escobar
 */
public class DiagramEditPart extends AbstractGraphicalEditPart implements LayerConstants {

   protected IModelListener modelListener = new IModelListener() {

      @Override
      public void onModelEvent(Object object) {
         handleModelEvent(object);
      }
   };

   public DiagramEditPart(Object model) {
      super();
      setModel((ODMDiagram) model);
   }

   public void activate() {
      super.activate();
      ((ODMDiagram) getModel()).addListener(modelListener);
   }

   protected IFigure createFigure() {
      Figure f = new FreeformLayer();
      f.setBorder(new MarginBorder(5));
      f.setLayoutManager(new FreeformLayout());

      ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
      FanRouter router = new FanRouter();
      router.setSeparation(20);
      router.setNextRouter(new ShortestPathConnectionRouter(f));
      connLayer.setConnectionRouter(router);
      connLayer.setAntialias(SWT.ON);

      return f;
   }

   protected void createEditPolicies() {
      installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
      installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
      installEditPolicy("Snap Feedback", new SnapFeedbackPolicy());
   }

   public void deactivate() {
      ((ODMDiagram) getModel()).removeListener(modelListener);
      super.deactivate();
   }

   protected void handleModelEvent(Object object) {
      refreshVisuals();
      refreshChildren();
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (SnapToHelper.class == adapter) {
         Object snapPropertObject = getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
         return Boolean.TRUE.equals(snapPropertObject) ? new SnapToGeometry(this) : null;
      }
      return super.getAdapter(adapter);
   }

   @SuppressWarnings("unchecked")
   protected List getModelChildren() {
      List toReturn = new ArrayList();

      List<DataType> types = ((ODMDiagram) getModel()).getContent();
      toReturn.addAll(types);

      //      Map<ArtifactDataType, InheritanceLinkModel> inheritMap = new HashMap<ArtifactDataType, InheritanceLinkModel>();
      //      Map<RelationDataType, RelationLinkModel> relationMap = new HashMap<RelationDataType, RelationLinkModel>();
      //      for (DataType dataType : types) {
      //         ArtifactDataType artifact = (ArtifactDataType) dataType;
      //
      //         for (ArtifactDataType ancestor : artifact.getSuperTypes()) {
      //            InheritanceLinkModel inheritModel = inheritMap.get(ancestor);
      //            if (inheritModel == null) {
      //               inheritModel = inheritMap.get(artifact);
      //               if (inheritModel == null) {
      //                  inheritModel = new InheritanceLinkModel();
      //                  inheritMap.put(ancestor, inheritModel);
      //                  inheritMap.put(artifact, inheritModel);
      //               }
      //            }
      //            inheritModel.setSource(artifact);
      //            inheritModel.setTarget(ancestor);
      //         }

      //         for (RelationDataType relation : artifact.getLocalRelations()) {
      //            RelationLinkModel linkModel = relationMap.get(relation);
      //            if (linkModel == null) {
      //               linkModel = new RelationLinkModel();
      //               linkModel.setRelation(relation);
      //               relationMap.put(relation, linkModel);
      //            }
      //            if (linkModel.getASide() == null) {
      //               linkModel.setASide(artifact);
      //            } else {
      //               linkModel.setBSide(artifact);
      //            }
      //         }
      //      }
      //      for (InheritanceLinkModel inheritModel : inheritMap.values()) {
      //         if (inheritModel.getSource() != null && inheritModel.getTarget() != null) {
      //            toReturn.add(inheritModel);
      //         }
      //      }
      //      for (RelationLinkModel linkModel : relationMap.values()) {
      //         if (linkModel.getASide() != null && linkModel.getBSide() != null) {
      //            toReturn.add(linkModel);
      //         }
      //      }
      return toReturn;
   }
}
