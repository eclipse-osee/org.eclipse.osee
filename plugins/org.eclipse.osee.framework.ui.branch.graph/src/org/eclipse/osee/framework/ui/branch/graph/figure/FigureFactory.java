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
package org.eclipse.osee.framework.ui.branch.graph.figure;

import java.util.Date;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.branch.graph.model.BranchModel;
import org.eclipse.osee.framework.ui.branch.graph.model.TxData;
import org.eclipse.osee.framework.ui.branch.graph.model.TxModel;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphColorConstants;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphImageConstants;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphTextFormat;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class FigureFactory {

   private FigureFactory() {
   }

   public static PolylineConnection createConnection(IFigure contents, IFigure source, IFigure target, String toolTip, boolean hasEndPoint, Color color) {
      PolylineConnection connection = new PolylineConnection();
      ConnectionAnchor targetAnchor = new ChopboxAnchor(target);
      connection.setTargetAnchor(targetAnchor);
      connection.setSourceAnchor(new ChopboxAnchor(source));
      if (hasEndPoint) {
         PolygonDecoration decoration = new PolygonDecoration();
         decoration.setTemplate(PolygonDecoration.TRIANGLE_TIP);
         connection.setTargetDecoration(decoration);
      }
      if (toolTip != null) {
         connection.setToolTip(new Label(toolTip));
      }
      connection.setForegroundColor(color);
      contents.add(connection);
      return connection;
   }

   public static BranchFigure createBranchLabelFigure(BranchModel branchModel) {
      Color bgcolor = GraphColorConstants.getBranchColor(branchModel);
      Color fgcolor = GraphColorConstants.FONT_COLOR;

      BranchId branch = branchModel.getBranch();
      String branchName = BranchManager.getBranchName(branch);
      Image image = GraphImageConstants.getImage(branch);

      return new BranchFigure(branchName, image, createBranchNoteFigure(branchModel), bgcolor, fgcolor);
   }

   public static TxFigure createTxFigure(TxModel txModel) {
      Color bgcolor = GraphColorConstants.getBranchColor(txModel.getParentBranchModel());
      Color fgcolor = GraphColorConstants.FONT_COLOR;
      return new TxFigure(txModel.getRevision(), createTxNoteFigure(txModel), bgcolor, fgcolor);
   }

   public static IFigure createTxNoteFigure(TxModel txModel) {
      TxData txData = txModel.getTxData();
      IOseeBranch branch = BranchManager.getBranchToken(txData.getBranch());
      String title = String.format("Tx: %s Name: %s", txData.getTxId(), branch.getShortName());
      return createNoteFigure(title, branch.getName(), txData.getAuthor(), txData.getTimeStamp(), txData.getComment());
   }

   public static IFigure createBranchNoteFigure(BranchModel branchModel) {
      IOseeBranch branch = BranchManager.getBranchToken(branchModel.getBranch());
      String title = String.format("Tx: %s Name: %s", branchModel.getFirstTx().getRevision(), branch.getShortName());
      TxData txData = branchModel.getFirstTx().getTxData();
      return createNoteFigure(title, branch.getName(), txData.getAuthor(), txData.getTimeStamp(), txData.getComment());
   }

   private static IFigure createNoteFigure(String shortName, String name, String author, Date date, String comment) {
      FrameFigure contents = new FrameFigure();
      contents.setLayoutManager(new GridLayout(2, false));
      contents.setBackgroundColor(GraphColorConstants.BGCOLOR);

      Font labelFont = JFaceResources.getTextFont();
      Font textFont = JFaceResources.getDefaultFont();

      contents.setLabel(" " + shortName + " ");
      contents.setLabelFont(JFaceResources.getTextFont());
      contents.setFont(textFont);

      contents.add(createLabel("Branch", labelFont, GraphColorConstants.FONT_COLOR));
      contents.add(createLabel(name, textFont));
      contents.add(createLabel("Author", labelFont, GraphColorConstants.FONT_COLOR));
      contents.add(createLabel(author, textFont));
      contents.add(createLabel("Date", labelFont, GraphColorConstants.FONT_COLOR));
      contents.add(createLabel(GraphTextFormat.formatDate(date), textFont));
      contents.add(createLabel("Message", labelFont, GraphColorConstants.FONT_COLOR));
      contents.add(createLabel(comment, textFont));
      contents.setPreferredSize(contents.getPreferredSize());
      return contents;
   }

   public static Label createLabel(String text, Font font) {
      return createLabel(text, font, PositionConstants.LEFT);
   }

   public static Label createLabel(String text, Font font, Color fgColor) {
      return createLabel(text, font, PositionConstants.LEFT, fgColor);
   }

   public static Label createLabel(String text, Font font, int position) {
      Label label = new Label(text);
      label.setFont(font);
      label.setTextAlignment(position);
      return label;
   }

   public static Label createLabel(String text, Font font, int position, Color fgColor) {
      Label label = new Label(text);
      label.setFont(font);
      label.setTextAlignment(position);
      label.setForegroundColor(fgColor);
      return label;
   }

   public static Label createLabel(String text, Font font, int alignment, Color fgColor, Color bgColor) {
      Label label = new Label(text);
      label.setFont(font);
      label.setTextAlignment(alignment);
      label.setForegroundColor(fgColor);
      label.setBackgroundColor(bgColor);
      return label;
   }
}
