/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class LegacyRelationTypeRestrictionItemProvider
   extends ItemProviderAdapter
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
   public LegacyRelationTypeRestrictionItemProvider(AdapterFactory adapterFactory) {
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

         addPermissionPropertyDescriptor(object);
         addRelationTypeRefPropertyDescriptor(object);
         addRestrictedToSidePropertyDescriptor(object);
         addArtifactMatcherRefPropertyDescriptor(object);
      }
      return itemPropertyDescriptors;
   }

   /**
    * This adds a property descriptor for the Permission feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addPermissionPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_LegacyRelationTypeRestriction_permission_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_LegacyRelationTypeRestriction_permission_feature", "_UI_LegacyRelationTypeRestriction_type"),
             OseeDslPackage.Literals.LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Relation Type Ref feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addRelationTypeRefPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_LegacyRelationTypeRestriction_relationTypeRef_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_LegacyRelationTypeRestriction_relationTypeRef_feature", "_UI_LegacyRelationTypeRestriction_type"),
             OseeDslPackage.Literals.LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF,
             true,
             false,
             true,
             null,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Restricted To Side feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addRestrictedToSidePropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_LegacyRelationTypeRestriction_restrictedToSide_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_LegacyRelationTypeRestriction_restrictedToSide_feature", "_UI_LegacyRelationTypeRestriction_type"),
             OseeDslPackage.Literals.LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This adds a property descriptor for the Artifact Matcher Ref feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addArtifactMatcherRefPropertyDescriptor(Object object) {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_LegacyRelationTypeRestriction_artifactMatcherRef_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_LegacyRelationTypeRestriction_artifactMatcherRef_feature", "_UI_LegacyRelationTypeRestriction_type"),
             OseeDslPackage.Literals.LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF,
             true,
             false,
             true,
             null,
             null,
             null));
   }

   /**
    * This returns LegacyRelationTypeRestriction.gif.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object getImage(Object object) {
      return overlayImage(object, getResourceLocator().getImage("full/obj16/LegacyRelationTypeRestriction"));
   }

   /**
    * This returns the label text for the adapted class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String getText(Object object) {
      AccessPermissionEnum labelValue = ((LegacyRelationTypeRestriction)object).getPermission();
      String label = labelValue == null ? null : labelValue.toString();
      return label == null || label.length() == 0 ?
         getString("_UI_LegacyRelationTypeRestriction_type") :
         getString("_UI_LegacyRelationTypeRestriction_type") + " " + label;
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

      switch (notification.getFeatureID(LegacyRelationTypeRestriction.class)) {
         case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION:
         case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE:
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

   /**
    * Return the resource locator for this item provider's resources.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public ResourceLocator getResourceLocator() {
      return OseeDslEditPlugin.INSTANCE;
   }

}
