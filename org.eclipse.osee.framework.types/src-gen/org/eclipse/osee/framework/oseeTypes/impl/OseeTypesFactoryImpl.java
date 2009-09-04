/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.osee.framework.oseeTypes.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class OseeTypesFactoryImpl extends EFactoryImpl implements OseeTypesFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OseeTypesFactory init()
  {
    try
    {
      OseeTypesFactory theOseeTypesFactory = (OseeTypesFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/osee/framework/OseeTypes"); 
      if (theOseeTypesFactory != null)
      {
        return theOseeTypesFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new OseeTypesFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeTypesFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case OseeTypesPackage.OSEE_TYPE_MODEL: return createOseeTypeModel();
      case OseeTypesPackage.IMPORT: return createImport();
      case OseeTypesPackage.OSEE_ELEMENT: return createOseeElement();
      case OseeTypesPackage.OSEE_TYPE: return createOseeType();
      case OseeTypesPackage.ARTIFACT_TYPE: return createArtifactType();
      case OseeTypesPackage.ATTRIBUTE_TYPE_REF: return createAttributeTypeRef();
      case OseeTypesPackage.ATTRIBUTE_TYPE: return createAttributeType();
      case OseeTypesPackage.OSEE_ENUM_TYPE: return createOseeEnumType();
      case OseeTypesPackage.OSEE_ENUM_ENTRY: return createOseeEnumEntry();
      case OseeTypesPackage.OSEE_ENUM_OVERRIDE: return createOseeEnumOverride();
      case OseeTypesPackage.OVERRIDE_OPTION: return createOverrideOption();
      case OseeTypesPackage.ADD_ENUM: return createAddEnum();
      case OseeTypesPackage.REMOVE_ENUM: return createRemoveEnum();
      case OseeTypesPackage.RELATION_TYPE: return createRelationType();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case OseeTypesPackage.RELATION_MULTIPLICITY_ENUM:
        return createRelationMultiplicityEnumFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case OseeTypesPackage.RELATION_MULTIPLICITY_ENUM:
        return convertRelationMultiplicityEnumToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeTypeModel createOseeTypeModel()
  {
    OseeTypeModelImpl oseeTypeModel = new OseeTypeModelImpl();
    return oseeTypeModel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Import createImport()
  {
    ImportImpl import_ = new ImportImpl();
    return import_;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeElement createOseeElement()
  {
    OseeElementImpl oseeElement = new OseeElementImpl();
    return oseeElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeType createOseeType()
  {
    OseeTypeImpl oseeType = new OseeTypeImpl();
    return oseeType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArtifactType createArtifactType()
  {
    ArtifactTypeImpl artifactType = new ArtifactTypeImpl();
    return artifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeTypeRef createAttributeTypeRef()
  {
    AttributeTypeRefImpl attributeTypeRef = new AttributeTypeRefImpl();
    return attributeTypeRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeType createAttributeType()
  {
    AttributeTypeImpl attributeType = new AttributeTypeImpl();
    return attributeType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeEnumType createOseeEnumType()
  {
    OseeEnumTypeImpl oseeEnumType = new OseeEnumTypeImpl();
    return oseeEnumType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeEnumEntry createOseeEnumEntry()
  {
    OseeEnumEntryImpl oseeEnumEntry = new OseeEnumEntryImpl();
    return oseeEnumEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeEnumOverride createOseeEnumOverride()
  {
    OseeEnumOverrideImpl oseeEnumOverride = new OseeEnumOverrideImpl();
    return oseeEnumOverride;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OverrideOption createOverrideOption()
  {
    OverrideOptionImpl overrideOption = new OverrideOptionImpl();
    return overrideOption;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AddEnum createAddEnum()
  {
    AddEnumImpl addEnum = new AddEnumImpl();
    return addEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RemoveEnum createRemoveEnum()
  {
    RemoveEnumImpl removeEnum = new RemoveEnumImpl();
    return removeEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationType createRelationType()
  {
    RelationTypeImpl relationType = new RelationTypeImpl();
    return relationType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationMultiplicityEnum createRelationMultiplicityEnumFromString(EDataType eDataType, String initialValue)
  {
    RelationMultiplicityEnum result = RelationMultiplicityEnum.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertRelationMultiplicityEnumToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeTypesPackage getOseeTypesPackage()
  {
    return (OseeTypesPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static OseeTypesPackage getPackage()
  {
    return OseeTypesPackage.eINSTANCE;
  }

} //OseeTypesFactoryImpl
