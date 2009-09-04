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

import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;
import org.eclipse.osee.framework.oseeTypes.RelationType;

/**
 * This is the item provider adapter for a {@link org.eclipse.osee.framework.oseeTypes.RelationType} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class RelationTypeItemProvider
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
   public RelationTypeItemProvider(AdapterFactory adapterFactory) {
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

         addSideANamePropertyDescriptor(object);
         addSideAArtifactTypePropertyDescriptor(object);
         addSideBNamePropertyDescriptor(object);
         addSideBArtifactTypePropertyDescriptor(object);
         addDefaultOrderTypePropertyDescriptor(object);
         addMultiplicityPropertyDescriptor(object);
      }
      return itemPropertyDescriptors;
   }

   /**
    * This adds a property descriptor for the Side AName feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addSideANamePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_RelationType_sideAName_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_RelationType_sideAName_feature", "_UI_RelationType_type"),
             OseeTypesPackage.Literals.RELATION_TYPE__SIDE_ANAME,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Side AArtifact Type feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addSideAArtifactTypePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_RelationType_sideAArtifactType_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_RelationType_sideAArtifactType_feature", "_UI_RelationType_type"),
             OseeTypesPackage.Literals.RELATION_TYPE__SIDE_AARTIFACT_TYPE,
             true,
             false,
             true,
             null,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Side BName feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addSideBNamePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_RelationType_sideBName_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_RelationType_sideBName_feature", "_UI_RelationType_type"),
             OseeTypesPackage.Literals.RELATION_TYPE__SIDE_BNAME,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Side BArtifact Type feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addSideBArtifactTypePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_RelationType_sideBArtifactType_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_RelationType_sideBArtifactType_feature", "_UI_RelationType_type"),
             OseeTypesPackage.Literals.RELATION_TYPE__SIDE_BARTIFACT_TYPE,
             true,
             false,
             true,
             null,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Default Order Type feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addDefaultOrderTypePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_RelationType_defaultOrderType_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_RelationType_defaultOrderType_feature", "_UI_RelationType_type"),
             OseeTypesPackage.Literals.RELATION_TYPE__DEFAULT_ORDER_TYPE,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Multiplicity feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addMultiplicityPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_RelationType_multiplicity_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_RelationType_multiplicity_feature", "_UI_RelationType_type"),
             OseeTypesPackage.Literals.RELATION_TYPE__MULTIPLICITY,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This returns RelationType.gif.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object getImage(Object object) {
      return overlayImage(object, getResourceLocator().getImage("full/obj16/RelationType"));
   }

   /**
    * This returns the label text for the adapted class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String getText(Object object) {
      String label = ((RelationType)object).getName();
      return label == null || label.length() == 0 ?
         getString("_UI_RelationType_type") :
         getString("_UI_RelationType_type") + " " + label;
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

      switch (notification.getFeatureID(RelationType.class)) {
         case OseeTypesPackage.RELATION_TYPE__SIDE_ANAME:
         case OseeTypesPackage.RELATION_TYPE__SIDE_BNAME:
         case OseeTypesPackage.RELATION_TYPE__DEFAULT_ORDER_TYPE:
         case OseeTypesPackage.RELATION_TYPE__MULTIPLICITY:
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
