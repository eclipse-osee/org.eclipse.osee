/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.osee.framework.core.dsl.oseeDsl.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage
 * @generated
 */
public class OseeDslSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static OseeDslPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeDslSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = OseeDslPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case OseeDslPackage.OSEE_DSL:
      {
        OseeDsl oseeDsl = (OseeDsl)theEObject;
        T result = caseOseeDsl(oseeDsl);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.IMPORT:
      {
        Import import_ = (Import)theEObject;
        T result = caseImport(import_);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.OSEE_ELEMENT:
      {
        OseeElement oseeElement = (OseeElement)theEObject;
        T result = caseOseeElement(oseeElement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.OSEE_TYPE:
      {
        OseeType oseeType = (OseeType)theEObject;
        T result = caseOseeType(oseeType);
        if (result == null) result = caseOseeElement(oseeType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XARTIFACT_TYPE:
      {
        XArtifactType xArtifactType = (XArtifactType)theEObject;
        T result = caseXArtifactType(xArtifactType);
        if (result == null) result = caseOseeType(xArtifactType);
        if (result == null) result = caseOseeElement(xArtifactType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XATTRIBUTE_TYPE_REF:
      {
        XAttributeTypeRef xAttributeTypeRef = (XAttributeTypeRef)theEObject;
        T result = caseXAttributeTypeRef(xAttributeTypeRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XATTRIBUTE_TYPE:
      {
        XAttributeType xAttributeType = (XAttributeType)theEObject;
        T result = caseXAttributeType(xAttributeType);
        if (result == null) result = caseOseeType(xAttributeType);
        if (result == null) result = caseOseeElement(xAttributeType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XOSEE_ENUM_TYPE:
      {
        XOseeEnumType xOseeEnumType = (XOseeEnumType)theEObject;
        T result = caseXOseeEnumType(xOseeEnumType);
        if (result == null) result = caseOseeType(xOseeEnumType);
        if (result == null) result = caseOseeElement(xOseeEnumType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XOSEE_ENUM_ENTRY:
      {
        XOseeEnumEntry xOseeEnumEntry = (XOseeEnumEntry)theEObject;
        T result = caseXOseeEnumEntry(xOseeEnumEntry);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE:
      {
        XOseeEnumOverride xOseeEnumOverride = (XOseeEnumOverride)theEObject;
        T result = caseXOseeEnumOverride(xOseeEnumOverride);
        if (result == null) result = caseOseeElement(xOseeEnumOverride);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.OVERRIDE_OPTION:
      {
        OverrideOption overrideOption = (OverrideOption)theEObject;
        T result = caseOverrideOption(overrideOption);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.ADD_ENUM:
      {
        AddEnum addEnum = (AddEnum)theEObject;
        T result = caseAddEnum(addEnum);
        if (result == null) result = caseOverrideOption(addEnum);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.REMOVE_ENUM:
      {
        RemoveEnum removeEnum = (RemoveEnum)theEObject;
        T result = caseRemoveEnum(removeEnum);
        if (result == null) result = caseOverrideOption(removeEnum);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XOSEE_ARTIFACT_TYPE_OVERRIDE:
      {
        XOseeArtifactTypeOverride xOseeArtifactTypeOverride = (XOseeArtifactTypeOverride)theEObject;
        T result = caseXOseeArtifactTypeOverride(xOseeArtifactTypeOverride);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.ATTRIBUTE_OVERRIDE_OPTION:
      {
        AttributeOverrideOption attributeOverrideOption = (AttributeOverrideOption)theEObject;
        T result = caseAttributeOverrideOption(attributeOverrideOption);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.ADD_ATTRIBUTE:
      {
        AddAttribute addAttribute = (AddAttribute)theEObject;
        T result = caseAddAttribute(addAttribute);
        if (result == null) result = caseAttributeOverrideOption(addAttribute);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.REMOVE_ATTRIBUTE:
      {
        RemoveAttribute removeAttribute = (RemoveAttribute)theEObject;
        T result = caseRemoveAttribute(removeAttribute);
        if (result == null) result = caseAttributeOverrideOption(removeAttribute);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.UPDATE_ATTRIBUTE:
      {
        UpdateAttribute updateAttribute = (UpdateAttribute)theEObject;
        T result = caseUpdateAttribute(updateAttribute);
        if (result == null) result = caseAttributeOverrideOption(updateAttribute);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XRELATION_TYPE:
      {
        XRelationType xRelationType = (XRelationType)theEObject;
        T result = caseXRelationType(xRelationType);
        if (result == null) result = caseOseeType(xRelationType);
        if (result == null) result = caseOseeElement(xRelationType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.CONDITION:
      {
        Condition condition = (Condition)theEObject;
        T result = caseCondition(condition);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.SIMPLE_CONDITION:
      {
        SimpleCondition simpleCondition = (SimpleCondition)theEObject;
        T result = caseSimpleCondition(simpleCondition);
        if (result == null) result = caseCondition(simpleCondition);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.COMPOUND_CONDITION:
      {
        CompoundCondition compoundCondition = (CompoundCondition)theEObject;
        T result = caseCompoundCondition(compoundCondition);
        if (result == null) result = caseCondition(compoundCondition);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XARTIFACT_MATCHER:
      {
        XArtifactMatcher xArtifactMatcher = (XArtifactMatcher)theEObject;
        T result = caseXArtifactMatcher(xArtifactMatcher);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.ROLE:
      {
        Role role = (Role)theEObject;
        T result = caseRole(role);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.REFERENCED_CONTEXT:
      {
        ReferencedContext referencedContext = (ReferencedContext)theEObject;
        T result = caseReferencedContext(referencedContext);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.USERS_AND_GROUPS:
      {
        UsersAndGroups usersAndGroups = (UsersAndGroups)theEObject;
        T result = caseUsersAndGroups(usersAndGroups);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.ACCESS_CONTEXT:
      {
        AccessContext accessContext = (AccessContext)theEObject;
        T result = caseAccessContext(accessContext);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.HIERARCHY_RESTRICTION:
      {
        HierarchyRestriction hierarchyRestriction = (HierarchyRestriction)theEObject;
        T result = caseHierarchyRestriction(hierarchyRestriction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.RELATION_TYPE_ARTIFACT_TYPE_PREDICATE:
      {
        RelationTypeArtifactTypePredicate relationTypeArtifactTypePredicate = (RelationTypeArtifactTypePredicate)theEObject;
        T result = caseRelationTypeArtifactTypePredicate(relationTypeArtifactTypePredicate);
        if (result == null) result = caseRelationTypePredicate(relationTypeArtifactTypePredicate);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.RELATION_TYPE_ARTIFACT_PREDICATE:
      {
        RelationTypeArtifactPredicate relationTypeArtifactPredicate = (RelationTypeArtifactPredicate)theEObject;
        T result = caseRelationTypeArtifactPredicate(relationTypeArtifactPredicate);
        if (result == null) result = caseRelationTypePredicate(relationTypeArtifactPredicate);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.RELATION_TYPE_PREDICATE:
      {
        RelationTypePredicate relationTypePredicate = (RelationTypePredicate)theEObject;
        T result = caseRelationTypePredicate(relationTypePredicate);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.OBJECT_RESTRICTION:
      {
        ObjectRestriction objectRestriction = (ObjectRestriction)theEObject;
        T result = caseObjectRestriction(objectRestriction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.ARTIFACT_MATCH_RESTRICTION:
      {
        ArtifactMatchRestriction artifactMatchRestriction = (ArtifactMatchRestriction)theEObject;
        T result = caseArtifactMatchRestriction(artifactMatchRestriction);
        if (result == null) result = caseObjectRestriction(artifactMatchRestriction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.ARTIFACT_TYPE_RESTRICTION:
      {
        ArtifactTypeRestriction artifactTypeRestriction = (ArtifactTypeRestriction)theEObject;
        T result = caseArtifactTypeRestriction(artifactTypeRestriction);
        if (result == null) result = caseObjectRestriction(artifactTypeRestriction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION:
      {
        AttributeTypeRestriction attributeTypeRestriction = (AttributeTypeRestriction)theEObject;
        T result = caseAttributeTypeRestriction(attributeTypeRestriction);
        if (result == null) result = caseObjectRestriction(attributeTypeRestriction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION:
      {
        LegacyRelationTypeRestriction legacyRelationTypeRestriction = (LegacyRelationTypeRestriction)theEObject;
        T result = caseLegacyRelationTypeRestriction(legacyRelationTypeRestriction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.RELATION_TYPE_RESTRICTION:
      {
        RelationTypeRestriction relationTypeRestriction = (RelationTypeRestriction)theEObject;
        T result = caseRelationTypeRestriction(relationTypeRestriction);
        if (result == null) result = caseObjectRestriction(relationTypeRestriction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Osee Dsl</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Osee Dsl</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOseeDsl(OseeDsl object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Import</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Import</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImport(Import object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Osee Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Osee Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOseeElement(OseeElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Osee Type</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Osee Type</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOseeType(OseeType object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XArtifact Type</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XArtifact Type</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXArtifactType(XArtifactType object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XAttribute Type Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XAttribute Type Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXAttributeTypeRef(XAttributeTypeRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XAttribute Type</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XAttribute Type</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXAttributeType(XAttributeType object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XOsee Enum Type</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XOsee Enum Type</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXOseeEnumType(XOseeEnumType object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XOsee Enum Entry</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XOsee Enum Entry</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXOseeEnumEntry(XOseeEnumEntry object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XOsee Enum Override</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XOsee Enum Override</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXOseeEnumOverride(XOseeEnumOverride object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Override Option</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Override Option</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOverrideOption(OverrideOption object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Add Enum</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Add Enum</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAddEnum(AddEnum object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Remove Enum</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Remove Enum</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRemoveEnum(RemoveEnum object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XOsee Artifact Type Override</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XOsee Artifact Type Override</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXOseeArtifactTypeOverride(XOseeArtifactTypeOverride object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute Override Option</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Override Option</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeOverrideOption(AttributeOverrideOption object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Add Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Add Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAddAttribute(AddAttribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Remove Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Remove Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRemoveAttribute(RemoveAttribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Update Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Update Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUpdateAttribute(UpdateAttribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XRelation Type</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XRelation Type</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXRelationType(XRelationType object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Condition</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Condition</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCondition(Condition object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Simple Condition</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Simple Condition</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSimpleCondition(SimpleCondition object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Compound Condition</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Compound Condition</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCompoundCondition(CompoundCondition object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XArtifact Matcher</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XArtifact Matcher</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXArtifactMatcher(XArtifactMatcher object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Role</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Role</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRole(Role object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Referenced Context</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Referenced Context</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseReferencedContext(ReferencedContext object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Users And Groups</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Users And Groups</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUsersAndGroups(UsersAndGroups object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Access Context</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Access Context</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAccessContext(AccessContext object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Hierarchy Restriction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Hierarchy Restriction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseHierarchyRestriction(HierarchyRestriction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Relation Type Artifact Type Predicate</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Relation Type Artifact Type Predicate</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRelationTypeArtifactTypePredicate(RelationTypeArtifactTypePredicate object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Relation Type Artifact Predicate</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Relation Type Artifact Predicate</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRelationTypeArtifactPredicate(RelationTypeArtifactPredicate object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Relation Type Predicate</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Relation Type Predicate</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRelationTypePredicate(RelationTypePredicate object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Object Restriction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Object Restriction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseObjectRestriction(ObjectRestriction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Artifact Match Restriction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Artifact Match Restriction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseArtifactMatchRestriction(ArtifactMatchRestriction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Artifact Type Restriction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Artifact Type Restriction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseArtifactTypeRestriction(ArtifactTypeRestriction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute Type Restriction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Type Restriction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeTypeRestriction(AttributeTypeRestriction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Legacy Relation Type Restriction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Legacy Relation Type Restriction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLegacyRelationTypeRestriction(LegacyRelationTypeRestriction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Relation Type Restriction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Relation Type Restriction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRelationTypeRestriction(RelationTypeRestriction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} //OseeDslSwitch
