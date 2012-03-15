/*
 * Created on Mar 23, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
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
   public void testConstructor() {
      new ModelUtil();
   }

   @Test
   public void testSaveModelLoadModel() throws IOException, OseeCoreException {

      WorkDefinition workDef = getWorkDefinition();
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

   @Test(expected = OseeStateException.class)
   public void testSaveModelLoadModel_exception() throws OseeCoreException {

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

   @Test(expected = OseeCoreException.class)
   public void testSaveModelLoadModel_exception2() throws OseeCoreException {

      ModelUtil.loadModel("ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", "blah", new IResourceProvider() {

         @Override
         public Collection<String> getErrors() {
            return Collections.emptyList();
         }

         @Override
         public AtsDsl getContents(String uri, String xTextData) throws OseeCoreException {
            throw new OseeCoreException("this is exception");
         }
      });
   }

   public WorkDefinition getWorkDefinition() {
      WorkDefinition workDef = new WorkDefinition("WorkDef_Team_Default");

      StateDefinition analyze = new StateDefinition("Analyze");
      analyze.setWorkDefinition(workDef);
      analyze.setWorkPageType(WorkPageType.Working);
      analyze.setOrdinal(1);
      workDef.getStates().add(analyze);

      workDef.setStartState(analyze);

      StateDefinition implement = new StateDefinition("Implement");
      implement.setWorkDefinition(workDef);
      implement.setWorkPageType(WorkPageType.Working);
      implement.setOrdinal(2);
      workDef.getStates().add(implement);

      StateDefinition completed = new StateDefinition("Completed");
      completed.setWorkDefinition(workDef);
      completed.setWorkPageType(WorkPageType.Completed);
      completed.setOrdinal(3);
      workDef.getStates().add(completed);

      StateDefinition cancelled = new StateDefinition("Cancelled");
      cancelled.setWorkDefinition(workDef);
      cancelled.setWorkPageType(WorkPageType.Cancelled);
      cancelled.setOrdinal(4);
      workDef.getStates().add(cancelled);

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

      WidgetDefinition estHoursWidgetDef = new WidgetDefinition("Estimated Hours");
      estHoursWidgetDef.setAttributeName("ats.Estimated Hours");
      estHoursWidgetDef.setXWidgetName("XFloatDam");

      WidgetDefinition workPackageWidgetDef = new WidgetDefinition("Work Package");
      workPackageWidgetDef.setAttributeName("ats.Work Package");
      workPackageWidgetDef.setXWidgetName("XTextDam");

      return workDef;
   }
}
