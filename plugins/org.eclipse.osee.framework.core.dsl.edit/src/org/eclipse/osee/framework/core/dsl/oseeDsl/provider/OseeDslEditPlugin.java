/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.provider;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;

/**
 * This is the central singleton for the OseeDsl edit plugin. <!-- begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public final class OseeDslEditPlugin extends EMFPlugin {
   /**
    * Keep track of the singleton. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public static final OseeDslEditPlugin INSTANCE = new OseeDslEditPlugin();

   /**
    * Keep track of the singleton. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private static Implementation plugin;

   /**
    * Create the instance. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public OseeDslEditPlugin() {
      super(new ResourceLocator[] {});
   }

   /**
    * Returns the singleton instance of the Eclipse plugin. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @return the singleton instance.
    * @generated
    */
   @Override
   public ResourceLocator getPluginResourceLocator() {
      return plugin;
   }

   /**
    * Returns the singleton instance of the Eclipse plugin. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @return the singleton instance.
    * @generated
    */
   public static Implementation getPlugin() {
      return plugin;
   }

   /**
    * The actual implementation of the Eclipse <b>Plugin</b>. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public static class Implementation extends EclipsePlugin {
      /**
       * Creates an instance. <!-- begin-user-doc --> <!-- end-user-doc -->
       * 
       * @generated
       */
      public Implementation() {
         super();

         // Remember the static instance.
         //
         plugin = this;
      }
   }

}
