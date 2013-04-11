/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.osee.framework.core.dsl.oseeDsl.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage
 * @generated
 */
public class OseeDslAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static OseeDslPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeDslAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = OseeDslPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OseeDslSwitch<Adapter> modelSwitch =
    new OseeDslSwitch<Adapter>()
    {
      @Override
      public Adapter caseOseeDsl(OseeDsl object)
      {
        return createOseeDslAdapter();
      }
      @Override
      public Adapter caseImport(Import object)
      {
        return createImportAdapter();
      }
      @Override
      public Adapter caseOseeElement(OseeElement object)
      {
        return createOseeElementAdapter();
      }
      @Override
      public Adapter caseOseeType(OseeType object)
      {
        return createOseeTypeAdapter();
      }
      @Override
      public Adapter caseXArtifactType(XArtifactType object)
      {
        return createXArtifactTypeAdapter();
      }
      @Override
      public Adapter caseXAttributeTypeRef(XAttributeTypeRef object)
      {
        return createXAttributeTypeRefAdapter();
      }
      @Override
      public Adapter caseXAttributeType(XAttributeType object)
      {
        return createXAttributeTypeAdapter();
      }
      @Override
      public Adapter caseXOseeEnumType(XOseeEnumType object)
      {
        return createXOseeEnumTypeAdapter();
      }
      @Override
      public Adapter caseXOseeEnumEntry(XOseeEnumEntry object)
      {
        return createXOseeEnumEntryAdapter();
      }
      @Override
      public Adapter caseXOseeEnumOverride(XOseeEnumOverride object)
      {
        return createXOseeEnumOverrideAdapter();
      }
      @Override
      public Adapter caseOverrideOption(OverrideOption object)
      {
        return createOverrideOptionAdapter();
      }
      @Override
      public Adapter caseAddEnum(AddEnum object)
      {
        return createAddEnumAdapter();
      }
      @Override
      public Adapter caseRemoveEnum(RemoveEnum object)
      {
        return createRemoveEnumAdapter();
      }
      @Override
      public Adapter caseXOseeArtifactTypeOverride(XOseeArtifactTypeOverride object)
      {
        return createXOseeArtifactTypeOverrideAdapter();
      }
      @Override
      public Adapter caseAttributeOverrideOption(AttributeOverrideOption object)
      {
        return createAttributeOverrideOptionAdapter();
      }
      @Override
      public Adapter caseAddAttribute(AddAttribute object)
      {
        return createAddAttributeAdapter();
      }
      @Override
      public Adapter caseRemoveAttribute(RemoveAttribute object)
      {
        return createRemoveAttributeAdapter();
      }
      @Override
      public Adapter caseUpdateAttribute(UpdateAttribute object)
      {
        return createUpdateAttributeAdapter();
      }
      @Override
      public Adapter caseXRelationType(XRelationType object)
      {
        return createXRelationTypeAdapter();
      }
      @Override
      public Adapter caseCondition(Condition object)
      {
        return createConditionAdapter();
      }
      @Override
      public Adapter caseSimpleCondition(SimpleCondition object)
      {
        return createSimpleConditionAdapter();
      }
      @Override
      public Adapter caseCompoundCondition(CompoundCondition object)
      {
        return createCompoundConditionAdapter();
      }
      @Override
      public Adapter caseXArtifactMatcher(XArtifactMatcher object)
      {
        return createXArtifactMatcherAdapter();
      }
      @Override
      public Adapter caseRole(Role object)
      {
        return createRoleAdapter();
      }
      @Override
      public Adapter caseReferencedContext(ReferencedContext object)
      {
        return createReferencedContextAdapter();
      }
      @Override
      public Adapter caseUsersAndGroups(UsersAndGroups object)
      {
        return createUsersAndGroupsAdapter();
      }
      @Override
      public Adapter caseAccessContext(AccessContext object)
      {
        return createAccessContextAdapter();
      }
      @Override
      public Adapter caseHierarchyRestriction(HierarchyRestriction object)
      {
        return createHierarchyRestrictionAdapter();
      }
      @Override
      public Adapter caseRelationTypeArtifactTypePredicate(RelationTypeArtifactTypePredicate object)
      {
        return createRelationTypeArtifactTypePredicateAdapter();
      }
      @Override
      public Adapter caseRelationTypeArtifactPredicate(RelationTypeArtifactPredicate object)
      {
        return createRelationTypeArtifactPredicateAdapter();
      }
      @Override
      public Adapter caseRelationTypePredicate(RelationTypePredicate object)
      {
        return createRelationTypePredicateAdapter();
      }
      @Override
      public Adapter caseObjectRestriction(ObjectRestriction object)
      {
        return createObjectRestrictionAdapter();
      }
      @Override
      public Adapter caseArtifactMatchRestriction(ArtifactMatchRestriction object)
      {
        return createArtifactMatchRestrictionAdapter();
      }
      @Override
      public Adapter caseArtifactTypeRestriction(ArtifactTypeRestriction object)
      {
        return createArtifactTypeRestrictionAdapter();
      }
      @Override
      public Adapter caseAttributeTypeRestriction(AttributeTypeRestriction object)
      {
        return createAttributeTypeRestrictionAdapter();
      }
      @Override
      public Adapter caseLegacyRelationTypeRestriction(LegacyRelationTypeRestriction object)
      {
        return createLegacyRelationTypeRestrictionAdapter();
      }
      @Override
      public Adapter caseRelationTypeRestriction(RelationTypeRestriction object)
      {
        return createRelationTypeRestrictionAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl <em>Osee Dsl</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl
   * @generated
   */
  public Adapter createOseeDslAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Import <em>Import</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Import
   * @generated
   */
  public Adapter createImportAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement <em>Osee Element</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement
   * @generated
   */
  public Adapter createOseeElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType <em>Osee Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType
   * @generated
   */
  public Adapter createOseeTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType <em>XArtifact Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType
   * @generated
   */
  public Adapter createXArtifactTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef <em>XAttribute Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef
   * @generated
   */
  public Adapter createXAttributeTypeRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType <em>XAttribute Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType
   * @generated
   */
  public Adapter createXAttributeTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType <em>XOsee Enum Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType
   * @generated
   */
  public Adapter createXOseeEnumTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry <em>XOsee Enum Entry</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry
   * @generated
   */
  public Adapter createXOseeEnumEntryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride <em>XOsee Enum Override</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride
   * @generated
   */
  public Adapter createXOseeEnumOverrideAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption <em>Override Option</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption
   * @generated
   */
  public Adapter createOverrideOptionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum <em>Add Enum</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum
   * @generated
   */
  public Adapter createAddEnumAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum <em>Remove Enum</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum
   * @generated
   */
  public Adapter createRemoveEnumAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride <em>XOsee Artifact Type Override</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride
   * @generated
   */
  public Adapter createXOseeArtifactTypeOverrideAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeOverrideOption <em>Attribute Override Option</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeOverrideOption
   * @generated
   */
  public Adapter createAttributeOverrideOptionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AddAttribute <em>Add Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AddAttribute
   * @generated
   */
  public Adapter createAddAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveAttribute <em>Remove Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveAttribute
   * @generated
   */
  public Adapter createRemoveAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.UpdateAttribute <em>Update Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.UpdateAttribute
   * @generated
   */
  public Adapter createUpdateAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType <em>XRelation Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType
   * @generated
   */
  public Adapter createXRelationTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Condition <em>Condition</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Condition
   * @generated
   */
  public Adapter createConditionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition <em>Simple Condition</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition
   * @generated
   */
  public Adapter createSimpleConditionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition <em>Compound Condition</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition
   * @generated
   */
  public Adapter createCompoundConditionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher <em>XArtifact Matcher</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher
   * @generated
   */
  public Adapter createXArtifactMatcherAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.Role <em>Role</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.Role
   * @generated
   */
  public Adapter createRoleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext <em>Referenced Context</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext
   * @generated
   */
  public Adapter createReferencedContextAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups <em>Users And Groups</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups
   * @generated
   */
  public Adapter createUsersAndGroupsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext <em>Access Context</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext
   * @generated
   */
  public Adapter createAccessContextAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction <em>Hierarchy Restriction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction
   * @generated
   */
  public Adapter createHierarchyRestrictionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate <em>Relation Type Artifact Type Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate
   * @generated
   */
  public Adapter createRelationTypeArtifactTypePredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactPredicate <em>Relation Type Artifact Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactPredicate
   * @generated
   */
  public Adapter createRelationTypeArtifactPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypePredicate <em>Relation Type Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypePredicate
   * @generated
   */
  public Adapter createRelationTypePredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction <em>Object Restriction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction
   * @generated
   */
  public Adapter createObjectRestrictionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction <em>Artifact Match Restriction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction
   * @generated
   */
  public Adapter createArtifactMatchRestrictionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction <em>Artifact Type Restriction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction
   * @generated
   */
  public Adapter createArtifactTypeRestrictionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction <em>Attribute Type Restriction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction
   * @generated
   */
  public Adapter createAttributeTypeRestrictionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction <em>Legacy Relation Type Restriction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction
   * @generated
   */
  public Adapter createLegacyRelationTypeRestrictionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction <em>Relation Type Restriction</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction
   * @generated
   */
  public Adapter createRelationTypeRestrictionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //OseeDslAdapterFactory
