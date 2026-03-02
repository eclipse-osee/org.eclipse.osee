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
import org.eclipse.osee.ats.ide.util.widgets.signby.XAbstractSignByAndDateButtonArtWidget;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractSelectedWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboArtWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XDateArtWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloatArtWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XIntegerArtWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
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
      WorkDefinition workDef = sma.getWorkDefinition();
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

      if (currUpdateWidget instanceof XXTextWidget) {
         currText = ((XXTextWidget) currUpdateWidget).getSelectedFirst();
         rootText = ((XXTextWidget) rootWidget).getSelectedFirst();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XXTextWidget) currUpdateWidget).setSelected(rootText);
      } else if (currUpdateWidget instanceof XComboArtWidget) {
         currText = ((XComboArtWidget) currUpdateWidget).get();
         rootText = ((XComboArtWidget) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XComboArtWidget) currUpdateWidget).set(rootText);
      } else if (currUpdateWidget instanceof XFloatArtWidget) {
         currText = ((XFloatArtWidget) currUpdateWidget).get();
         rootText = ((XFloatArtWidget) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XFloatArtWidget) currUpdateWidget).set(rootText);
      } else if (currUpdateWidget instanceof XIntegerArtWidget) {
         currText = ((XIntegerArtWidget) currUpdateWidget).get();
         rootText = ((XIntegerArtWidget) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XIntegerArtWidget) currUpdateWidget).set(rootText);
      } else if (currUpdateWidget instanceof XAbstractSignByAndDateButtonArtWidget) {
         ((XAbstractSignByAndDateButtonArtWidget) currUpdateWidget).refresh();
      } else if (currUpdateWidget instanceof XDateArtWidget) {
         currText = ((XDateArtWidget) currUpdateWidget).get();
         rootText = ((XDateArtWidget) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XDateArtWidget) currUpdateWidget).setDate(((XDateArtWidget) rootWidget).getDate());
      } else if (currUpdateWidget instanceof XAbstractSelectedWidget) {
         boolean currSel = ((XAbstractSelectedWidget) currUpdateWidget).isSelected();
         boolean rootSel = ((XAbstractSelectedWidget) rootWidget).isSelected();
         if (currSel == rootSel) {
            return false;
         }
         ((XAbstractSelectedWidget) currUpdateWidget).set(rootSel);
         ;
      } else if (currUpdateWidget instanceof XTextWidget) {
         currText = ((XTextWidget) currUpdateWidget).get();
         rootText = ((XTextWidget) rootWidget).get();
         if (rootText.equals(currText)) {
            return false;
         }
         ((XTextWidget) currUpdateWidget).set(rootText);
      }

      return true;
   }

}
