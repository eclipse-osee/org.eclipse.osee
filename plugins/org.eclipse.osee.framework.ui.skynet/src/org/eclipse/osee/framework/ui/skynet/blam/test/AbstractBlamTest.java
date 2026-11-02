/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam.test;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamInputSection;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXBranchAndViewSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXBranchSelWidget;
import org.junit.Assert;

/**
 * Each BLAM MUST also have a BlamTest residing in the same package as the Blam. This allows the BLAMs to be tested both
 * in test environments and production prior to release. Each BlamTest should also test the operation in a
 * non-destructive way for production or check if TestUtil.isInTest to determine how exhaustive a test to run.<br>
 * <br>
 * Generic features like opening editor, rendering widgets, testing title and description are automatically tested by
 * this Abstract.
 *
 * @author Donald G. Dunne
 */
public class AbstractBlamTest {

   private final AbstractBlam blam;
   private boolean closeEditor = true;
   private BlamEditor editor;
   private XResultData rd;

   public AbstractBlamTest(AbstractBlam blam) {
      this.blam = blam;
      this.blam.setPend(true);
   }

   public XResultData test(XResultData rd) {
      this.rd = rd;
      try {

         setupDemoTest(blam, rd);

         String name = blam.getName();
         rd.logf("\nTesting [%s] - [%s]\n", blam.getClass().getSimpleName(), name);

         testOpenEditor();

         editor = findEditor();

         if (editor == null) {
            rd.errorf("Editor Did Not Open for [%s]\n", blam.getName());
            return rd;
         }

         rd.assertNotNullOrEmpty("Must have title\n", blam.getTitle());
         rd.assertNotNullOrEmpty("Must have description\n", blam.getDescriptionUsage());

         if (blam.getDescriptionUsage().equals(AbstractBlam.DEFAULT_DESCRIPTION)) {
            rd.warningf("All BLAMS should have their own description, not default [%s].\n", blam.getName());
         }

         // Test widget rendering and round-trip
         testWidgetRenderAndRoundTrip(editor, rd);

         if (!ClientSessionManager.isProductionDataStore()) {
            try {
               DemoUtilCore.setDemoDbBlamTest(true);
               runDemoTest(editor, rd);
            } catch (Exception ex) {
               rd.errorf("Error running BLAM [%s] - %s", getClass().getSimpleName(), Lib.exceptionToString(ex));
            } finally {
               DemoUtilCore.setDemoDbBlamTest(false);
            }
         } else {
            try {
               runProductionTest(editor, rd);
            } catch (Exception ex) {
               rd.errorf("Error running BLAM [%s] - %s", getClass().getSimpleName(), Lib.exceptionToString(ex));
            }
         }

         if (closeEditor) {
            editor.close(false);
         }

      } catch (Exception ex) {
         rd.errorf("Exception testing blam [%s]: %s\n", blam.getName(), ex.getMessage());
      }

      return rd;
   }

   public void cleanupSimpleTest(String title, BranchToken branch) {
      cleanupSimpleTest(Arrays.asList(title), branch);
   }

   public void cleanupSimpleTest(Collection<String> titles, BranchToken branch) {
      List<Artifact> artifacts = new ArrayList<>();
      for (String title : titles) {
         artifacts.addAll(
            ArtifactQuery.getArtifactListFromName(title, branch, EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS));
      }
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
   }

   protected void setupDemoTest(AbstractBlam blam2, XResultData rd2) {
      // for subclass demo test
   }

