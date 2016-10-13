/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XRelation Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideAName <em>Side AName</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideAArtifactType <em>Side AArtifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideBName <em>Side BName</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideBArtifactType <em>Side BArtifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getDefaultOrderType <em>Default Order Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getMultiplicity <em>Multiplicity</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXRelationType()
 * @model
 * @generated
 */
public interface XRelationType extends OseeType
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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXRelationType_SideAName()
   * @model
   * @generated
   */
  String getSideAName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideAName <em>Side AName</em>}' attribute.
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
   * @see #setSideAArtifactType(XArtifactType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXRelationType_SideAArtifactType()
   * @model
   * @generated
   */
  XArtifactType getSideAArtifactType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideAArtifactType <em>Side AArtifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Side AArtifact Type</em>' reference.
   * @see #getSideAArtifactType()
   * @generated
   */
  void setSideAArtifactType(XArtifactType value);

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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXRelationType_SideBName()
   * @model
   * @generated
   */
  String getSideBName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideBName <em>Side BName</em>}' attribute.
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
   * @see #setSideBArtifactType(XArtifactType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXRelationType_SideBArtifactType()
   * @model
   * @generated
   */
  XArtifactType getSideBArtifactType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getSideBArtifactType <em>Side BArtifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Side BArtifact Type</em>' reference.
   * @see #getSideBArtifactType()
   * @generated
   */
  void setSideBArtifactType(XArtifactType value);

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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXRelationType_DefaultOrderType()
   * @model
   * @generated
   */
  String getDefaultOrderType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getDefaultOrderType <em>Default Order Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Order Type</em>' attribute.
   * @see #getDefaultOrderType()
   * @generated
   */
  void setDefaultOrderType(String value);

  /**
   * Returns the value of the '<em><b>Multiplicity</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Multiplicity</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Multiplicity</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum
   * @see #setMultiplicity(RelationMultiplicityEnum)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getXRelationType_Multiplicity()
   * @model
   * @generated
   */
  RelationMultiplicityEnum getMultiplicity();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType#getMultiplicity <em>Multiplicity</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Multiplicity</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum
   * @see #getMultiplicity()
   * @generated
   */
  void setMultiplicity(RelationMultiplicityEnum value);

} // XRelationType
