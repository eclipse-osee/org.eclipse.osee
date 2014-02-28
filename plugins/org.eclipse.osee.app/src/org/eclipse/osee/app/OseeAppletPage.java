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
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.template.engine.CompositeRule;
import org.eclipse.osee.template.engine.IdentifiableOptionsRule;
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

   public String realizeApplet(IResourceRegistry registry, ResourceToken valuesResource) {
      PageCreator page = PageFactory.newPageCreator(registry);
      return realizeApplet(valuesResource, page);
   }

   public String realizeApplet(ResourceToken valuesResource, PageCreator page) {
      page.readKeyValuePairs(valuesResource);
      return realizeApplet(page);
   }

   public String realizeApplet(PageCreator page) {
      CharSequence widgets = page.getValue("widgets");
      Matcher matcher = listAttributePattern.matcher(widgets);

      CompositeRule<BranchReadable> dataListsRule = new CompositeRule<BranchReadable>("dataLists");
      while (matcher.find()) {
         String listId = matcher.group(1);
         if (listId.equals("baselineBranches") || listId.equals("workingAndBaselineBranches")) {
            if (!dataListsRule.ruleExists(listId)) {
               Iterable<BranchReadable> options = getBranchOptions(query, listId);
               dataListsRule.addRule(new IdentifiableOptionsRule<BranchReadable>("", options, listId));
            }
         }
      }
      page.addSubstitution(dataListsRule);

      return page.realizePage(OseeAppResourceTokens.OseeAppHtml);
   }

   private Iterable<BranchReadable> getBranchOptions(BranchQuery query, String listId) {
      BranchType[] branchTypes =
         listId.equals("baselineBranches") ? new BranchType[] {BASELINE} : new BranchType[] {BASELINE, WORKING};
      query.andIsOfType(branchTypes);
      query.andStateIs(CREATED, MODIFIED);

      return query.getResults();
   }
}