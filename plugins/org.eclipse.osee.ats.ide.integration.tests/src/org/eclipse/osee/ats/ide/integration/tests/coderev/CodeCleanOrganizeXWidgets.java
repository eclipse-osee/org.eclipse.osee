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
package org.eclipse.osee.ats.ide.integration.tests.coderev;

import java.util.ArrayList;
import java.util.List;

/**
 * Reorganizes {@code XX.*Widget.java} files into a standard section order. Emitted order:
 * <ol>
 * <li>Package + Imports + class declaration (untouched)</li>
 * <li>Static fields (WidgetId/WidgetIdAts ID first), instance fields (no headers)</li>
 * <li>Constructors (default no-arg, then {@code this(...)} chains, then the single {@code super(...)})</li>
 * <li>UI Construction, Storage, Validation, Refresh / Lifecycle, Event Listeners, Misc</li>
 * <li>Initialization (setArtifact, initializeSelected) - always last</li>
 * </ol>
 * <p>
 * Discovery is filesystem-based across every repo under the workspace root: files matching {@code XX.*Widget\.java}
 * (covers concrete {@code XX*Widget} and abstract {@code XXAbstract*Widget} bases). Concrete widgets must be an
 * {@code @Component(service = XWidget...)}; abstract bases are organized when {@link #includeAbstract} is true. See
 * {@link AbstractCodeClean} for the shared pipeline.
 *
 * @author Donald G. Dunne
 */
public class CodeCleanOrganizeXWidgets extends AbstractCodeClean {

   public CodeCleanOrganizeXWidgets() {
      // Report only by default; set true manually when ready to make changes.
      changeFiles = false;
   }

   @Override
   protected String getCleanName() {
      return "CodeCleanOrganizeXWidgets";
   }

   @Override
   protected String getFilePattern() {
      return "XX.*Widget\\.java";
   }

   @Override
   protected boolean shouldProcess(String contents, String justFilename) {
      boolean isAbstract = contents.contains("public abstract class");
      if (isAbstract && !includeAbstract) {
         rd.logf("Skipping abstract class [%s]\n", justFilename);
         return false;
      }
      // Concrete widgets must be an @Component XWidget service; abstract bases have none, so skip that check for them.
      if (!isAbstract && !contents.contains("@Component(service = XWidget")) {
         rd.logf("Skipping non-component [%s]\n", justFilename);
         return false;
      }
      return true;
   }

   @Override
   protected String rebuildBody(String justFilename, String header, List<Member> members) {
      List<Member> staticFields = new ArrayList<>();
      List<Member> instanceFields = new ArrayList<>();
      List<Member> defaultConstructors = new ArrayList<>();
      List<Member> thisConstructors = new ArrayList<>();
      List<Member> superConstructors = new ArrayList<>();
      List<Member> uiConstruction = new ArrayList<>();
      List<Member> storage = new ArrayList<>();
      List<Member> validation = new ArrayList<>();
      List<Member> refreshLifecycle = new ArrayList<>();
      List<Member> initialization = new ArrayList<>();
      List<Member> eventListeners = new ArrayList<>();
      List<Member> other = new ArrayList<>();

      for (Member m : members) {
         categorize(m, justFilename, staticFields, instanceFields, defaultConstructors, thisConstructors,
            superConstructors, uiConstruction, storage, validation, refreshLifecycle, initialization, eventListeners,
            other);
      }

      // WidgetId ID field first among static fields
      staticFields.sort((a, b) -> {
         boolean aIsId = a.text.contains("WidgetId ID") || a.text.contains("WidgetIdAts ID");
         boolean bIsId = b.text.contains("WidgetId ID") || b.text.contains("WidgetIdAts ID");
         if (aIsId && !bIsId) {
            return -1;
         }
         if (!aIsId && bIsId) {
            return 1;
         }
         return 0;
      });

      StringBuilder result = new StringBuilder();
      result.append(header);
      result.append("\n");

      for (Member m : staticFields) {
         result.append(m.text).append("\n");
      }
      if (!staticFields.isEmpty()) {
         result.append("\n");
      }

      for (Member m : instanceFields) {
         result.append(m.text).append("\n");
      }
      if (!instanceFields.isEmpty()) {
         result.append("\n");
      }

      if (!defaultConstructors.isEmpty() || !thisConstructors.isEmpty() || !superConstructors.isEmpty()) {
         result.append(String.format(SECTION_FORMAT, "Constructors")).append("\n\n");
         for (Member m : defaultConstructors) {
            result.append(m.text).append("\n\n");
         }
         for (Member m : thisConstructors) {
            result.append(m.text).append("\n\n");
         }
         for (Member m : superConstructors) {
            result.append(m.text).append("\n\n");
         }
      }

      appendSection(result, "UI Construction", uiConstruction);
      appendSection(result, "Storage", storage);
      appendSection(result, "Validation", validation);
      appendSection(result, "Refresh / Lifecycle", refreshLifecycle);
      appendSection(result, "Event Listeners", eventListeners);
      appendSection(result, "Misc", other);
      appendSection(result, "Initialization", initialization);

      result.append("}\n");
      return result.toString();
   }

