/*
 * Created on Nov 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Provides enum like capabilities to Work Pages (like ordinal, values and valueOf) while providing for the inheritance
 * of classes.
 * 
 * @author Donald G. Dunne
 */
public abstract class WorkPageAdapter implements IWorkPage {
   private final WorkPageType workPageType;
   private static CompositeKeyHashMap<Class<?>, String, WorkPageAdapter> classAndNameToPage =
      new CompositeKeyHashMap<Class<?>, String, WorkPageAdapter>();
   private static final Map<WorkPageAdapter, Integer> pageToOrdinal = new HashMap<WorkPageAdapter, Integer>(10);
   private static final Map<Class<?>, List<WorkPageAdapter>> classToPages =
      new HashMap<Class<?>, List<WorkPageAdapter>>();
   private static final CountingMap<Class<?>> classToOrdinalCount = new CountingMap<Class<?>>(20);
   private String description;
   private final String pageName;

   public WorkPageAdapter(Class<?> clazz, String pageName, WorkPageType workPageType) {
      assert (Strings.isValid(pageName));
      assert (workPageType != null);
      this.pageName = pageName;
      this.workPageType = workPageType;
      classAndNameToPage.put(clazz, pageName, this);
      classToOrdinalCount.put(clazz);
      pageToOrdinal.put(this, classToOrdinalCount.get(clazz));
   }

   public int ordinal() {
      return pageToOrdinal.get(this);
   }

   @SuppressWarnings("unchecked")
   public static <T> T valueOfPage(Class<?> clazz, String pageName) {
      return (T) classAndNameToPage.get(clazz, pageName);
   }

   public synchronized static <T> List<T> pages(Class<?> clazz) {
      if (classToPages.get(clazz) == null) {
         Set<WorkPageAdapter> pages = new HashSet<WorkPageAdapter>();
         for (WorkPageAdapter page : pageToOrdinal.keySet()) {
            if (page.getClass().isAssignableFrom(clazz)) {
               pages.add(page);
            }
         }
         List<WorkPageAdapter> pagesOrdered = new ArrayList<WorkPageAdapter>();
         for (int x = 1; x <= pages.size(); x++) {
            for (WorkPageAdapter page : pages) {
               if (page.ordinal() == x) {
                  pagesOrdered.add(page);
               }
            }
         }
         classToPages.put(clazz, pagesOrdered);
      }
      return Collections.castAll(classToPages.get(clazz));
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public Integer getDefaultPercent() {
      return null;
   }

   @Override
   public String getPageName() {
      return pageName;
   }

   @Override
   public boolean isCompletedOrCancelledPage() {
      return getWorkPageType().isCompletedOrCancelledPage();
   }

   @Override
   public boolean isCompletedPage() {
      return getWorkPageType().isCompletedPage();
   }

   @Override
   public boolean isCancelledPage() {
      return getWorkPageType().isCancelledPage();
   }

   @Override
   public boolean isWorkingPage() {
      return getWorkPageType().isWorkingPage();
   }

   @Override
   public WorkPageType getWorkPageType() {
      return workPageType;
   }

   public void setDescription(String description) {
      this.description = description;
   }

}
