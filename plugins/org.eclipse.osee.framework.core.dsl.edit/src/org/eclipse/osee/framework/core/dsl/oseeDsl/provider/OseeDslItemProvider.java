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

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class OseeDslItemProvider
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
	public OseeDslItemProvider(AdapterFactory adapterFactory) {
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

		}
		return itemPropertyDescriptors;
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
			childrenFeatures.add(OseeDslPackage.Literals.OSEE_DSL__IMPORTS);
			childrenFeatures.add(OseeDslPackage.Literals.OSEE_DSL__ARTIFACT_TYPES);
			childrenFeatures.add(OseeDslPackage.Literals.OSEE_DSL__RELATION_TYPES);
			childrenFeatures.add(OseeDslPackage.Literals.OSEE_DSL__ATTRIBUTE_TYPES);
			childrenFeatures.add(OseeDslPackage.Literals.OSEE_DSL__ENUM_TYPES);
			childrenFeatures.add(OseeDslPackage.Literals.OSEE_DSL__ENUM_OVERRIDES);
			childrenFeatures.add(OseeDslPackage.Literals.OSEE_DSL__BRANCH_REFS);
			childrenFeatures.add(OseeDslPackage.Literals.OSEE_DSL__ARTIFACT_REFS);
			childrenFeatures.add(OseeDslPackage.Literals.OSEE_DSL__ACCESS_DECLARATIONS);
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
	 * This returns OseeDsl.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/OseeDsl"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		return getString("_UI_OseeDsl_type");
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

		switch (notification.getFeatureID(OseeDsl.class)) {
			case OseeDslPackage.OSEE_DSL__IMPORTS:
			case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPES:
			case OseeDslPackage.OSEE_DSL__RELATION_TYPES:
			case OseeDslPackage.OSEE_DSL__ATTRIBUTE_TYPES:
			case OseeDslPackage.OSEE_DSL__ENUM_TYPES:
			case OseeDslPackage.OSEE_DSL__ENUM_OVERRIDES:
			case OseeDslPackage.OSEE_DSL__BRANCH_REFS:
			case OseeDslPackage.OSEE_DSL__ARTIFACT_REFS:
			case OseeDslPackage.OSEE_DSL__ACCESS_DECLARATIONS:
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
				(OseeDslPackage.Literals.OSEE_DSL__IMPORTS,
				 OseeDslFactory.eINSTANCE.createImport()));

		newChildDescriptors.add
			(createChildParameter
				(OseeDslPackage.Literals.OSEE_DSL__ARTIFACT_TYPES,
				 OseeDslFactory.eINSTANCE.createXArtifactType()));

		newChildDescriptors.add
			(createChildParameter
				(OseeDslPackage.Literals.OSEE_DSL__RELATION_TYPES,
				 OseeDslFactory.eINSTANCE.createXRelationType()));

		newChildDescriptors.add
			(createChildParameter
				(OseeDslPackage.Literals.OSEE_DSL__ATTRIBUTE_TYPES,
				 OseeDslFactory.eINSTANCE.createXAttributeType()));

		newChildDescriptors.add
			(createChildParameter
				(OseeDslPackage.Literals.OSEE_DSL__ENUM_TYPES,
				 OseeDslFactory.eINSTANCE.createXOseeEnumType()));

		newChildDescriptors.add
			(createChildParameter
				(OseeDslPackage.Literals.OSEE_DSL__ENUM_OVERRIDES,
				 OseeDslFactory.eINSTANCE.createXOseeEnumOverride()));

		newChildDescriptors.add
			(createChildParameter
				(OseeDslPackage.Literals.OSEE_DSL__BRANCH_REFS,
				 OseeDslFactory.eINSTANCE.createXBranchRef()));

		newChildDescriptors.add
			(createChildParameter
				(OseeDslPackage.Literals.OSEE_DSL__ARTIFACT_REFS,
				 OseeDslFactory.eINSTANCE.createXArtifactRef()));

		newChildDescriptors.add
			(createChildParameter
				(OseeDslPackage.Literals.OSEE_DSL__ACCESS_DECLARATIONS,
				 OseeDslFactory.eINSTANCE.createAccessContext()));
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
