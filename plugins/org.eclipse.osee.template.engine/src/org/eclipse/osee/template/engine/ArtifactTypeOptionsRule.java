/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.template.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Marc Potter
 */

/**
 * This class creates the HTML required to handle a selection of artifact types. The selection is done using two <select>
 * boxes. The first is a single selection box listing all the possible types that can be selected. The second lists the
 * types that are selected. The selected types can be added to by selecting from the list of possible types.
 * 
 */
//@formatter:off
/**************************************************
 * The rule within the template or substitution file is of the format
 * <?rule name="artifactTypeSelect" id="[ID]" types="[TYPES]" preselect="[PRESEL]" ?>
 * Where:
 *      [ID] (required) is the identifier of the select box and OSEEParam
 *      [TYPES] (required) is a comma separated list of the names of the artifact types to be made available
 *      [PRESEL] (optional) is a comma separated list of the names of the artifact types that will show as selected
 *                       when the page is first displayed
 */
//@formatter:on
public class ArtifactTypeOptionsRule extends AppendableRule<String> {
   public static final String RULE_NAME = "artifactTypeSelect";
   private final Set<String> typeNames;
   private final Set<String> preselectedTypeNames;
   private String selectId;
   private final static String theJavaScript = "<script type=\"text/javascript\">\n" + //
      "function typeSelected() {\n" + //
      "    var available = document.getElementById(\"%s\");\n" + //
      "    var selected = document.getElementById(\"%s\");\n" + //
      "    var availableOptions = available.options;\n" + //
      "    if(available.selectedIndex > -1) {\n" + //
      "        var index = available.selectedIndex\n" + //
      "        var text =  availableOptions[index].text;\n" + //
      "        if (!text) {\n" + //
      "            return;\n" + //
      "        }\n" + //
      "        var foundIt = false;\n" + //
      "        var counter = 0;\n" + //
      "        var selectedOptions = selected.options;\n" + //
      "        while ((counter < selectedOptions.length) && !foundIt) {\n" + //
      "            if (selectedOptions[counter].text == text) {\n" + //
      "                foundIt = true;\n" + //
      "            }\n" + //
      "        counter++;\n" + //
      "        }\n" + //
      "        if (!foundIt) {\n" + //
      "            var option = document.createElement(\"option\");\n" + //
      "            option.text = text;\n" + //
      "            option.value = availableOptions[index].value;\n" + //
      "            selected.add(option);\n" + //
      "        }\n" + //
      "    }\n" + //
      "    available.selectedIndex = -1;\n" + //
      "}\n" + //
      "function typeDeselected(evnt) {\n" + //
      "    var selected = document.getElementById(\"%s\");\n" + //
      "    var options = selected.options;\n" + //
      "    while(selected.selectedIndex > -1) {\n" + //
      "        selected.remove(selected.selectedIndex)\n" + //
      "    }\n" + //
      "    if (evnt) {\n" + //
      "       evnt.preventDefault();\n" + //
      "       evnt.returnValue = false;\n" + //
      "    }\n" + //
      "}\n" + //
      "</script>";

   public ArtifactTypeOptionsRule(String selectId, Set<String> typeNames, Set<String> preselectedTypeNames) {
      super(RULE_NAME);
      Conditions.checkNotNull(selectId, "selection id");
      Conditions.checkNotNull(typeNames, "types");
      Conditions.checkNotNull(preselectedTypeNames, "preseleted types");
      this.typeNames = typeNames;
      this.preselectedTypeNames = preselectedTypeNames;
      this.selectId = selectId;
   }

   @Override
   public void applyTo(Appendable appendable, Map<String, String> attributes) throws IOException {
      String value = attributes.get("id");
      if (value == null) {
         throw new IOException("no base type specified for artifact select rule");
      }
      selectId = value;
      value = attributes.get("types");
      if (value == null) {
         throw new IOException("no base type specified for artifact select rule");
      }
      parseStringToSet(value, typeNames);

      value = attributes.get("preselect");
      if (value != null) {
         parseStringToSet(value, preselectedTypeNames);
      }
      applyTo(appendable);
   }

   private void parseStringToSet(String toParse, Set<String> types) {
      StringTokenizer splitter = new StringTokenizer(toParse, ",");
      while (splitter.hasMoreTokens()) {
         types.add(splitter.nextToken().trim());
      }
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      ArrayList<String> sortedList = new ArrayList<>();
      for (String type : typeNames) {
         sortedList.add(type);
      }
      Collections.sort(sortedList);
      String availableName = selectId + "_available";
      appendable.append("<select id=\"");
      appendable.append(availableName);
      appendable.append("\" onchange=\"typeSelected()\">\n");
      /*
       * add a blank as the first option, this allows the first real option to be selected. Otherwise the first option
       * will show in the box and there will be no onchange event if it is selected.
       */
      appendable.append("<option value=\"\" selected></option>/n");
      for (String name : sortedList) {
         appendable.append("<option value=\"");
         appendable.append(name);
         appendable.append("\">");
         appendable.append(name);
         appendable.append("</option>\n");
      }
      appendable.append("</select><br>\n");
      appendable.append("<select id=\"");
      appendable.append(selectId);
      appendable.append("\" multiple oncontextmenu=\"typeDeselected(event)\">\n");
      if (preselectedTypeNames != null) {
         for (String name : preselectedTypeNames) {
            appendable.append("<option value=\"");
            appendable.append(name);
            appendable.append("\">");
            appendable.append(name);
            appendable.append("</option>\n");
         }
      }
      appendable.append("</select><br>\n");
      // Now add the JavaScript
      appendable.append(String.format(theJavaScript, availableName, selectId, selectId));
   }

}
