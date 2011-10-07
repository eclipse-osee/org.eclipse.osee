package org.eclipse.osee.ats.presenter.mock;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponentInterface;
import org.eclipse.osee.display.api.data.WebId;

public class MockAtsSearchHeaderComponent implements AtsSearchHeaderComponentInterface {

   List<WebId> programs = new LinkedList<WebId>();
   List<WebId> builds = new LinkedList<WebId>();
   WebId selectedProgram, selectedBuild;
   String errorMessage = "";
   boolean clearAllCalled = false;

   public boolean isClearAllCalled() {
      return clearAllCalled;
   }

   public List<WebId> getPrograms() {
      return programs;
   }

   public List<WebId> getBuilds() {
      return builds;
   }

   public WebId getSelectedProgram() {
      return selectedProgram;
   }

   public WebId getSelectedBuild() {
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
   public void addProgram(WebId program) {
      programs.add(program);
   }

   @Override
   public void clearBuilds() {
      builds.clear();
   }

   @Override
   public void addBuild(WebId build) {
      builds.add(build);
   }

   @Override
   public void setSearchCriteria(WebId program, WebId build, boolean nameOnly, String searchPhrase) {
   }

   @Override
   public void setProgram(WebId program) {
      selectedProgram = program;
   }

   @Override
   public void setBuild(WebId build) {
      selectedBuild = build;
   }

}