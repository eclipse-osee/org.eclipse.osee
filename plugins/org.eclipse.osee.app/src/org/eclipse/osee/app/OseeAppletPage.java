/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.app;

import static org.eclipse.osee.framework.core.enums.BranchState.CREATED;
import static org.eclipse.osee.framework.core.enums.BranchState.MODIFIED;
import static org.eclipse.osee.framework.core.enums.BranchType.BASELINE;
import static org.eclipse.osee.framework.core.enums.BranchType.WORKING;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.template.engine.AppendableRule;
import org.eclipse.osee.template.engine.CompositeRule;
import org.eclipse.osee.template.engine.IdentifiableLongOptionsRule;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public class OseeAppletPage {

   //example input for pattern:  <input id="selected_branch" type="text" list="baselineBranches" required/><br />
   private static final Pattern listAttributePattern = Pattern.compile("<input[^>]+?list=\"([^\"]+)");
   private final BranchQuery query;

   public OseeAppletPage(BranchQuery query) {
      this.query = query;
   }

   public String realizeApplet(String name, Class<?> clazz) {
      ResourceToken valuesToken = new ClassBasedResourceToken(name, clazz);
      return realizeApplet(null, valuesToken);
   }

   public String realizeApplet(IResourceRegistry registry, String name, Class<?> clazz) {
      ResourceToken valuesToken = new ClassBasedResourceToken(name, clazz);
      return realizeApplet(registry, valuesToken);
   }

   public String realizeApplet(IResourceRegistry registry, String name, Class<?> clazz, AppendableRule<?>... rules) {
      ResourceToken valuesToken = new ClassBasedResourceToken(name, clazz);
      PageCreator page = PageFactory.newPageCreatorWithRules(registry, valuesToken, rules);

      return realizeApplet(page);
   }

   public String realizeApplet(IResourceRegistry registry, ResourceToken valuesToken) {
      PageCreator page = PageFactory.newPageCreator(registry, valuesToken);
      return realizeApplet(page);
   }

   public String realizeApplet(PageCreator page) {
      CharSequence widgets = page.getValue("widgets");
      Matcher matcher = listAttributePattern.matcher(widgets);

      CompositeRule<IOseeBranch> dataListsRule = new CompositeRule<>("dataLists");
      while (matcher.find()) {
         String listId = matcher.group(1);
         if (listId.equals("baselineBranches") || listId.equals("workingAndBaselineBranches")) {
            if (!dataListsRule.ruleExists(listId)) {
               Iterable<IOseeBranch> options = getBranchOptions(query, listId);
               dataListsRule.addRule(new IdentifiableLongOptionsRule<IOseeBranch>("", options, listId));
            }
         }
      }
      page.addSubstitution(dataListsRule);

      return page.realizePage(OseeAppResourceTokens.OseeAppHtml);
   }

   private Iterable<IOseeBranch> getBranchOptions(BranchQuery query, String listId) {
      BranchType[] branchTypes =
         listId.equals("baselineBranches") ? new BranchType[] {BASELINE} : new BranchType[] {BASELINE, WORKING};
      query.andIsOfType(branchTypes);
      query.andStateIs(CREATED, MODIFIED);

      return query.getResultsAsId();
   }
}