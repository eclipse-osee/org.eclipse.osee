package org.eclipse.osee.coverage.store;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public class CoverageAttributes {
   private final String displayName;
   private final String storeName;
   private final String description;
   private static Map<String, CoverageAttributes> attrNameToAttr = new HashMap<String, CoverageAttributes>();

   public enum Namespace {
      Coverage("coverage."), None("");
      private final String prefix;

      private Namespace(String prefix) {
         this.prefix = prefix;
      }

      public String getPrefix() {
         return prefix;
      }
   };

   public static final CoverageAttributes ACTIVE = new CoverageAttributes(Namespace.None, "Active");

   public static final CoverageAttributes NOTES = new CoverageAttributes(Namespace.Coverage, "Notes");
   public static final CoverageAttributes FILE_CONTENTS = new CoverageAttributes(Namespace.Coverage, "File Contents");
   public static final CoverageAttributes ASSIGNEES = new CoverageAttributes(Namespace.Coverage, "Assignees");
   public static final CoverageAttributes NAMESPACE = new CoverageAttributes(Namespace.Coverage, "Namespace");
   public static final CoverageAttributes LOCATION = new CoverageAttributes(Namespace.Coverage, "Location");
   public static final CoverageAttributes ORDER = new CoverageAttributes(Namespace.Coverage, "Order");
   public static final CoverageAttributes COVERAGE_ITEM = new CoverageAttributes(Namespace.Coverage, "Coverage Item");

   protected CoverageAttributes(String displayName, String storeName, String description) {
      this.displayName = displayName;
      this.storeName = storeName;
      this.description = description;
      attrNameToAttr.put(getStoreName(), this);
   }

   private CoverageAttributes(String displayName, String storeName) {
      this(displayName, storeName, "");
   }

   private CoverageAttributes(Namespace namespace, String name) {
      this(name, namespace.prefix + name);
   }

   public String getDisplayName() {
      return displayName;
   }

   public String getStoreName() {
      return storeName;
   }

   public String getDescription() {
      return description;
   }

}
