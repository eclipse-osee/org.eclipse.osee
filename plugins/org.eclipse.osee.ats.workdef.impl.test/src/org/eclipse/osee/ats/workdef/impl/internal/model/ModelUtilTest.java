/*
 * Created on Mar 23, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.impl.internal.model;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.dsl.IResourceProvider;
import org.eclipse.osee.ats.dsl.ModelUtil;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.workdef.api.IAtsStateDefinition;
import org.eclipse.osee.ats.workdef.api.IAtsWidgetDefinition;
import org.eclipse.osee.ats.workdef.api.IAtsWorkDefinition;
import org.eclipse.osee.ats.workdef.api.StateType;
import org.eclipse.osee.ats.workdef.impl.internal.convert.ConvertWorkDefinitionToAtsDsl;
import org.eclipse.osee.ats.workdef.impl.internal.model.StateDefinition;
import org.eclipse.osee.ats.workdef.impl.internal.model.WidgetDefinition;
import org.eclipse.osee.ats.workdef.impl.internal.model.WorkDefinition;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Test;

/**
 * Test case for {@link ModelUtil}
 * 
 * @author Donald G. Dunne
 */
public class ModelUtilTest {

   @Test
   public void testSaveModelLoadModel() throws Exception {

      IAtsWorkDefinition workDef = getWorkDefinition();
      XResultData resultData = new XResultData();
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(resultData);
      AtsDsl atsDsl = converter.convert(workDef.getName(), workDef);

      String filename = workDef.getName() + ".ats";
      File file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + filename);
      FileOutputStream outputStream = new FileOutputStream(file);
      ModelUtil.saveModel(atsDsl, "ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", outputStream);
      String contents = Lib.fileToString(file);

      Lib.writeStringToFile(contents, file);

      ModelUtil.loadModel("ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", contents);
   }

   @Test(expected = Exception.class)
   public void testSaveModelLoadModel_exception() throws Exception {

      ModelUtil.loadModel("ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", "blah", new IResourceProvider() {

         @Override
         public Collection<String> getErrors() {
            return Arrays.asList("This is the error");
         }

         @Override
         public AtsDsl getContents(String uri, String xTextData) {
            return null;
         }
      });
   }

   @Test(expected = Exception.class)
   public void testSaveModelLoadModel_exception2() throws Exception {

      ModelUtil.loadModel("ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", "blah", new IResourceProvider() {

         @Override
         public Collection<String> getErrors() {
            return Collections.emptyList();
         }

         @Override
         public AtsDsl getContents(String uri, String xTextData) throws Exception {
            throw new Exception("this is exception");
         }
      });
   }

   public IAtsWorkDefinition getWorkDefinition() {
      IAtsWorkDefinition workDef = new WorkDefinition("WorkDef_Team_Default");

      IAtsStateDefinition analyze = new StateDefinition("Analyze");
      analyze.setWorkDefinition(workDef);
      analyze.setStateType(StateType.Working);
      analyze.setOrdinal(1);
      workDef.addState(analyze);

      workDef.setStartState(analyze);

      IAtsStateDefinition implement = new StateDefinition("Implement");
      implement.setWorkDefinition(workDef);
      implement.setStateType(StateType.Working);
      implement.setOrdinal(2);
      workDef.addState(implement);

      IAtsStateDefinition completed = new StateDefinition("Completed");
      completed.setWorkDefinition(workDef);
      completed.setStateType(StateType.Completed);
      completed.setOrdinal(3);
      workDef.addState(completed);

      IAtsStateDefinition cancelled = new StateDefinition("Cancelled");
      cancelled.setWorkDefinition(workDef);
      cancelled.setStateType(StateType.Cancelled);
      cancelled.setOrdinal(4);
      workDef.addState(cancelled);

      analyze.setDefaultToState(implement);
      analyze.getToStates().addAll(Arrays.asList(implement, completed, cancelled));
      analyze.getOverrideAttributeValidationStates().addAll(Arrays.asList(cancelled));

      implement.setDefaultToState(completed);
      implement.getToStates().addAll(Arrays.asList(analyze, completed, cancelled));
      implement.getOverrideAttributeValidationStates().addAll(Arrays.asList(cancelled, analyze));

      completed.setDefaultToState(completed);
      completed.getToStates().addAll(Arrays.asList(implement));
      completed.getOverrideAttributeValidationStates().addAll(Arrays.asList(implement));

      cancelled.getToStates().addAll(Arrays.asList(analyze, implement));
      cancelled.getOverrideAttributeValidationStates().addAll(Arrays.asList(analyze, implement));

      IAtsWidgetDefinition estHoursWidgetDef = new WidgetDefinition("Estimated Hours");
      estHoursWidgetDef.setAttributeName("ats.Estimated Hours");
      estHoursWidgetDef.setXWidgetName("XFloatDam");

      IAtsWidgetDefinition workPackageWidgetDef = new WidgetDefinition("Work Package");
      workPackageWidgetDef.setAttributeName("ats.Work Package");
      workPackageWidgetDef.setXWidgetName("XTextDam");

      return workDef;
   }
}
