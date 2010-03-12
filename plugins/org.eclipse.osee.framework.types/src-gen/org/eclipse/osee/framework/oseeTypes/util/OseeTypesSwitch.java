/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.osee.framework.oseeTypes.*;

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
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage
 * @generated
 */
public class OseeTypesSwitch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static OseeTypesPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeTypesSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = OseeTypesPackage.eINSTANCE;
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
      case OseeTypesPackage.OSEE_TYPE_MODEL:
      {
        OseeTypeModel oseeTypeModel = (OseeTypeModel)theEObject;
        T result = caseOseeTypeModel(oseeTypeModel);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.IMPORT:
      {
        Import import_ = (Import)theEObject;
        T result = caseImport(import_);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.OSEE_ELEMENT:
      {
        OseeElement oseeElement = (OseeElement)theEObject;
        T result = caseOseeElement(oseeElement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.OSEE_TYPE:
      {
        OseeType oseeType = (OseeType)theEObject;
        T result = caseOseeType(oseeType);
        if (result == null) result = caseOseeElement(oseeType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.XARTIFACT_TYPE:
      {
        XArtifactType xArtifactType = (XArtifactType)theEObject;
        T result = caseXArtifactType(xArtifactType);
        if (result == null) result = caseOseeType(xArtifactType);
        if (result == null) result = caseOseeElement(xArtifactType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.XATTRIBUTE_TYPE_REF:
      {
        XAttributeTypeRef xAttributeTypeRef = (XAttributeTypeRef)theEObject;
        T result = caseXAttributeTypeRef(xAttributeTypeRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.XATTRIBUTE_TYPE:
      {
        XAttributeType xAttributeType = (XAttributeType)theEObject;
        T result = caseXAttributeType(xAttributeType);
        if (result == null) result = caseOseeType(xAttributeType);
        if (result == null) result = caseOseeElement(xAttributeType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.XOSEE_ENUM_TYPE:
      {
        XOseeEnumType xOseeEnumType = (XOseeEnumType)theEObject;
        T result = caseXOseeEnumType(xOseeEnumType);
        if (result == null) result = caseOseeType(xOseeEnumType);
        if (result == null) result = caseOseeElement(xOseeEnumType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.XOSEE_ENUM_ENTRY:
      {
        XOseeEnumEntry xOseeEnumEntry = (XOseeEnumEntry)theEObject;
        T result = caseXOseeEnumEntry(xOseeEnumEntry);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.XOSEE_ENUM_OVERRIDE:
      {
        XOseeEnumOverride xOseeEnumOverride = (XOseeEnumOverride)theEObject;
        T result = caseXOseeEnumOverride(xOseeEnumOverride);
        if (result == null) result = caseOseeElement(xOseeEnumOverride);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.OVERRIDE_OPTION:
      {
        OverrideOption overrideOption = (OverrideOption)theEObject;
        T result = caseOverrideOption(overrideOption);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.ADD_ENUM:
      {
        AddEnum addEnum = (AddEnum)theEObject;
        T result = caseAddEnum(addEnum);
        if (result == null) result = caseOverrideOption(addEnum);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.REMOVE_ENUM:
      {
        RemoveEnum removeEnum = (RemoveEnum)theEObject;
        T result = caseRemoveEnum(removeEnum);
        if (result == null) result = caseOverrideOption(removeEnum);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OseeTypesPackage.XRELATION_TYPE:
      {
        XRelationType xRelationType = (XRelationType)theEObject;
        T result = caseXRelationType(xRelationType);
        if (result == null) result = caseOseeType(xRelationType);
        if (result == null) result = caseOseeElement(xRelationType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Osee Type Model</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Osee Type Model</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOseeTypeModel(OseeTypeModel object)
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

} //OseeTypesSwitch
