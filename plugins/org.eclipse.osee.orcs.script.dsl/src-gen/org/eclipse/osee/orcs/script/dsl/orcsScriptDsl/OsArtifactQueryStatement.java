/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Os Artifact Query Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getData <em>Data</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getCollect <em>Collect</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsArtifactQueryStatement()
 * @model
 * @generated
 */
public interface OsArtifactQueryStatement extends OsObjectQuery
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
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsArtifactQueryStatement_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Data</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Data</em>' containment reference.
   * @see #setData(OsArtifactQuery)
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsArtifactQueryStatement_Data()
   * @model containment="true"
   * @generated
   */
  OsArtifactQuery getData();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getData <em>Data</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Data</em>' containment reference.
   * @see #getData()
   * @generated
   */
  void setData(OsArtifactQuery value);

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
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsArtifactQueryStatement_Collect()
   * @model containment="true"
   * @generated
   */
  OsCollectClause getCollect();

  /**
   * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement#getCollect <em>Collect</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Collect</em>' containment reference.
   * @see #getCollect()
   * @generated
   */
  void setCollect(OsCollectClause value);

} // OsArtifactQueryStatement
