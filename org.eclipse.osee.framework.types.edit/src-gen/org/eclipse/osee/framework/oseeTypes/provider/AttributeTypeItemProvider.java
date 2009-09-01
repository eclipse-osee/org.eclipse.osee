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

import org.eclipse.osee.framework.oseeTypes.AttributeType;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.osee.framework.oseeTypes.AttributeType} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class AttributeTypeItemProvider
   extends OseeTypeItemProvider
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
   public AttributeTypeItemProvider(AdapterFactory adapterFactory) {
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

         addBaseAttributeTypePropertyDescriptor(object);
         addOverridePropertyDescriptor(object);
         addDataProviderPropertyDescriptor(object);
         addMinPropertyDescriptor(object);
         addMaxPropertyDescriptor(object);
         addTaggerIdPropertyDescriptor(object);
         addEnumTypePropertyDescriptor(object);
         addDescriptionPropertyDescriptor(object);
         addDefaultValuePropertyDescriptor(object);
         addFileExtensionPropertyDescriptor(object);
      }
      return itemPropertyDescriptors;
   }

   /**
    * This adds a property descriptor for the Base Attribute Type feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addBaseAttributeTypePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_baseAttributeType_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_baseAttributeType_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__BASE_ATTRIBUTE_TYPE,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Override feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addOverridePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_override_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_override_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__OVERRIDE,
             true,
             false,
             true,
             null,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Data Provider feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addDataProviderPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_dataProvider_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_dataProvider_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__DATA_PROVIDER,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Min feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addMinPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_min_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_min_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__MIN,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Max feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addMaxPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_max_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_max_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__MAX,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Tagger Id feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addTaggerIdPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_taggerId_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_taggerId_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__TAGGER_ID,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Enum Type feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addEnumTypePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_enumType_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_enumType_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__ENUM_TYPE,
             true,
             false,
             true,
             null,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Description feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addDescriptionPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_description_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_description_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__DESCRIPTION,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Default Value feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addDefaultValuePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_defaultValue_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_defaultValue_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__DEFAULT_VALUE,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the File Extension feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addFileExtensionPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_AttributeType_fileExtension_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_AttributeType_fileExtension_feature", "_UI_AttributeType_type"),
             OseeTypesPackage.Literals.ATTRIBUTE_TYPE__FILE_EXTENSION,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This returns AttributeType.gif.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object getImage(Object object) {
      return overlayImage(object, getResourceLocator().getImage("full/obj16/AttributeType"));
   }

   /**
    * This returns the label text for the adapted class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String getText(Object object) {
      String label = ((AttributeType)object).getName();
      return label == null || label.length() == 0 ?
         getString("_UI_AttributeType_type") :
         getString("_UI_AttributeType_type") + " " + label;
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

      switch (notification.getFeatureID(AttributeType.class)) {
         case OseeTypesPackage.ATTRIBUTE_TYPE__BASE_ATTRIBUTE_TYPE:
         case OseeTypesPackage.ATTRIBUTE_TYPE__DATA_PROVIDER:
         case OseeTypesPackage.ATTRIBUTE_TYPE__MIN:
         case OseeTypesPackage.ATTRIBUTE_TYPE__MAX:
         case OseeTypesPackage.ATTRIBUTE_TYPE__TAGGER_ID:
         case OseeTypesPackage.ATTRIBUTE_TYPE__DESCRIPTION:
         case OseeTypesPackage.ATTRIBUTE_TYPE__DEFAULT_VALUE:
         case OseeTypesPackage.ATTRIBUTE_TYPE__FILE_EXTENSION:
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
