/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.osee.framework.oseeTypes.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.eclipse.osee.framework.oseeTypes.OseeEnumOverride;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.osee.framework.oseeTypes.OseeEnumOverride} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class OseeEnumOverrideItemProvider
   extends OseeElementItemProvider
   implements
      IEditingDomainItemProvider,
      IStructuredItemContentProvider,
      ITreeItemContentProvider,
      IItemLabelProvider,
      IItemPropertySource {
   /**
    * This constructs an instance from a factory and a notifier.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public OseeEnumOverrideItemProvider(AdapterFactory adapterFactory) {
      super(adapterFactory);
   }

   /**
    * This returns the property descriptors for the adapted class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
      if (itemPropertyDescriptors == null) {
         super.getPropertyDescriptors(object);

         addOverridenEnumTypePropertyDescriptor(object);
         addInheritAllPropertyDescriptor(object);
      }
      return itemPropertyDescriptors;
   }

   /**
    * This adds a property descriptor for the Overriden Enum Type feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addOverridenEnumTypePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_OseeEnumOverride_overridenEnumType_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_OseeEnumOverride_overridenEnumType_feature", "_UI_OseeEnumOverride_type"),
             OseeTypesPackage.Literals.OSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE,
             true,
             false,
             true,
             null,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Inherit All feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addInheritAllPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_OseeEnumOverride_inheritAll_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_OseeEnumOverride_inheritAll_feature", "_UI_OseeEnumOverride_type"),
             OseeTypesPackage.Literals.OSEE_ENUM_OVERRIDE__INHERIT_ALL,
             true,
             false,
             false,
             ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
    * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
    * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
      if (childrenFeatures == null) {
         super.getChildrenFeatures(object);
         childrenFeatures.add(OseeTypesPackage.Literals.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS);
      }
      return childrenFeatures;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected EStructuralFeature getChildFeature(Object object, Object child) {
      // Check the type of the specified child object and return the proper feature to use for
      // adding (see {@link AddCommand}) it as a child.

      return super.getChildFeature(object, child);
   }

   /**
    * This returns OseeEnumOverride.gif.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object getImage(Object object) {
      return overlayImage(object, getResourceLocator().getImage("full/obj16/OseeEnumOverride"));
   }

   /**
    * This returns the label text for the adapted class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String getText(Object object) {
      OseeEnumOverride oseeEnumOverride = (OseeEnumOverride)object;
      return getString("_UI_OseeEnumOverride_type") + " " + oseeEnumOverride.isInheritAll();
   }

   /**
    * This handles model notifications by calling {@link #updateChildren} to update any cached
    * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void notifyChanged(Notification notification) {
      updateChildren(notification);

      switch (notification.getFeatureID(OseeEnumOverride.class)) {
         case OseeTypesPackage.OSEE_ENUM_OVERRIDE__INHERIT_ALL:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
            return;
         case OseeTypesPackage.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
            return;
      }
      super.notifyChanged(notification);
   }

   /**
    * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
    * that can be created under this object.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
      super.collectNewChildDescriptors(newChildDescriptors, object);

      newChildDescriptors.add
         (createChildParameter
            (OseeTypesPackage.Literals.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS,
             OseeTypesFactory.eINSTANCE.createOverrideOption()));

      newChildDescriptors.add
         (createChildParameter
            (OseeTypesPackage.Literals.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS,
             OseeTypesFactory.eINSTANCE.createAddEnum()));

      newChildDescriptors.add
         (createChildParameter
            (OseeTypesPackage.Literals.OSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS,
             OseeTypesFactory.eINSTANCE.createRemoveEnum()));
   }

}
