/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutDef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutItem;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Layout Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.LayoutDefImpl#getLayoutItems <em>Layout Items</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LayoutDefImpl extends LayoutTypeImpl implements LayoutDef
{
  /**
   * The cached value of the '{@link #getLayoutItems() <em>Layout Items</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLayoutItems()
   * @generated
   * @ordered
   */
  protected EList<LayoutItem> layoutItems;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LayoutDefImpl()
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
    return AtsDslPackage.Literals.LAYOUT_DEF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<LayoutItem> getLayoutItems()
  {
    if (layoutItems == null)
    {
      layoutItems = new EObjectContainmentEList<LayoutItem>(LayoutItem.class, this, AtsDslPackage.LAYOUT_DEF__LAYOUT_ITEMS);
    }
    return layoutItems;
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
      case AtsDslPackage.LAYOUT_DEF__LAYOUT_ITEMS:
        return ((InternalEList<?>)getLayoutItems()).basicRemove(otherEnd, msgs);
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
      case AtsDslPackage.LAYOUT_DEF__LAYOUT_ITEMS:
        return getLayoutItems();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case AtsDslPackage.LAYOUT_DEF__LAYOUT_ITEMS:
        getLayoutItems().clear();
        getLayoutItems().addAll((Collection<? extends LayoutItem>)newValue);
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
      case AtsDslPackage.LAYOUT_DEF__LAYOUT_ITEMS:
        getLayoutItems().clear();
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
      case AtsDslPackage.LAYOUT_DEF__LAYOUT_ITEMS:
        return layoutItems != null && !layoutItems.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //LayoutDefImpl