   private void appendSection(StringBuilder result, String title, List<Member> members) {
      if (members.isEmpty()) {
         return;
      }
      result.append(String.format(SECTION_FORMAT, title)).append("\n\n");
      for (Member m : members) {
         result.append(m.text).append("\n\n");
      }
   }

   private void categorize(Member m, String className, List<Member> staticFields, List<Member> instanceFields,
      List<Member> defaultConstructors, List<Member> thisConstructors, List<Member> superConstructors,
      List<Member> uiConstruction, List<Member> storage, List<Member> validation, List<Member> refreshLifecycle,
      List<Member> initialization, List<Member> eventListeners, List<Member> other) {

      String text = m.text;
      String trimmedFirst = getFirstCodeLine(text);

      // Constructors - check before fields to avoid mis-categorization
      if (trimmedFirst.contains(className + "(") && !trimmedFirst.contains("=")) {
         if (trimmedFirst.contains(className + "()")) {
            defaultConstructors.add(m);
         } else if (text.contains("super(")) {
            superConstructors.add(m);
         } else {
            thisConstructors.add(m);
         }
         return;
      }

      if (isField(text)) {
         if (text.contains("static ")) {
            staticFields.add(m);
         } else {
            instanceFields.add(m);
         }
         return;
      }

      // UI Construction. getChildrenXWidgets() lives here because a composite defines its children as part of
      // building its UI.
      if (matchesAny(trimmedFirst, "createControls(", "createIconWidget(", "createLabelWidget(", "createValueWidget(",
         "createToolBar(", "getChildrenXWidgets(")) {
         uiConstruction.add(m);
         return;
      }

      if (matchesAny(trimmedFirst, "handleSelection(", "handleSelectedPersist(", "getSentinel(", "getSelectable(",
         "isSelectable(", "getDisplayValue(", "getEnumeratedArt(", "clear(", "setDate(", "setSelected(", "getBoolean(",
         "getAssignees(", "getInput(", "getMapEntry(", "setMapEntry(", "getSelectedArtifacts(", "addToInput(",
         "saveToArtifact(", "revert(", "isDirty(", "getStored(", "isOfType(", "isAttributeStorage(")) {
         storage.add(m);
         return;
      }

      if (matchesAny(trimmedFirst, "getValidator(", "validateHandleSelection(", "validateValuesXOption(", "isValid(")) {
         validation.add(m);
         return;
      }

      // Refresh / Lifecycle. setNotificationsAllowed() is here because composites delegate it to children as part
      // of their refresh/notification lifecycle.
      if (matchesAny(trimmedFirst, "refresh(", "refreshAll(", "dispose(", "getControl(", "isEmpty(", "setEditable(",
         "setFocus(", "isChecked(", "getCheckButton(", "addSelectionListener(", "getComparator(", "getLabelProvider(",
         "setButtonVisible(", "isButtonVisible(", "setNotificationsAllowed(")) {
         refreshLifecycle.add(m);
         return;
      }

      if (matchesAny(trimmedFirst, "setArtifact(", "initializeSelected(")) {
         initialization.add(m);
         return;
      }

      if (matchesAny(trimmedFirst, "handleArtifactEvent(", "handleArtifactTopicEvent(", "handleBranchEvent(",
         "handleEvent(", "getEventFilters(", "getTopicEventFilters(")) {
         eventListeners.add(m);
         return;
      }

      other.add(m);
   }

   public static void main(String[] args) {
      new CodeCleanOrganizeXWidgets().run();
   }

}
