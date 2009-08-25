/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relation Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideAName <em>Side AName</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideAArtifactType <em>Side AArtifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideBName <em>Side BName</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideBArtifactType <em>Side BArtifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.RelationType#getDefaultOrderType <em>Default Order Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.RelationType#getMultiplicity <em>Multiplicity</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRelationType()
 * @model
 * @generated
 */
public interface RelationType extends OseeType
{
  /**
   * Returns the value of the '<em><b>Side AName</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Side AName</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Side AName</em>' attribute.
   * @see #setSideAName(String)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRelationType_SideAName()
   * @model
   * @generated
   */
  String getSideAName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideAName <em>Side AName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Side AName</em>' attribute.
   * @see #getSideAName()
   * @generated
   */
  void setSideAName(String value);

  /**
   * Returns the value of the '<em><b>Side AArtifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Side AArtifact Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Side AArtifact Type</em>' reference.
   * @see #setSideAArtifactType(ArtifactType)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRelationType_SideAArtifactType()
   * @model
   * @generated
   */
  ArtifactType getSideAArtifactType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideAArtifactType <em>Side AArtifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Side AArtifact Type</em>' reference.
   * @see #getSideAArtifactType()
   * @generated
   */
  void setSideAArtifactType(ArtifactType value);

  /**
   * Returns the value of the '<em><b>Side BName</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Side BName</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Side BName</em>' attribute.
   * @see #setSideBName(String)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRelationType_SideBName()
   * @model
   * @generated
   */
  String getSideBName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideBName <em>Side BName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Side BName</em>' attribute.
   * @see #getSideBName()
   * @generated
   */
  void setSideBName(String value);

  /**
   * Returns the value of the '<em><b>Side BArtifact Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Side BArtifact Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Side BArtifact Type</em>' reference.
   * @see #setSideBArtifactType(ArtifactType)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRelationType_SideBArtifactType()
   * @model
   * @generated
   */
  ArtifactType getSideBArtifactType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getSideBArtifactType <em>Side BArtifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Side BArtifact Type</em>' reference.
   * @see #getSideBArtifactType()
   * @generated
   */
  void setSideBArtifactType(ArtifactType value);

  /**
   * Returns the value of the '<em><b>Default Order Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default Order Type</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Order Type</em>' attribute.
   * @see #setDefaultOrderType(String)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRelationType_DefaultOrderType()
   * @model
   * @generated
   */
  String getDefaultOrderType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getDefaultOrderType <em>Default Order Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Order Type</em>' attribute.
   * @see #getDefaultOrderType()
   * @generated
   */
  void setDefaultOrderType(String value);

  /**
   * Returns the value of the '<em><b>Multiplicity</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Multiplicity</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Multiplicity</em>' attribute.
   * @see org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum
   * @see #setMultiplicity(RelationMultiplicityEnum)
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage#getRelationType_Multiplicity()
   * @model
   * @generated
   */
  RelationMultiplicityEnum getMultiplicity();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.oseeTypes.RelationType#getMultiplicity <em>Multiplicity</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Multiplicity</em>' attribute.
   * @see org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum
   * @see #getMultiplicity()
   * @generated
   */
  void setMultiplicity(RelationMultiplicityEnum value);

} // RelationType
