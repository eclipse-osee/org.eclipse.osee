/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQuery;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Os Tx Query Statement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxQueryStatementImpl#getData <em>Data</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsTxQueryStatementImpl extends OsQueryImpl implements OsTxQueryStatement
{
  /**
   * The cached value of the '{@link #getData() <em>Data</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getData()
   * @generated
   * @ordered
   */
  protected OsTxQuery data;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OsTxQueryStatementImpl()
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
    return OrcsScriptDslPackage.Literals.OS_TX_QUERY_STATEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxQuery getData()
  {
    return data;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetData(OsTxQuery newData, NotificationChain msgs)
  {
    OsTxQuery oldData = data;
    data = newData;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT__DATA, oldData, newData);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setData(OsTxQuery newData)
  {
    if (newData != data)
    {
      NotificationChain msgs = null;
      if (data != null)
        msgs = ((InternalEObject)data).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT__DATA, null, msgs);
      if (newData != null)
        msgs = ((InternalEObject)newData).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT__DATA, null, msgs);
      msgs = basicSetData(newData, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT__DATA, newData, newData));
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
      case OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT__DATA:
        return basicSetData(null, msgs);
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
      case OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT__DATA:
        return getData();
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
      case OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT__DATA:
        setData((OsTxQuery)newValue);
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
      case OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT__DATA:
        setData((OsTxQuery)null);
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
      case OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT__DATA:
        return data != null;
    }
    return super.eIsSet(featureID);
  }

} //OsTxQueryStatementImpl
