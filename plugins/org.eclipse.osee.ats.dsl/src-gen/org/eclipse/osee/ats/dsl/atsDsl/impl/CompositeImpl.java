/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.Composite;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutItem;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Composite</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CompositeImpl#getNumColumns <em>Num Columns</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CompositeImpl#getLayoutItems <em>Layout Items</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.CompositeImpl#getOptions <em>Options</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CompositeImpl extends LayoutItemImpl implements Composite
{
  /**
   * The default value of the '{@link #getNumColumns() <em>Num Columns</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNumColumns()
   * @generated
   * @ordered
   */
  protected static final int NUM_COLUMNS_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getNumColumns() <em>Num Columns</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNumColumns()
   * @generated
   * @ordered
   */
  protected int numColumns = NUM_COLUMNS_EDEFAULT;

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
   * The cached value of the '{@link #getOptions() <em>Options</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOptions()
   * @generated
   * @ordered
   */
  protected EList<String> options;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CompositeImpl()
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
    return AtsDslPackage.Literals.COMPOSITE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getNumColumns()
  {
    return numColumns;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNumColumns(int newNumColumns)
  {
    int oldNumColumns = numColumns;
    numColumns = newNumColumns;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.COMPOSITE__NUM_COLUMNS, oldNumColumns, numColumns));
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
      layoutItems = new EObjectContainmentEList<LayoutItem>(LayoutItem.class, this, AtsDslPackage.COMPOSITE__LAYOUT_ITEMS);
    }
    return layoutItems;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getOptions()
  {
    if (options == null)
    {
      options = new EDataTypeEList<String>(String.class, this, AtsDslPackage.COMPOSITE__OPTIONS);
    }
    return options;
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
      case AtsDslPackage.COMPOSITE__LAYOUT_ITEMS:
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
      case AtsDslPackage.COMPOSITE__NUM_COLUMNS:
        return getNumColumns();
      case AtsDslPackage.COMPOSITE__LAYOUT_ITEMS:
        return getLayoutItems();
      case AtsDslPackage.COMPOSITE__OPTIONS:
        return getOptions();
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
      case AtsDslPackage.COMPOSITE__NUM_COLUMNS:
        setNumColumns((Integer)newValue);
        return;
      case AtsDslPackage.COMPOSITE__LAYOUT_ITEMS:
        getLayoutItems().clear();
        getLayoutItems().addAll((Collection<? extends LayoutItem>)newValue);
        return;
      case AtsDslPackage.COMPOSITE__OPTIONS:
        getOptions().clear();
        getOptions().addAll((Collection<? extends String>)newValue);
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
      case AtsDslPackage.COMPOSITE__NUM_COLUMNS:
        setNumColumns(NUM_COLUMNS_EDEFAULT);
        return;
      case AtsDslPackage.COMPOSITE__LAYOUT_ITEMS:
        getLayoutItems().clear();
        return;
      case AtsDslPackage.COMPOSITE__OPTIONS:
        getOptions().clear();
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
      case AtsDslPackage.COMPOSITE__NUM_COLUMNS:
        return numColumns != NUM_COLUMNS_EDEFAULT;
      case AtsDslPackage.COMPOSITE__LAYOUT_ITEMS:
        return layoutItems != null && !layoutItems.isEmpty();
      case AtsDslPackage.COMPOSITE__OPTIONS:
        return options != null && !options.isEmpty();
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
    result.append(" (numColumns: ");
    result.append(numColumns);
    result.append(", options: ");
    result.append(options);
    result.append(')');
    return result.toString();
  }

} //CompositeImpl
