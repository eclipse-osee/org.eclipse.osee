/*
 * Created on Nov 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.workdef.WorkDefinition;
import org.eclipse.osee.ats.workdef.WorkDefinitionFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides combo box to choose from all valid states in ATS configured items. Used for generic searching windows where
 * set states are not known.
 * 
 * @author Donald G. Dunne
 */
public class XStateSearchCombo extends XComboViewer {
   protected static List<String> validStates = new ArrayList<String>();
   public static final String WIDGET_ID = XStateSearchCombo.class.getSimpleName();
   private String selectedState = null;

   public XStateSearchCombo() {
      super("State", SWT.NONE);
      ensurePopulated();
   }

   protected synchronized void ensurePopulated() {
      if (validStates.isEmpty()) {
         validStates.add("--select--");
         try {
            for (WorkItemDefinition wid : WorkItemDefinitionFactory.getWorkItemDefinitions()) {
               if (wid instanceof WorkPageDefinition) {
                  if (!validStates.contains(((WorkPageDefinition) wid).getPageName())) {
                     validStates.add(((WorkPageDefinition) wid).getPageName());
                  }
               }
            }
            for (WorkDefinition workDef : WorkDefinitionFactory.loadAllDefinitions()) {
               for (StateDefinition state : workDef.getStates()) {
                  if (!validStates.contains(state.getName())) {
                     validStates.add(state.getName());
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
         Collections.sort(validStates);
      }
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      getComboViewer().setInput(validStates);
      ArrayList<Object> defaultSelection = new ArrayList<Object>();
      defaultSelection.add("--select--");
      setSelected(defaultSelection);
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            String selected = (String) getSelected();
            if (selected.equals("--select--")) {
               selectedState = null;
            } else {
               selectedState = (String) getSelected();
            }
         }
      });
   }

   public String getSelectedState() {
      return selectedState;
   }

   @Override
   public void setSelected(List<Object> selected) {
      super.setSelected(selected);
      if (!selected.isEmpty() && !selected.iterator().next().equals("--select--")) {
         selectedState = (String) selected.iterator().next();
      }
   }

}
