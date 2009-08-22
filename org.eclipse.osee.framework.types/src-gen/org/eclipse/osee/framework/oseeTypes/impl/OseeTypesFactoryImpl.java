/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import org.eclipse.emf.ecore.EClass;
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
      case OseeTypesPackage.MODEL: return createModel();
      case OseeTypesPackage.IMPORT: return createImport();
      case OseeTypesPackage.TYPE: return createType();
      case OseeTypesPackage.ARTIFACT_TYPE: return createArtifactType();
      case OseeTypesPackage.XREF: return createXRef();
      case OseeTypesPackage.RELATION_TYPE_REF: return createRelationTypeRef();
      case OseeTypesPackage.ATTRIBUTE_TYPE_REF: return createAttributeTypeRef();
      case OseeTypesPackage.ATTRIBUTE_TYPE: return createAttributeType();
      case OseeTypesPackage.XATTRIBUTE: return createXAttribute();
      case OseeTypesPackage.RELATION_TYPE: return createRelationType();
      case OseeTypesPackage.XRELATION: return createXRelation();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model createModel()
  {
    ModelImpl model = new ModelImpl();
    return model;
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
  public Type createType()
  {
    TypeImpl type = new TypeImpl();
    return type;
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
  public XRef createXRef()
  {
    XRefImpl xRef = new XRefImpl();
    return xRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationTypeRef createRelationTypeRef()
  {
    RelationTypeRefImpl relationTypeRef = new RelationTypeRefImpl();
    return relationTypeRef;
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
  public XAttribute createXAttribute()
  {
    XAttributeImpl xAttribute = new XAttributeImpl();
    return xAttribute;
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
  public XRelation createXRelation()
  {
    XRelationImpl xRelation = new XRelationImpl();
    return xRelation;
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
