/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQuery;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Os Artifact Query Statement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryStatementImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryStatementImpl#getData <em>Data</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsArtifactQueryStatementImpl#getCollect <em>Collect</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsArtifactQueryStatementImpl extends OsObjectQueryImpl implements OsArtifactQueryStatement
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getData() <em>Data</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getData()
   * @generated
   * @ordered
   */
  protected OsArtifactQuery data;

  /**
   * The cached value of the '{@link #getCollect() <em>Collect</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCollect()
   * @generated
   * @ordered
   */
  protected OsCollectClause collect;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OsArtifactQueryStatementImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return OrcsScriptDslPackage.Literals.OS_ARTIFACT_QUERY_STATEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactQuery getData()
  {
    return data;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetData(OsArtifactQuery newData, NotificationChain msgs)
  {
    OsArtifactQuery oldData = data;
    data = newData;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__DATA, oldData, newData);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setData(OsArtifactQuery newData)
  {
    if (newData != data)
    {
      NotificationChain msgs = null;
      if (data != null)
        msgs = ((InternalEObject)data).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__DATA, null, msgs);
      if (newData != null)
        msgs = ((InternalEObject)newData).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__DATA, null, msgs);
      msgs = basicSetData(newData, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__DATA, newData, newData));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsCollectClause getCollect()
  {
    return collect;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCollect(OsCollectClause newCollect, NotificationChain msgs)
  {
    OsCollectClause oldCollect = collect;
    collect = newCollect;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__COLLECT, oldCollect, newCollect);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCollect(OsCollectClause newCollect)
  {
    if (newCollect != collect)
    {
      NotificationChain msgs = null;
      if (collect != null)
        msgs = ((InternalEObject)collect).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__COLLECT, null, msgs);
      if (newCollect != null)
        msgs = ((InternalEObject)newCollect).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__COLLECT, null, msgs);
      msgs = basicSetCollect(newCollect, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__COLLECT, newCollect, newCollect));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__DATA:
        return basicSetData(null, msgs);
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__COLLECT:
        return basicSetCollect(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__NAME:
        return getName();
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__DATA:
        return getData();
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__COLLECT:
        return getCollect();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__NAME:
        setName((String)newValue);
        return;
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__DATA:
        setData((OsArtifactQuery)newValue);
        return;
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__COLLECT:
        setCollect((OsCollectClause)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__NAME:
        setName(NAME_EDEFAULT);
        return;
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__DATA:
        setData((OsArtifactQuery)null);
        return;
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__COLLECT:
        setCollect((OsCollectClause)null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__DATA:
        return data != null;
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT__COLLECT:
        return collect != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //OsArtifactQueryStatementImpl
