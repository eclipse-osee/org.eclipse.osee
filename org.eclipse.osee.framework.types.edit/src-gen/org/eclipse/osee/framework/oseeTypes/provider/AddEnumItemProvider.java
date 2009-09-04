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

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.eclipse.osee.framework.oseeTypes.AddEnum;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.osee.framework.oseeTypes.AddEnum} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class AddEnumItemProvider
   extends OverrideOptionItemProvider
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
   public AddEnumItemProvider(AdapterFactory adapterFactory) {
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

         addEnumEntryPropertyDescriptor(object);
         addOrdinalPropertyDescriptor(object);
      }
      return itemPropertyDescriptors;
   }

   /**
    * This adds a property descriptor for the Enum Entry feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addEnumEntryPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AddEnum_enumEntry_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AddEnum_enumEntry_feature", "_UI_AddEnum_type"),
             OseeTypesPackage.Literals.ADD_ENUM__ENUM_ENTRY,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Ordinal feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addOrdinalPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AddEnum_ordinal_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AddEnum_ordinal_feature", "_UI_AddEnum_type"),
             OseeTypesPackage.Literals.ADD_ENUM__ORDINAL,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This returns AddEnum.gif.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object getImage(Object object) {
      return overlayImage(object, getResourceLocator().getImage("full/obj16/AddEnum"));
   }

   /**
    * This returns the label text for the adapted class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String getText(Object object) {
      String label = ((AddEnum)object).getEnumEntry();
      return label == null || label.length() == 0 ?
         getString("_UI_AddEnum_type") :
         getString("_UI_AddEnum_type") + " " + label;
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

      switch (notification.getFeatureID(AddEnum.class)) {
         case OseeTypesPackage.ADD_ENUM__ENUM_ENTRY:
         case OseeTypesPackage.ADD_ENUM__ORDINAL:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
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
   }

}
