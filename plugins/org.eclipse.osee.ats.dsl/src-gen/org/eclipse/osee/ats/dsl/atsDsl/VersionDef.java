/**
 */
package org.eclipse.osee.ats.dsl.atsDsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Version Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getUuid <em>Uuid</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getStaticId <em>Static Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getNext <em>Next</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getReleased <em>Released</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getAllowCreateBranch <em>Allow Create Branch</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getAllowCommitBranch <em>Allow Commit Branch</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getBaselineBranchUuid <em>Baseline Branch Uuid</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getParallelVersion <em>Parallel Version</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef()
 * @model
 * @generated
 */
public interface VersionDef extends EObject
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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

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
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef_Uuid()
   * @model
   * @generated
   */
  int getUuid();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getUuid <em>Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Uuid</em>' attribute.
   * @see #getUuid()
   * @generated
   */
  void setUuid(int value);

  /**
   * Returns the value of the '<em><b>Static Id</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Static Id</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Static Id</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef_StaticId()
   * @model unique="false"
   * @generated
   */
  EList<String> getStaticId();

  /**
   * Returns the value of the '<em><b>Next</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Next</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Next</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #setNext(BooleanDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef_Next()
   * @model
   * @generated
   */
  BooleanDef getNext();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getNext <em>Next</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Next</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #getNext()
   * @generated
   */
  void setNext(BooleanDef value);

  /**
   * Returns the value of the '<em><b>Released</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Released</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Released</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #setReleased(BooleanDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef_Released()
   * @model
   * @generated
   */
  BooleanDef getReleased();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getReleased <em>Released</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Released</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #getReleased()
   * @generated
   */
  void setReleased(BooleanDef value);

  /**
   * Returns the value of the '<em><b>Allow Create Branch</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Allow Create Branch</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Allow Create Branch</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #setAllowCreateBranch(BooleanDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef_AllowCreateBranch()
   * @model
   * @generated
   */
  BooleanDef getAllowCreateBranch();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getAllowCreateBranch <em>Allow Create Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Allow Create Branch</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #getAllowCreateBranch()
   * @generated
   */
  void setAllowCreateBranch(BooleanDef value);

  /**
   * Returns the value of the '<em><b>Allow Commit Branch</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.ats.dsl.atsDsl.BooleanDef}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Allow Commit Branch</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Allow Commit Branch</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #setAllowCommitBranch(BooleanDef)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef_AllowCommitBranch()
   * @model
   * @generated
   */
  BooleanDef getAllowCommitBranch();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getAllowCommitBranch <em>Allow Commit Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Allow Commit Branch</em>' attribute.
   * @see org.eclipse.osee.ats.dsl.atsDsl.BooleanDef
   * @see #getAllowCommitBranch()
   * @generated
   */
  void setAllowCommitBranch(BooleanDef value);

  /**
   * Returns the value of the '<em><b>Baseline Branch Uuid</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Baseline Branch Uuid</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Baseline Branch Uuid</em>' attribute.
   * @see #setBaselineBranchUuid(String)
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef_BaselineBranchUuid()
   * @model
   * @generated
   */
  String getBaselineBranchUuid();

  /**
   * Sets the value of the '{@link org.eclipse.osee.ats.dsl.atsDsl.VersionDef#getBaselineBranchUuid <em>Baseline Branch Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Baseline Branch Uuid</em>' attribute.
   * @see #getBaselineBranchUuid()
   * @generated
   */
  void setBaselineBranchUuid(String value);

  /**
   * Returns the value of the '<em><b>Parallel Version</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parallel Version</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parallel Version</em>' attribute list.
   * @see org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage#getVersionDef_ParallelVersion()
   * @model unique="false"
   * @generated
   */
  EList<String> getParallelVersion();

} // VersionDef
