/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.text.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;

/**
 * @author Ryan D. Brooks
 */
public class InsertTypeGuids extends Rule {
   private static final Pattern classDeclarationP = Pattern.compile("Type \"([^\"]*)\".*?\\{\\s*"); //Type "([^"]*)".*?\{
   private static final Map<String, String> typeGuids = new HashMap<>();

   public InsertTypeGuids() {
      super(null);
      //      super("oseetemp");
      setFileNamePattern(".*\\.osee");
      typeGuids.put("Phone", "AAMFEbUkVSwKu4LSpWAA");
      typeGuids.put("Mobile Phone", "AAMFEbWi7AIC1z82PxQA");
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);

      Matcher matcher = classDeclarationP.matcher(seq);
      while (matcher.find()) {
         setRuleWasApplicable(true);
         String typeName = matcher.group(1);
         String typeGuid = typeGuids.get(typeName);
         if (typeGuid == null) {
            if (!typeName.contains(".enum") && !typeName.contains("demo") && !typeName.contains("Demo")) {
               System.out.println(String.format("%s => %s", typeName, typeGuid));
            }
         } else {
            String ins = String.format("typeGuid \"%s\"\r\n\t", typeGuid);
            changeSet.insertBefore(matcher.end(), ins);
         }
      }

      return changeSet;
   }
}