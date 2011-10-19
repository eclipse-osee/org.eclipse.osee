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
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;

/**
 * @author John Misinco
 */
public class MockRelationComponent implements RelationComponent {

   private WebArtifact artifact;
   private final List<WebId> relationTypes = new LinkedList<WebId>();
   private final List<WebArtifact> leftRelations = new LinkedList<WebArtifact>();
   private final List<WebArtifact> rightRelations = new LinkedList<WebArtifact>();
   private String errorMessage;

   public WebArtifact getArtifact() {
      return artifact;
   }

   public List<WebId> getRelationTypes() {
      return relationTypes;
   }

   public List<WebArtifact> getRightRelations() {
      return rightRelations;
   }

   public List<WebArtifact> getLeftRelations() {
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
   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public void clearAll() {
      clearRelations();
      relationTypes.clear();
   }

   @Override
   public void addRelationType(WebId id) {
      relationTypes.add(id);
   }

   @Override
   public void clearRelations() {
      rightRelations.clear();
      leftRelations.clear();
   }

   @Override
   public void addLeftRelated(WebArtifact id) {
      leftRelations.add(id);
   }

   @Override
   public void addRightRelated(WebArtifact id) {
      rightRelations.add(id);
   }

   @Override
   public void setLeftName(String name) {
   }

   @Override
   public void setRightName(String name) {
   }

}