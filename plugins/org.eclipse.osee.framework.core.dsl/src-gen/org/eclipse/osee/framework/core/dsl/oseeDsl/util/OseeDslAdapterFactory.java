/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Import;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption;
import org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage
 * @generated
 */
public class OseeDslAdapterFactory extends AdapterFactoryImpl {
   /**
    * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected static OseeDslPackage modelPackage;

   /**
    * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public OseeDslAdapterFactory() {
      if (modelPackage == null) {
         modelPackage = OseeDslPackage.eINSTANCE;
      }
   }

   /**
    * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation
    * returns <code>true</code> if the object is either the model's package or is an instance object of the model. <!--
    * end-user-doc -->
    * 
    * @return whether this factory is applicable for the type of the object.
    * @generated
    */
   @Override
   public boolean isFactoryForType(Object object) {
      if (object == modelPackage) {
         return true;
      }
      if (object instanceof EObject) {
         return ((EObject) object).eClass().getEPackage() == modelPackage;
      }
      return false;
   }

   /**
    * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OseeDslSwitch<Adapter> modelSwitch = new OseeDslSwitch<Adapter>() {
      @Override
      public Adapter caseOseeDsl(OseeDsl object) {
         return createOseeDslAdapter();
      }

      @Override
      public Adapter caseImport(Import object) {
         return createImportAdapter();
      }

      @Override
      public Adapter caseOseeElement(OseeElement object) {
         return createOseeElementAdapter();
      }

      @Override
      public Adapter caseOseeType(OseeType object) {
         return createOseeTypeAdapter();
      }

      @Override
      public Adapter caseXArtifactType(XArtifactType object) {
         return createXArtifactTypeAdapter();
      }

      @Override
      public Adapter caseXAttributeTypeRef(XAttributeTypeRef object) {
         return createXAttributeTypeRefAdapter();
      }

      @Override
      public Adapter caseXAttributeType(XAttributeType object) {
         return createXAttributeTypeAdapter();
      }

      @Override
      public Adapter caseXOseeEnumType(XOseeEnumType object) {
         return createXOseeEnumTypeAdapter();
      }

      @Override
      public Adapter caseXOseeEnumEntry(XOseeEnumEntry object) {
         return createXOseeEnumEntryAdapter();
      }

      @Override
      public Adapter caseXOseeEnumOverride(XOseeEnumOverride object) {
         return createXOseeEnumOverrideAdapter();
      }

      @Override
      public Adapter caseOverrideOption(OverrideOption object) {
         return createOverrideOptionAdapter();
      }

      @Override
      public Adapter caseAddEnum(AddEnum object) {
         return createAddEnumAdapter();
      }

      @Override
      public Adapter caseRemoveEnum(RemoveEnum object) {
         return createRemoveEnumAdapter();
      }

      @Override
      public Adapter caseXRelationType(XRelationType object) {
         return createXRelationTypeAdapter();
      }

      @Override
      public Adapter caseXArtifactRef(XArtifactRef object) {
         return createXArtifactRefAdapter();
      }

      @Override
      public Adapter caseXBranchRef(XBranchRef object) {
         return createXBranchRefAdapter();
      }

      @Override
      public Adapter caseAccessContext(AccessContext object) {
         return createAccessContextAdapter();
      }

      @Override
      public Adapter caseHierarchyRestriction(HierarchyRestriction object) {
         return createHierarchyRestrictionAdapter();
      }

      @Override
      public Adapter casePermissionRule(PermissionRule object) {
         return createPermissionRuleAdapter();
      }

      @Override
      public Adapter caseObjectRestriction(ObjectRestriction object) {
         return createObjectRestrictionAdapter();
      }

      @Override
      public Adapter caseArtifactInstanceRestriction(ArtifactInstanceRestriction object) {
         return createArtifactInstanceRestrictionAdapter();
      }

      @Override
      public Adapter caseArtifactTypeRestriction(ArtifactTypeRestriction object) {
         return createArtifactTypeRestrictionAdapter();
      }

      @Override
      public Adapter caseRelationTypeRestriction(RelationTypeRestriction object) {
         return createRelationTypeRestrictionAdapter();
      }

      @Override
      public Adapter caseAttributeTypeRestriction(AttributeTypeRestriction object) {
         return createAttributeTypeRestrictionAdapter();
      }

      @Override
      public Adapter caseAttributeTypeOfArtifactTypeRestriction(AttributeTypeOfArtifactTypeRestriction object) {
         return createAttributeTypeOfArtifactTypeRestrictionAdapter();
      }

      @Override
      public Adapter defaultCase(EObject object) {
         return createEObjectAdapter();
      }
   };

   /**
    * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param target the object to adapt.
    * @return the adapter for the <code>target</code>.
    * @generated
    */
   @Override
   public Adapter createAdapter(Notifier target) {
      return modelSwitch.doSwitch((EObject) target);
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl
    * <em>Osee Dsl</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
    * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl
    * @generated
    */
   public Adapter createOseeDslAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Import
    * <em>Import</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
    * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Import
    * @generated
    */
   public Adapter createImportAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement
    * <em>Osee Element</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement
    * @generated
    */
   public Adapter createOseeElementAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType
    * <em>Osee Type</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType
    * @generated
    */
   public Adapter createOseeTypeAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType
    * <em>XArtifact Type</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType
    * @generated
    */
   public Adapter createXArtifactTypeAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef <em>XAttribute Type Ref</em>}'. <!--
    * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
    * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef
    * @generated
    */
   public Adapter createXAttributeTypeRefAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType
    * <em>XAttribute Type</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType
    * @generated
    */
   public Adapter createXAttributeTypeAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType
    * <em>XOsee Enum Type</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType
    * @generated
    */
   public Adapter createXOseeEnumTypeAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry
    * <em>XOsee Enum Entry</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
    * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
    * end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry
    * @generated
    */
   public Adapter createXOseeEnumEntryAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride <em>XOsee Enum Override</em>}'. <!--
    * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
    * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride
    * @generated
    */
   public Adapter createXOseeEnumOverrideAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption
    * <em>Override Option</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption
    * @generated
    */
   public Adapter createOverrideOptionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum
    * <em>Add Enum</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
    * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum
    * @generated
    */
   public Adapter createAddEnumAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum
    * <em>Remove Enum</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum
    * @generated
    */
   public Adapter createRemoveEnumAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType
    * <em>XRelation Type</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType
    * @generated
    */
   public Adapter createXRelationTypeAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef
    * <em>XArtifact Ref</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef
    * @generated
    */
   public Adapter createXArtifactRefAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef
    * <em>XBranch Ref</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef
    * @generated
    */
   public Adapter createXBranchRefAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext
    * <em>Access Context</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext
    * @generated
    */
   public Adapter createAccessContextAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction <em>Hierarchy Restriction</em>}'. <!--
    * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
    * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction
    * @generated
    */
   public Adapter createHierarchyRestrictionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule
    * <em>Permission Rule</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
    * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule
    * @generated
    */
   public Adapter createPermissionRuleAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction <em>Object Restriction</em>}'. <!--
    * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
    * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction
    * @generated
    */
   public Adapter createObjectRestrictionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction
    * <em>Artifact Instance Restriction</em>}'. <!-- begin-user-doc --> This default implementation returns null so that
    * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
    * end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction
    * @generated
    */
   public Adapter createArtifactInstanceRestrictionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction <em>Artifact Type Restriction</em>}'.
    * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
    * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction
    * @generated
    */
   public Adapter createArtifactTypeRestrictionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction <em>Relation Type Restriction</em>}'.
    * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
    * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction
    * @generated
    */
   public Adapter createRelationTypeRestrictionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction <em>Attribute Type Restriction</em>}'.
    * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
    * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction
    * @generated
    */
   public Adapter createAttributeTypeRestrictionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction
    * <em>Attribute Type Of Artifact Type Restriction</em>}'. <!-- begin-user-doc --> This default implementation
    * returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the
    * cases anyway. <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction
    * @generated
    */
   public Adapter createAttributeTypeOfArtifactTypeRestrictionAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!--
    * end-user-doc -->
    * 
    * @return the new adapter.
    * @generated
    */
   public Adapter createEObjectAdapter() {
      return null;
   }

} //OseeDslAdapterFactory
