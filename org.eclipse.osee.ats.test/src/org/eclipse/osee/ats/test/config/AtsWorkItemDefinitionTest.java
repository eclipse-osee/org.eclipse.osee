/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.config;

import junit.framework.TestCase;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.XWidgetFactory;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemDefinitionTest extends TestCase {

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
   }

   public void testWorkWidgetDefinitions() throws Exception {
      for (WorkItemDefinition workItemDefinition : WorkItemDefinitionFactory.getWorkItemDefinitions()) {
         if (workItemDefinition instanceof WorkWidgetDefinition) {
            System.out.println("Testing Widget " + workItemDefinition);
            WorkWidgetDefinition workWidgetDefinition = (WorkWidgetDefinition) workItemDefinition;
            DynamicXWidgetLayoutData dynamicXWidgetLayoutData = workWidgetDefinition.get();
            XWidgetFactory.getInstance().createXWidget(dynamicXWidgetLayoutData);
         }
      }
   }

   public void testWorkPageDefinitions() throws Exception {
      for (WorkItemDefinition workItemDefinition : WorkItemDefinitionFactory.getWorkItemDefinitions()) {
         if (workItemDefinition instanceof WorkPageDefinition) {
            System.out.println("Testing Page " + workItemDefinition);
            WorkPageDefinition workPageDefinition = (WorkPageDefinition) workItemDefinition;
            workPageDefinition.getWorkItems(true);
         }
      }
   }

   public void testWorkFlowDefinitions() throws Exception {
      for (WorkItemDefinition workItemDefinition : WorkItemDefinitionFactory.getWorkItemDefinitions()) {
         if (workItemDefinition instanceof WorkFlowDefinition) {
            System.out.println("Testing Work Flow " + workItemDefinition);
            WorkFlowDefinition workFlowDefinition = (WorkFlowDefinition) workItemDefinition;
            assertTrue(workFlowDefinition.getPagesOrdered().size() > 0);
            assertNotNull(workFlowDefinition.getStartPage());
         }
      }
   }
}