   private void runProductionTest(BlamEditor editor, XResultData rd) throws Exception {
      // for subclass production test
      /**
       * NOTE: This test must not be destructive or make changes to production data; this is intended for testing things
       * like search performance or publishing
       */
   }

   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      // for subclass demo test
   }

   /**
    * Tests that all widgets rendered successfully, can be set with dummy data based on their type, and the data can be
    * read back from the VariableMap.
    */
   protected void testWidgetRenderAndRoundTrip(BlamEditor editor, XResultData rd) {
      try {
         BlamInputSection inputSection = editor.getOverviewPage().getInputSection();
         Collection<XWidget> widgets = inputSection.getBlamWidgets();

         if (widgets.isEmpty()) {
            rd.warningf("No widgets found for [%s]\n", blam.getName());
            return;
         }

         rd.logf("  Generic Testing %d widgets for [%s]\n", widgets.size(), blam.getName());

         // Set dummy data on each widget based on its type
         for (XWidget widget : widgets) {
            try {
               setDummyData(widget);
            } catch (Exception ex) {
               rd.errorf("  Failed to set dummy data on widget [%s] in [%s]: %s\n", widget.getLabel(), blam.getName(),
                  ex.getMessage());
            }
         }

         // Read data back via VariableMap
         VariableMap variableMap = inputSection.getData();

         // Verify each widget's value can be read from the variableMap
         for (XWidget widget : widgets) {
            String label = widget.getLabel();
            try {
               Object value = variableMap.getValue(label);
               if (value == null) {
                  // Some widgets (like labels, buttons) may not produce data — that's OK
                  if (widget instanceof XXAbstractBaseXWidget) {
                     XXAbstractBaseXWidget<?> xxWidget = (XXAbstractBaseXWidget<?>) widget;
                     if (xxWidget.isSelected()) {
                        rd.errorf("  Widget [%s] has selection but variableMap returned null in [%s]\n", label,
                           blam.getName());
                     }
                  }
               } else {
                  rd.logf("  Widget [%s] = %s\n", label,
                     value.toString().length() > 50 ? value.toString().substring(0, 50) + "..." : value.toString());
               }
            } catch (Exception ex) {
               rd.errorf("  Failed to read widget [%s] from variableMap in [%s]: %s\n", label, blam.getName(),
                  ex.getMessage());
            }
         }

      } catch (Exception ex) {
         rd.errorf("  Exception in widget round-trip test for [%s]: %s\n", blam.getName(), ex.getMessage());
      }
   }

   /**
    * Sets dummy/test data on a widget based on its type. Subclasses can override to provide blam-specific test data.
    */
   @SuppressWarnings("unchecked")
   protected void setDummyData(XWidget widget) {
      if (widget instanceof XXBranchAndViewSelWidget) {
         ((XXBranchAndViewSelWidget) widget).setSelected(CoreBranches.COMMON);
      } else if (widget instanceof XXBranchSelWidget) {
         ((XXBranchSelWidget) widget).setSelected(CoreBranches.COMMON);
      } else if (widget instanceof XXAbstractBaseXWidget) {
         XXAbstractBaseXWidget<?> xxWidget = (XXAbstractBaseXWidget<?>) widget;
         Object sentinel = xxWidget.getSentinel();
         if (sentinel instanceof String) {
            ((XXAbstractBaseXWidget<String>) xxWidget).setSelected("test value", false);
         } else if (sentinel instanceof Date) {
            ((XXAbstractBaseXWidget<Date>) xxWidget).setSelected(new Date(), false);
         } else if (sentinel instanceof Boolean) {
            ((XXAbstractBaseXWidget<Boolean>) xxWidget).setSelected(true, false);
         }
         // Other types (Branch, ArtifactType, etc.) are left at sentinel — subclasses can override
      }
   }

   private BlamEditor findEditor() {
      Collection<BlamEditor> editors = BlamEditor.getEditors();
      for (BlamEditor bEditor : editors) {
         if (bEditor.getEditorInput().getBlamOperation().equals(blam)) {
            return bEditor;
         }
      }
      return null;
   }

   public void testOpenEditor() throws Exception {
      try {
         BlamEditor.edit(blam);
      } catch (Exception ex) {
         Assert.fail(Lib.exceptionToString(ex));
      }
   }

   public boolean isCloseEditor() {
      return closeEditor;
   }

   public void setCloseEditor(boolean closeEditor) {
      this.closeEditor = closeEditor;
   }

   public AbstractBlam getBlam() {
      return blam;
   }

   @SuppressWarnings("unchecked")
   public boolean setSelected(String widgetName, Object value) {
      XWidget widget = editor.getXWidget(widgetName);
      if (widget == null) {
         rd.errorf("Can't access widget [%s]\n", widgetName);
         return false;
      }
      if (!(widget instanceof XXAbstractBaseXWidget<?>)) {
         rd.errorf("setSelected() is only valid for XWidgets extending XXAbstractBaseXWidget\n");
         return false;
      }
      XWidget.debugXWidgets.syserr("Setting selected [%s] to [%s]", widget.getLabel(), value);
      rd.logf("Setting selected [%s] to [%s]\n", widget.getLabel(), value);
      XXAbstractBaseXWidget<Object> xxAbstractBaseXWidget = (XXAbstractBaseXWidget<Object>) widget;
      if (value instanceof Collection<?>) {
         xxAbstractBaseXWidget.setSelected((Collection<Object>) value);
      } else {
         xxAbstractBaseXWidget.setSelected(value);
      }
      return true;
   }

}
