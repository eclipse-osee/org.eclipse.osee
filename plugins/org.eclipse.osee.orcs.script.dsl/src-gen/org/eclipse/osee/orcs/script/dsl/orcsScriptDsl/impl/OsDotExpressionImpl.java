/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Os Dot Expression</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsDotExpressionImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsDotExpressionImpl#getTail <em>Tail</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsDotExpressionImpl extends OsExpressionImpl implements OsDotExpression
{
  /**
   * The cached value of the '{@link #getRef() <em>Ref</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRef()
   * @generated
   * @ordered
   */
  protected OsExpression ref;

  /**
   * The cached value of the '{@link #getTail() <em>Tail</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTail()
   * @generated
   * @ordered
   */
  protected OsExpression tail;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OsDotExpressionImpl()
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
    return OrcsScriptDslPackage.Literals.OS_DOT_EXPRESSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsExpression getRef()
  {
    return ref;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetRef(OsExpression newRef, NotificationChain msgs)
  {
    OsExpression oldRef = ref;
    ref = newRef;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_DOT_EXPRESSION__REF, oldRef, newRef);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRef(OsExpression newRef)
  {
    if (newRef != ref)
    {
      NotificationChain msgs = null;
      if (ref != null)
        msgs = ((InternalEObject)ref).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_DOT_EXPRESSION__REF, null, msgs);
      if (newRef != null)
        msgs = ((InternalEObject)newRef).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_DOT_EXPRESSION__REF, null, msgs);
      msgs = basicSetRef(newRef, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_DOT_EXPRESSION__REF, newRef, newRef));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsExpression getTail()
  {
    if (tail != null && tail.eIsProxy())
    {
      InternalEObject oldTail = (InternalEObject)tail;
      tail = (OsExpression)eResolveProxy(oldTail);
      if (tail != oldTail)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OrcsScriptDslPackage.OS_DOT_EXPRESSION__TAIL, oldTail, tail));
      }
    }
    return tail;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsExpression basicGetTail()
  {
    return tail;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTail(OsExpression newTail)
  {
    OsExpression oldTail = tail;
    tail = newTail;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_DOT_EXPRESSION__TAIL, oldTail, tail));
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
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION__REF:
        return basicSetRef(null, msgs);
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
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION__REF:
        return getRef();
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION__TAIL:
        if (resolve) return getTail();
        return basicGetTail();
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
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION__REF:
        setRef((OsExpression)newValue);
        return;
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION__TAIL:
        setTail((OsExpression)newValue);
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
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION__REF:
        setRef((OsExpression)null);
        return;
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION__TAIL:
        setTail((OsExpression)null);
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
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION__REF:
        return ref != null;
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION__TAIL:
        return tail != null;
    }
    return super.eIsSet(featureID);
  }

} //OsDotExpressionImpl
