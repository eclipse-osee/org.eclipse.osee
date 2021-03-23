package org.eclipse.osee.framework.ui.skynet.widgets;
/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.widgets.Composite;

/**
 * Currently supports read-only usage of relations, but is designed to support editing also
 *
 * @author Bhawana Mishra
 */
public class XListRelationWidget extends XList implements RelationWidget {

   public static final String WIDGET_ID = XListRelationWidget.class.getSimpleName();

   private final RelationTypeSide relationTypeSide;
   private final Artifact artifact;

   public XListRelationWidget(Artifact artifact, String displayLabel, RelationTypeSide relationTypeSide) {
      super(displayLabel);
      this.relationTypeSide = relationTypeSide;
      this.artifact = artifact;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      refresh();
   }

   private Collection<String> getRelateditems() {
      return Collections.transform(artifact.getRelatedArtifacts(relationTypeSide), ArtifactToken::getName);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      // TODO: implement if relation editing is added to this widget
   }

   @Override
   public void revert() {
      // TODO: implement if relation editing is added to this widget
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public RelationTypeSide getRelationTypeSide() {
      return relationTypeSide;
      //      This will not be called automatically since RelationWidget is new.  Need to handle this case in org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeWorkflowSection.refresh().  Please test that if relation is deleted/added in ArtifactEditor, the WorkflowEditor refreshes correctly.
   }

   @Override
   public void refresh() {
      removeAll();
      add(getRelateditems());
      updateListWidget();
   }
}