/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Os Follow Relation Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getSide <em>Side</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getCriteria <em>Criteria</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getCollect <em>Collect</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFollowRelationType()
 * @model
 * @generated
 */
public interface OsFollowRelationType extends OsFollowStatement
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
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFollowRelationType_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' containment reference.
   * @see #setType(OsExpression)
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFollowRelationType_Type()
   * @model containment="true"
   * @generated
   */
  OsExpression getType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getType <em>Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' containment reference.
   * @see #getType()
   * @generated
   */
  void setType(OsExpression value);

  /**
   * Returns the value of the '<em><b>Side</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Side</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Side</em>' attribute.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide
   * @see #setSide(OsRelationSide)
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFollowRelationType_Side()
   * @model
   * @generated
   */
  OsRelationSide getSide();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getSide <em>Side</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Side</em>' attribute.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide
   * @see #getSide()
   * @generated
   */
  void setSide(OsRelationSide value);

  /**
   * Returns the value of the '<em><b>Criteria</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsItemCriteria}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Criteria</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Criteria</em>' containment reference list.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFollowRelationType_Criteria()
   * @model containment="true"
   * @generated
   */
  EList<OsItemCriteria> getCriteria();

  /**
   * Returns the value of the '<em><b>Collect</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Collect</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Collect</em>' containment reference.
   * @see #setCollect(OsCollectClause)
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsFollowRelationType_Collect()
   * @model containment="true"
   * @generated
   */
  OsCollectClause getCollect();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType#getCollect <em>Collect</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Collect</em>' containment reference.
   * @see #getCollect()
   * @generated
   */
  void setCollect(OsCollectClause value);

} // OsFollowRelationType
