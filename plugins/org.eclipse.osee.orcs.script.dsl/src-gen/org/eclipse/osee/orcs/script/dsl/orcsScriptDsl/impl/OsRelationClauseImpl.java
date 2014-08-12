/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Os Relation Clause</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationClauseImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationClauseImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsRelationClauseImpl#getSide <em>Side</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsRelationClauseImpl extends MinimalEObjectImpl.Container implements OsRelationClause
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
   * The cached value of the '{@link #getType() <em>Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected OsExpression type;

  /**
   * The default value of the '{@link #getSide() <em>Side</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSide()
   * @generated
   * @ordered
   */
  protected static final OsRelationSide SIDE_EDEFAULT = OsRelationSide.SIDE_A;

  /**
   * The cached value of the '{@link #getSide() <em>Side</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSide()
   * @generated
   * @ordered
   */
  protected OsRelationSide side = SIDE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OsRelationClauseImpl()
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
    return OrcsScriptDslPackage.Literals.OS_RELATION_CLAUSE;
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
      eNotify(new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_RELATION_CLAUSE__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsExpression getType()
  {
    return type;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetType(OsExpression newType, NotificationChain msgs)
  {
    OsExpression oldType = type;
    type = newType;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_RELATION_CLAUSE__TYPE, oldType, newType);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setType(OsExpression newType)
  {
    if (newType != type)
    {
      NotificationChain msgs = null;
      if (type != null)
        msgs = ((InternalEObject)type).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_RELATION_CLAUSE__TYPE, null, msgs);
      if (newType != null)
        msgs = ((InternalEObject)newType).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_RELATION_CLAUSE__TYPE, null, msgs);
      msgs = basicSetType(newType, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_RELATION_CLAUSE__TYPE, newType, newType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsRelationSide getSide()
  {
    return side;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSide(OsRelationSide newSide)
  {
    OsRelationSide oldSide = side;
    side = newSide == null ? SIDE_EDEFAULT : newSide;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OrcsScriptDslPackage.OS_RELATION_CLAUSE__SIDE, oldSide, side));
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
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__TYPE:
        return basicSetType(null, msgs);
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
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__NAME:
        return getName();
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__TYPE:
        return getType();
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__SIDE:
        return getSide();
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
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__NAME:
        setName((String)newValue);
        return;
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__TYPE:
        setType((OsExpression)newValue);
        return;
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__SIDE:
        setSide((OsRelationSide)newValue);
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
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__NAME:
        setName(NAME_EDEFAULT);
        return;
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__TYPE:
        setType((OsExpression)null);
        return;
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__SIDE:
        setSide(SIDE_EDEFAULT);
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
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__TYPE:
        return type != null;
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE__SIDE:
        return side != SIDE_EDEFAULT;
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
    result.append(", side: ");
    result.append(side);
    result.append(')');
    return result.toString();
  }

} //OsRelationClauseImpl
