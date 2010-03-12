/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
   private static final Map<String, String> typeGuids = new HashMap<String, String>();

   public InsertTypeGuids() {
      super(null);
      //      super("oseetemp");
      setFileNamePattern(Pattern.compile(".*\\.osee"));
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