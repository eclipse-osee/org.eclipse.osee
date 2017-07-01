/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Program Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getProgramDefOption <em>Program Def Option</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getUuid <em>Uuid</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getArtifactTypeName <em>Artifact Type Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getActive <em>Active</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getTeamDefinition <em>Team Definition</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getProgramDef()
 * @model
 * @generated
 */
public interface ProgramDef extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getProgramDef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Program Def Option</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Program Def Option</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Program Def Option</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getProgramDef_ProgramDefOption()
   * @model unique="false"
   * @generated
   */
  EList<String> getProgramDefOption();

  /**
   * Returns the value of the '<em><b>Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Uuid</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Uuid</em>' attribute.
   * @see #setUuid(int)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getProgramDef_Uuid()
   * @model
   * @generated
   */
  int getUuid();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getUuid <em>Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Uuid</em>' attribute.
   * @see #getUuid()
   * @generated
   */
  void setUuid(int value);

  /**
   * Returns the value of the '<em><b>Artifact Type Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Type Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Type Name</em>' attribute.
   * @see #setArtifactTypeName(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getProgramDef_ArtifactTypeName()
   * @model
   * @generated
   */
  String getArtifactTypeName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getArtifactTypeName <em>Artifact Type Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Artifact Type Name</em>' attribute.
   * @see #getArtifactTypeName()
   * @generated
   */
  void setArtifactTypeName(String value);

  /**
   * Returns the value of the '<em><b>Active</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Active</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Active</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #setActive(BooleanDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getProgramDef_Active()
   * @model
   * @generated
   */
  BooleanDef getActive();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getActive <em>Active</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Active</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #getActive()
   * @generated
   */
  void setActive(BooleanDef value);

  /**
   * Returns the value of the '<em><b>Namespace</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Namespace</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Namespace</em>' attribute.
   * @see #setNamespace(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getProgramDef_Namespace()
   * @model
   * @generated
   */
  String getNamespace();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getNamespace <em>Namespace</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Namespace</em>' attribute.
   * @see #getNamespace()
   * @generated
   */
  void setNamespace(String value);

  /**
   * Returns the value of the '<em><b>Team Definition</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Team Definition</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Team Definition</em>' attribute.
   * @see #setTeamDefinition(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getProgramDef_TeamDefinition()
   * @model
   * @generated
   */
  String getTeamDefinition();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.ProgramDef#getTeamDefinition <em>Team Definition</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Team Definition</em>' attribute.
   * @see #getTeamDefinition()
   * @generated
   */
  void setTeamDefinition(String value);

  /**
   * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.ats.dsl.atsDsl.AttrDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' containment reference list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getProgramDef_Attributes()
   * @model containment="true"
   * @generated
   */
  EList<AttrDef> getAttributes();

} // ProgramDef
