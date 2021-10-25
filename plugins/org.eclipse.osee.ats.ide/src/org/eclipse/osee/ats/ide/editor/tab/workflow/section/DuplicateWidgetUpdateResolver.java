/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.section;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.editor.tab.workflow.WfeWorkFlowTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeHeaderComposite;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractSignDateAndByButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonCommon;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XDateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloatDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XIntegerDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Jeremy A. Midvidy
 */
public class DuplicateWidgetUpdateResolver {

   /**
    * This function is invoked when a widget on a workflow is changed. When the modified widget has one or more
    * duplicate widgets elsewhere on the same workflow, this function will update all duplicate widgets on the workflow
    * to represent the updated state of the modified widget.
    *
    * @param xWidget The modified widget on the workflow.
    */
   public static void updateDuplicateWidgets(IManagedForm managedForm, AbstractWorkflowArtifact sma, XWidget xWidget) {
      WorkDefinition workDef = (WorkDefinition) sma.getWorkDefinition();
      Object container = managedForm.getContainer();
      WfeWorkFlowTab currWfe = null;
      if (container instanceof WfeWorkFlowTab) {
         currWfe = (WfeWorkFlowTab) container;
      } else {
         return;
      }

      // If widget does not have duplicates, return
      if (workDef.getLabelCount().get(xWidget.getLabel()) <= 1) {
         return;
      }

      // Check Header first
      WfeHeaderComposite headComp = currWfe.getHeader();
      Collection<XWidget> headerWidgets = headComp.getXWidgets(new ArrayList<XWidget>());

      for (XWidget currHeadWidget : headerWidgets) {
         if (currHeadWidget.getLabel().equals(xWidget.getLabel())) {
            updateWidget(xWidget, currHeadWidget);
         }
      }

      // Check States
      List<StateXWidgetPage> statePages = currWfe.getStatePages();
      for (StateXWidgetPage currStatePage : statePages) {
         // Update duplicate widgets
         Collection<XWidget> updateWidgets = currStatePage.getDynamicXWidgetLayout().getXWidgets();
         for (XWidget currUpdateWidget : updateWidgets) {
            if (currUpdateWidget.getLabel().equals(xWidget.getLabel())) {
               updateWidget(xWidget, currUpdateWidget);
            }
         }
      }
   }

   /**
    * This function executes the actual widget update. After executing, currUpdateWidget will have the same state as
    * rootWidget. As more widgets are defined and added to the code-base, this function can be extended to implement the
    * state-update that is specific to the design of the actual widget classes being passed in.
    *
    * @param rootWidget The modified widget.
    * @param currUpdateWidget The widget to be updated to have the same state as rootWidget.
    * @return boolean true if currUpdateWidget and rootWidget had different states, false if they had the same state.
    */
   public static boolean updateWidget(XWidget rootWidget, XWidget currUpdateWidget) {

      String currText;
      String rootText;

      if (rootWidget == currUpdateWidget) {
         return false;
      }

      if (currUpdateWidget instanceof XTextDam) {
         currText = ((XTextDam) currUpdateWidget).get();
         rootText = ((XTextDam) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XTextDam) currUpdateWidget).setText(rootText);
      } else if (currUpdateWidget instanceof XComboDam) {
         currText = ((XComboDam) currUpdateWidget).get();
         rootText = ((XComboDam) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XComboDam) currUpdateWidget).set(rootText);
      } else if (currUpdateWidget instanceof XFloatDam) {
         currText = ((XFloatDam) currUpdateWidget).get();
         rootText = ((XFloatDam) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XFloatDam) currUpdateWidget).set(rootText);
      } else if (currUpdateWidget instanceof XIntegerDam) {
         currText = ((XIntegerDam) currUpdateWidget).get();
         rootText = ((XIntegerDam) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XIntegerDam) currUpdateWidget).set(rootText);
      } else if (currUpdateWidget instanceof XAbstractSignDateAndByButton) {
         if (((XAbstractSignDateAndByButton) rootWidget).doSign()) {
            String retText = ((XAbstractSignDateAndByButton) rootWidget).getResultsText();
            if (retText.equals(XAbstractSignDateAndByButton.NOT_YET_SIGNED)) {
               ((XAbstractSignDateAndByButton) currUpdateWidget).setUnsigned();
            } else {
               ((XAbstractSignDateAndByButton) currUpdateWidget).setSigned();
            }
            ((XAbstractSignDateAndByButton) currUpdateWidget).refresh();
         }
      } else if (currUpdateWidget instanceof XDateDam) {
         currText = ((XDateDam) currUpdateWidget).get();
         rootText = ((XDateDam) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XDateDam) currUpdateWidget).setDate(((XDateDam) rootWidget).getDate());
      } else if (currUpdateWidget instanceof XButtonCommon) {
         boolean currSel = ((XButtonCommon) currUpdateWidget).isSelected();
         boolean rootSel = ((XButtonCommon) rootWidget).isSelected();
         if (currSel == rootSel) {
            return false;
         }
         ((XButtonCommon) currUpdateWidget).set(rootSel);
         ;
      } else if (currUpdateWidget instanceof XText) {
         currText = ((XText) currUpdateWidget).get();
         rootText = ((XText) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XText) currUpdateWidget).set(rootText);
      }

      return true;
   }

}
