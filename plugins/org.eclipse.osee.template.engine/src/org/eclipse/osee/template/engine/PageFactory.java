/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.template.engine;

import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;

/**
 * Factory containing convenience methods for both creating HtmlPageCreator objects and directly realizing a page in a
 * single call
 * 
 * @author Ryan D. Brooks
 */
public final class PageFactory {

   public static PageCreator newPageCreator(IResourceRegistry registry, String... keyValues) {
      PageCreator page = new PageCreator(registry);
      page.addKeyValuePairs(keyValues);
      return page;
   }

   public static PageCreator newPageCreator(IResourceRegistry registry, ResourceToken valuesResource, String... keyValues) {
      PageCreator page = newPageCreator(registry, keyValues);
      page.readKeyValuePairs(valuesResource);
      return page;
   }

   public static PageCreator newPageCreatorWithRules(IResourceRegistry registry, ResourceToken valuesResource, AppendableRule<?>... rules) {
      PageCreator page = newPageCreator(registry);
      page.addSubstitution(rules);
      page.readKeyValuePairs(valuesResource);
      return page;
   }

   public static PageCreator newPageCreator(IResourceRegistry registry, Iterable<String> keyValues) {
      PageCreator page = new PageCreator(registry);
      page.addKeyValuePairs(keyValues);
      return page;
   }

   public static PageCreator newPageCreator(IResourceRegistry registry, ResourceToken valuesResource, Iterable<String> keyValues) {
      PageCreator page = newPageCreator(registry, keyValues);
      page.readKeyValuePairs(valuesResource);
      return page;
   }

   public static String realizePage(IResourceRegistry registry, ResourceToken templateResource) {
      return realizePage(registry, templateResource, new String[0]);
   }

   public static String realizePage(IResourceRegistry registry, ResourceToken templateResource, String... keyValues) {
      PageCreator page = newPageCreator(registry, keyValues);
      return page.realizePage(templateResource);
   }

   public static String realizePage(IResourceRegistry registry, ResourceToken templateResource, AppendableRule<?>... rules) {
      PageCreator page = newPageCreator(registry);
      page.addSubstitution(rules);
      return page.realizePage(templateResource);
   }

   public static void realizePage(IResourceRegistry registry, ResourceToken templateResource, Appendable output, String... keyValues) {
      PageCreator page = newPageCreator(registry, keyValues);
      page.realizePage(templateResource, output);
   }

   public static String realizePage(IResourceRegistry registry, ResourceToken templateResource, ResourceToken valuesResource, String... keyValues) {
      PageCreator page = newPageCreator(registry, valuesResource, keyValues);
      return page.realizePage(templateResource);
   }

   public static String realizePage(IResourceRegistry registry, ResourceToken templateResource, Iterable<String> keyValues) {
      PageCreator page = newPageCreator(registry, keyValues);
      return page.realizePage(templateResource);
   }

   public static String realizePage(IResourceRegistry registry, ResourceToken templateResource, ResourceToken valuesResource, Iterable<String> keyValues) {
      PageCreator page = newPageCreator(registry, valuesResource, keyValues);
      return page.realizePage(templateResource);
   }

   public static String realizePage(String name, Class<?> clazz, String... keyValues) {
      return realizePage(null, name, clazz, keyValues);
   }

   public static String realizePage(IResourceRegistry registry, String name, Class<?> clazz, String... keyValues) {
      ResourceToken templateResource = new ClassBasedResourceToken(name, clazz);
      return realizePage(registry, templateResource, keyValues);
   }

   public static String realizePage(String name, Class<?> clazz) {
      return realizePage(null, name, clazz, new String[0]);
   }

   public static String realizePage(String name, Class<?> clazz, AppendableRule<?>... rules) {
      return realizePage(null, name, clazz, rules);
   }

   public static String realizePage(IResourceRegistry registry, String name, Class<?> clazz, AppendableRule<?>... rules) {
      ResourceToken templateResource = new ClassBasedResourceToken(name, clazz);
      return realizePage(registry, templateResource, rules);
   }
}