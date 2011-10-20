/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.mocks;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.data.ViewId;

/**
 * @author John Misinco
 */
public class MockRelationComponent implements RelationComponent {

   private ViewArtifact artifact;
   private final List<ViewId> relationTypes = new LinkedList<ViewId>();
   private final List<ViewArtifact> leftRelations = new LinkedList<ViewArtifact>();
   private final List<ViewArtifact> rightRelations = new LinkedList<ViewArtifact>();
   private String errorMessage;

   public ViewArtifact getArtifact() {
      return artifact;
   }

   public List<ViewId> getRelationTypes() {
      return relationTypes;
   }

   public List<ViewArtifact> getRightRelations() {
      return rightRelations;
   }

   public List<ViewArtifact> getLeftRelations() {
      return leftRelations;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   @Override
   public void setErrorMessage(String message) {
      errorMessage = message;
   }

   @Override
   public void setArtifact(ViewArtifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public void clearAll() {
      clearRelations();
      relationTypes.clear();
   }

   @Override
   public void addRelationType(ViewId id) {
      relationTypes.add(id);
   }

   @Override
   public void clearRelations() {
      rightRelations.clear();
      leftRelations.clear();
   }

   @Override
   public void addLeftRelated(ViewArtifact id) {
      leftRelations.add(id);
   }

   @Override
   public void addRightRelated(ViewArtifact id) {
      rightRelations.add(id);
   }

   @Override
   public void setLeftName(String name) {
   }

   @Override
   public void setRightName(String name) {
   }

}