/*
 * Created on Nov 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.util.enumeration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Provides enum like capabilities (like ordinal, values and valueOf) while providing for the inheritance of classes.
 * 
 * @author Donald G. Dunne
 */
public abstract class AbstractEnumeration implements IEnum {
   private static CompositeKeyHashMap<Class<?>, String, AbstractEnumeration> classAndNameToPage =
      new CompositeKeyHashMap<Class<?>, String, AbstractEnumeration>();
   private static final Map<AbstractEnumeration, Integer> pageToOrdinal = new HashMap<AbstractEnumeration, Integer>(10);
   private static final CountingMap<Class<?>> classToOrdinalCount = new CountingMap<Class<?>>(20);
   private String description;
   private final String name;

   public AbstractEnumeration(Class<?> clazz, String name) {
      assert (Strings.isValid(name));
      this.name = name;
      classAndNameToPage.put(clazz, name, this);
      classToOrdinalCount.put(clazz);
      pageToOrdinal.put(this, classToOrdinalCount.get(clazz));
   }

   @Override
   public int ordinal() {
      return pageToOrdinal.get(this);
   }

   @SuppressWarnings("unchecked")
   public static <T> T valueOfPage(Class<?> clazz, String name) {
      return (T) classAndNameToPage.get(clazz, name);
   }

   @SuppressWarnings("unchecked")
   public static <T> Set<T> pages(Class<?> clazz) {
      Set<T> pages = new HashSet<T>();
      for (AbstractEnumeration page : pageToOrdinal.keySet()) {
         if (page.getClass().isAssignableFrom(clazz)) {
            pages.add((T) page);
         }
      }
      return pages;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public String name() {
      return name;
   }

   public void setDescription(String description) {
      this.description = description;
   }

}
