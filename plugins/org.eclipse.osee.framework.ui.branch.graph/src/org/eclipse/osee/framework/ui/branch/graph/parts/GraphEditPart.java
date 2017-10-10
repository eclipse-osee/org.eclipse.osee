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
package org.eclipse.osee.framework.ui.branch.graph.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.branch.graph.Activator;
import org.eclipse.osee.framework.ui.branch.graph.figure.BranchFigure;
import org.eclipse.osee.framework.ui.branch.graph.figure.FigureFactory;
import org.eclipse.osee.framework.ui.branch.graph.figure.TxFigure;
import org.eclipse.osee.framework.ui.branch.graph.model.BranchModel;
import org.eclipse.osee.framework.ui.branch.graph.model.GraphCache;
import org.eclipse.osee.framework.ui.branch.graph.model.TxModel;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphOptions;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphOptions.ConnectionFilter;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphOptions.TxFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class GraphEditPart extends AbstractGraphicalEditPart {

   private final GraphicalViewer viewer;
   private final Map<BranchId, BranchFigure> branchFigureMap;
   private final Map<Long, TxModel> txNumberToTxModelMap;
   private final Map<Long, TxFigure> txNumberToTxFigureMap;
   private final HashCollection<ConnectionType, Connection> connectionMap;

   private final HashCollection<Integer, BranchModel> branchesByLevel;

   private final IPreferenceStore preferenceStore;

   private enum ConnectionType {
      BRANCH_INTERNAL,
      PARENT_CHILD,
      MERGE;
   }

   public GraphEditPart(GraphicalViewer viewer) {
      super();
      this.viewer = viewer;
      this.branchFigureMap = new HashMap<>();
      this.txNumberToTxModelMap = new HashMap<>();
      this.txNumberToTxFigureMap = new HashMap<>();
      this.branchesByLevel = new HashCollection<>();
      this.connectionMap = new HashCollection<>();
      this.preferenceStore = Activator.getInstance().getPreferenceStore();
   }

   /*
    * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
    */
   @Override
   protected void createEditPolicies() {
      //      installEditPolicy(EditPolicy.LAYOUT_ROLE, new GraphXYLayoutEditPolicy());
   }

   /*
    * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    */
   @Override
   protected IFigure createFigure() {
      IFigure figure = new Figure();
      figure.setBackgroundColor(ColorConstants.white);
      figure.setOpaque(true);

      XYLayout layout = new XYLayout();
      figure.setLayoutManager(layout);

      ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
      FanRouter router = new FanRouter();
      router.setSeparation(20);
      router.setNextRouter(new ShortestPathConnectionRouter(figure));
      connLayer.setConnectionRouter(router);
      connLayer.setAntialias(SWT.ON);
      return figure;
   }

   /*
    * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
    */
   @Override
   protected List<BranchModel> getModelChildren() {
      GraphCache graphCache = (GraphCache) getModel();

      List<BranchModel> nodes = new ArrayList<>();
      nodes.add(graphCache.getRootModel());
      nodes.addAll(graphCache.getRootModel().getAllChildrenBelow());
      Collections.sort(nodes, new Comparator<BranchModel>() {
         @Override
         public int compare(BranchModel o1, BranchModel o2) {
            int level1 = o1.getDepth();
            int level2 = o2.getDepth();
            int result = new Integer(level1).compareTo(new Integer(level2));
            //            if (result == 0) {
            //               BranchModel parent1 = o1.getParentBranch();
            //               BranchModel parent2 = o2.getParentBranch();
            //               if (parent1 == null && parent2 == null) {
            //                  result = 0;
            //               } else if (parent1 != null && parent2 == null) {
            //                  result = 1;
            //               } else if (parent2 != null && parent1 == null) {
            //                  result = -1;
            //               } else {
            //                  result = parent1.getBranch().compareTo(parent2.getBranch());
            //               }
            //            }
            //            if (result == 0) {
            //               TxModel m1 = o1.getFirstTx();
            //               TxModel m2 = o2.getFirstTx();
            //               try {
            //                  result = m1.compareTo(m2);
            //               } catch (Exception ex) {
            //                  OseeLog.logf(RevisionGraphActivator.class, Level.SEVERE,
            //                        "One of the branch models did not have a starting tx. 1:[%s] 2:[%s]", o1, o2);
            //               }
            //            }
            return result;
         }
      });

      List<BranchModel> toDraw = new ArrayList<>();
      for (int index = 0; index < nodes.size(); index++) {
         BranchModel model = nodes.get(index);
         model.setIndex(index);
         toDraw.add(model);
         branchFigureMap.put(model.getBranch(), FigureFactory.createBranchLabelFigure(model));
         branchesByLevel.put(model.getDepth(), model);
      }

      for (TxModel model : graphCache.getTxModels()) {
         BranchModel parent = model.getParentBranchModel();
         if (parent != null && parent.areTxsVisible() && parent.isVisible()) {
            Long txNumber = model.getRevision();
            txNumberToTxModelMap.put(txNumber, model);
            txNumberToTxFigureMap.put(txNumber, FigureFactory.createTxFigure(model));
         } else {
            OseeLog.logf(Activator.class, Level.SEVERE, "Orphan TxModel: [%s]", model.toString());
         }
      }

      createTxConnections(toDraw);
      createMergedConnections(toDraw);

      setConnectionVisibility();
      return toDraw;
   }

   public int getMaxTxForGraphLevel(int graphLevel) {
      int max = Integer.MIN_VALUE;
      Collection<BranchModel> models = branchesByLevel.getValues(graphLevel);
      if (models != null) {
         for (BranchModel model : models) {
            if (model.areTxsVisible()) {
               max = Math.max(model.getTxs().size(), max);
            } else {
               max = Math.max(3, max);
            }
         }
      }
      return max;
   }

   public int getNumberOfBranchesAtGraphLevel(int graphLevel) {
      Collection<?> collection = branchesByLevel.getValues(graphLevel);
      return collection != null ? collection.size() : 0;
   }

   public int getMaxNumberOfBranchesAtAnyLevel() {
      int max = Integer.MIN_VALUE;
      for (Integer level : branchesByLevel.keySet()) {
         max = Math.max(max, getNumberOfBranchesAtGraphLevel(level));
      }
      return max;
   }

   public BranchFigure getFigure(BranchId branch) {
      return branchFigureMap.get(branch);
   }

   public TxFigure getTxFigure(TxModel txModel) {
      return txNumberToTxFigureMap.get(txModel.getRevision());
   }

   private void createMergedConnections(Collection<BranchModel> toReturn) {
      for (BranchModel branchNode : toReturn) {
         for (TxModel txModel : branchNode.getTxs()) {
            if (txModel.getMergedTx() != null) {
               TxFigure txFigure = txNumberToTxFigureMap.get(txModel.getRevision());
               for (TxModel merged : txModel.getMergedTx()) {
                  TxFigure mergedView = txNumberToTxFigureMap.get(merged.getRevision());
                  if (mergedView != null) {
                     String message = getConnectionLabel(merged, txModel);
                     connect(ConnectionType.MERGE, getFigure(), mergedView, txFigure, message, true,
                        ColorConstants.red);
                  }
               }
            }
         }
      }
   }

   private String getConnectionLabel(TxModel source, TxModel target) {
      String sourceName = BranchManager.getBranchShortName(source.getParentBranchModel().getBranch());
      String targetName = BranchManager.getBranchShortName(target.getParentBranchModel().getBranch());
      return String.format("%s:%s - %s:%s", sourceName, source.getRevision(), targetName, target.getRevision());
   }

   private void createTxConnections(Collection<BranchModel> toReturn) {
      for (BranchModel branchModel : toReturn) {
         if (branchModel.areTxsVisible()) {
            for (TxModel txModel : branchModel.getTxs()) {
               TxFigure txFigure = getTxFigure(txModel);
               boolean connectToBranchLabel = branchModel.getFirstTx().equals(txModel);
               String msg = null;
               if (txModel.getParentTx() != null) {
                  TxModel parentTxModel = txModel.getParentTx();
                  TxFigure parent = getTxFigure(parentTxModel);
                  if (parent != null) {
                     msg = getConnectionLabel(parentTxModel, txModel);
                     connect(ConnectionType.BRANCH_INTERNAL, getFigure(), parent, txFigure, msg, false,
                        ColorConstants.black);
                  }
               } else if (txModel.getSourceTx() != null) {
                  TxModel sourceTx = txModel.getSourceTx();
                  TxFigure source = getTxFigure(sourceTx);
                  if (source != null) {
                     msg = getConnectionLabel(sourceTx, txModel);
                     connect(ConnectionType.PARENT_CHILD, getFigure(), source, txFigure, msg, true,
                        ColorConstants.blue);
                  }
               } else {
                  connectToBranchLabel = true;
               }

               if (connectToBranchLabel) {
                  BranchFigure branchFigure = branchFigureMap.get(branchModel.getBranch());
                  msg = getConnectionLabel(branchModel.getFirstTx(), txModel);
                  connect(ConnectionType.BRANCH_INTERNAL, getFigure(), branchFigure, txFigure, msg, false,
                     ColorConstants.black);
               }
            }
         } else {
            TxModel txModel = branchModel.getFirstTx();
            if (txModel == null) {
               OseeLog.logf(Activator.class, Level.SEVERE, "Branch did not have a starting tx [%s]", branchModel);
            } else {
               if (txModel.getSourceTx() != null) {
                  TxModel sourceTx = txModel.getSourceTx();
                  String msg = getConnectionLabel(sourceTx, txModel);

                  BranchFigure branchFigure = branchFigureMap.get(branchModel.getBranch());
                  if (sourceTx.getParentBranchModel().areTxsVisible()) {
                     TxFigure source = getTxFigure(sourceTx);
                     if (source != null) {
                        connect(ConnectionType.PARENT_CHILD, getFigure(), source, branchFigure, msg, true,
                           ColorConstants.lightBlue);
                     }
                  } else {
                     BranchFigure source = branchFigureMap.get(sourceTx.getParentBranchModel().getBranch());
                     connect(ConnectionType.PARENT_CHILD, getFigure(), source, branchFigure, msg, true,
                        ColorConstants.lightBlue);
                  }
               }
            }
         }
      }
   }

   private Set<ConnectionType> getFilteredConnectionTypes() {
      int connectLevel = preferenceStore.getInt(GraphOptions.FILTER_CONNECTIONS_PREFERENCE);
      ConnectionFilter[] filters = ConnectionFilter.values();
      ConnectionFilter filter = ConnectionFilter.NO_FILTER;
      if (connectLevel < filters.length && connectLevel >= 0) {
         filter = filters[connectLevel];
      }
      Set<ConnectionType> filtered = new HashSet<>();
      switch (filter) {
         case FILTER_CHILD_BRANCH_CONNECTIONS:
            filtered.add(ConnectionType.PARENT_CHILD);
            break;
         case FILTER_MERGE_CONNECTIONS:
            filtered.add(ConnectionType.MERGE);
            break;
         case FILTER_ALL_CONNECTIONS:
            for (ConnectionType connectionType : ConnectionType.values()) {
               if (connectionType != ConnectionType.BRANCH_INTERNAL) {
                  filtered.add(connectionType);
               }
            }
            break;
         case NO_FILTER:
         default:
            break;
      }
      return filtered;
   }

   public void setConnectionVisibility() {
      Set<ConnectionType> filteredTypes = getFilteredConnectionTypes();
      for (ConnectionType connectType : connectionMap.keySet()) {
         Collection<Connection> connections = connectionMap.getValues(connectType);
         if (connections != null) {
            for (Connection connection : connections) {
               boolean isVisible = false;
               if (!filteredTypes.contains(connectType)) {
                  isVisible = true;
               } else {
                  IFigure source = connection.getSourceAnchor().getOwner();
                  IFigure target = connection.getTargetAnchor().getOwner();
                  if (source instanceof TxFigure && ((TxFigure) source).isSelected()) {
                     isVisible = true;
                  } else if (target instanceof TxFigure && ((TxFigure) target).isSelected()) {
                     isVisible = true;
                  } else {
                     isVisible = false;
                  }
               }
               connection.setVisible(isVisible);
            }
         }
      }
   }

   public void setTxVisibility() {
      int filterSetting = preferenceStore.getInt(GraphOptions.TRANSACTION_FILTER);
      boolean isVisible = TxFilter.NO_FILTER.ordinal() == filterSetting;
      GraphCache graph = (GraphCache) getModel();
      for (BranchModel model : graph.getBranchModels()) {
         model.setTxsVisible(isVisible);
      }
      viewer.setContents(graph);
   }

   private void connect(ConnectionType connectType, IFigure contents, IFigure source, IFigure target, String toolTip, boolean hasEndPoint, Color color) {
      PolylineConnection connection =
         FigureFactory.createConnection(getFigure(), source, target, toolTip, hasEndPoint, color);
      ConnectionMouseListener listener = new ConnectionMouseListener(connection);
      connection.addMouseMotionListener(listener);
      connection.addMouseListener(listener);
      connection.setCursor(Cursors.HAND);
      connectionMap.put(connectType, connection);
   }

   private void scrollTo(Rectangle fbounds) {
      scrollTo(fbounds.x + fbounds.width / 2, fbounds.y + fbounds.height / 2);
   }

   private void scrollTo(int ax, int ay) {
      Viewport viewport = ((FigureCanvas) viewer.getControl()).getViewport();
      Rectangle vbounds = viewport.getBounds();
      Point p = new Point(ax, ay);
      int x = p.x - vbounds.width / 2;
      int y = p.y - vbounds.height / 2;
      viewport.setHorizontalLocation(x);
      viewport.setVerticalLocation(y);
   }

   private void scrollTo(IFigure target) {
      scrollTo(target.getBounds());
   }

   private final class ConnectionMouseListener implements MouseMotionListener, MouseListener {

      private final PolylineConnection connection;

      public ConnectionMouseListener(PolylineConnection connection) {
         this.connection = connection;
      }

      @Override
      public void mouseDragged(MouseEvent event) {
         // do nothing
      }

      @Override
      public void mouseEntered(MouseEvent event) {
         // do nothing
      }

      @Override
      public void mouseExited(MouseEvent event) {
         connection.setLineWidth(1);
      }

      @Override
      public void mouseHover(MouseEvent event) {
         connection.setLineWidth(3);
      }

      @Override
      public void mouseMoved(MouseEvent event) {
         // do nothing
      }

      @Override
      public void mouseDoubleClicked(MouseEvent event) {
         // do nothing
      }

      @Override
      public void mousePressed(MouseEvent event) {
         TxFigure txFigure = (TxFigure) connection.getTargetAnchor().getOwner();
         scrollTo(txFigure);
         Map<?, ?> map = viewer.getEditPartRegistry();

         TxModel txModel = txNumberToTxModelMap.get(txFigure.getTxNumber());
         if (txModel != null) {
            EditPart editPart = (EditPart) map.get(txModel);
            if (editPart != null) {
               viewer.select(editPart);
            }
         }
      }

      @Override
      public void mouseReleased(MouseEvent event) {
         // do nothing
      }
   }
}
