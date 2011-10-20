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
package org.eclipse.osee.ats.mocks;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.data.AtsSearchParameters;
import org.eclipse.osee.display.api.data.ViewId;

/**
 * @author John Misinco
 */
public class MockAtsSearchHeaderComponent implements AtsSearchHeaderComponent {

   List<ViewId> programs = new LinkedList<ViewId>();
   List<ViewId> builds = new LinkedList<ViewId>();
   ViewId selectedProgram, selectedBuild;
   String errorMessage = "";
   boolean clearAllCalled = false;

   public boolean isClearAllCalled() {
      return clearAllCalled;
   }

   public List<ViewId> getPrograms() {
      return programs;
   }

   public List<ViewId> getBuilds() {
      return builds;
   }

   public ViewId getSelectedProgram() {
      return selectedProgram;
   }

   public ViewId getSelectedBuild() {
      return selectedBuild;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   @Override
   public void clearAll() {
      clearAllCalled = true;
      programs.clear();
      builds.clear();
   }

   @Override
   public void setErrorMessage(String message) {
      errorMessage = message;
   }

   @Override
   public void addProgram(ViewId program) {
      programs.add(program);
   }

   @Override
   public void clearBuilds() {
      builds.clear();
   }

   @Override
   public void addBuild(ViewId build) {
      builds.add(build);
   }

   @Override
   public void setSearchCriteria(AtsSearchParameters params) {
   }

   @Override
   public void setShowVerboseSearchResults(boolean showVerboseSearchResults) {
   }

}