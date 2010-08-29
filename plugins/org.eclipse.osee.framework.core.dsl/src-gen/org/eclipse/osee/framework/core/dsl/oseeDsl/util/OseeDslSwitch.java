/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

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
public class OseeDslSwitch<T>
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
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return
        eSuperTypes.isEmpty() ?
          defaultCase(theEObject) :
          doSwitch(eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
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
      case OseeDslPackage.XRELATION_TYPE:
      {
        XRelationType xRelationType = (XRelationType)theEObject;
        T result = caseXRelationType(xRelationType);
        if (result == null) result = caseOseeType(xRelationType);
        if (result == null) result = caseOseeElement(xRelationType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XARTIFACT_REF:
      {
        XArtifactRef xArtifactRef = (XArtifactRef)theEObject;
        T result = caseXArtifactRef(xArtifactRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.XBRANCH_REF:
      {
        XBranchRef xBranchRef = (XBranchRef)theEObject;
        T result = caseXBranchRef(xBranchRef);
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
      case OseeDslPackage.OBJECT_RESTRICTION:
      {
        ObjectRestriction objectRestriction = (ObjectRestriction)theEObject;
        T result = caseObjectRestriction(objectRestriction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeDslPackage.ARTIFACT_INSTANCE_RESTRICTION:
      {
        ArtifactInstanceRestriction artifactInstanceRestriction = (ArtifactInstanceRestriction)theEObject;
        T result = caseArtifactInstanceRestriction(artifactInstanceRestriction);
        if (result == null) result = caseObjectRestriction(artifactInstanceRestriction);
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
   * Returns the result of interpreting the object as an instance of '<em>XArtifact Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XArtifact Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXArtifactRef(XArtifactRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>XBranch Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>XBranch Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXBranchRef(XBranchRef object)
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
   * Returns the result of interpreting the object as an instance of '<em>Artifact Instance Restriction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Artifact Instance Restriction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseArtifactInstanceRestriction(ArtifactInstanceRestriction object)
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
  public T defaultCase(EObject object)
  {
    return null;
  }

} //OseeDslSwitch
