/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage
 * @generated
 */
public interface OseeTypesFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  OseeTypesFactory eINSTANCE = org.eclipse.osee.framework.oseeTypes.impl.OseeTypesFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Osee Type Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Osee Type Model</em>'.
   * @generated
   */
  OseeTypeModel createOseeTypeModel();

  /**
   * Returns a new object of class '<em>Import</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Import</em>'.
   * @generated
   */
  Import createImport();

  /**
   * Returns a new object of class '<em>Osee Element</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Osee Element</em>'.
   * @generated
   */
  OseeElement createOseeElement();

  /**
   * Returns a new object of class '<em>Osee Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Osee Type</em>'.
   * @generated
   */
  OseeType createOseeType();

  /**
   * Returns a new object of class '<em>Artifact Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Artifact Type</em>'.
   * @generated
   */
  ArtifactType createArtifactType();

  /**
   * Returns a new object of class '<em>Attribute Type Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Type Ref</em>'.
   * @generated
   */
  AttributeTypeRef createAttributeTypeRef();

  /**
   * Returns a new object of class '<em>Attribute Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Type</em>'.
   * @generated
   */
  AttributeType createAttributeType();

  /**
   * Returns a new object of class '<em>Osee Enum Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Osee Enum Type</em>'.
   * @generated
   */
  OseeEnumType createOseeEnumType();

  /**
   * Returns a new object of class '<em>Osee Enum Entry</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Osee Enum Entry</em>'.
   * @generated
   */
  OseeEnumEntry createOseeEnumEntry();

  /**
   * Returns a new object of class '<em>Osee Enum Override</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Osee Enum Override</em>'.
   * @generated
   */
  OseeEnumOverride createOseeEnumOverride();

  /**
   * Returns a new object of class '<em>Override Option</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Override Option</em>'.
   * @generated
   */
  OverrideOption createOverrideOption();

  /**
   * Returns a new object of class '<em>Add Enum</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Add Enum</em>'.
   * @generated
   */
  AddEnum createAddEnum();

  /**
   * Returns a new object of class '<em>Remove Enum</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Remove Enum</em>'.
   * @generated
   */
  RemoveEnum createRemoveEnum();

  /**
   * Returns a new object of class '<em>Relation Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Relation Type</em>'.
   * @generated
   */
  RelationType createRelationType();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  OseeTypesPackage getOseeTypesPackage();

} //OseeTypesFactory
