/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetDef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Widget Ref</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetRefImpl#getWidget <em>Widget</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WidgetRefImpl extends LayoutItemImpl implements WidgetRef
{
  /**
   * The cached value of the '{@link #getWidget() <em>Widget</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWidget()
   * @generated
   * @ordered
   */
  protected WidgetDef widget;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WidgetRefImpl()
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
    return AtsDslPackage.Literals.WIDGET_REF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WidgetDef getWidget()
  {
    if (widget != null && widget.eIsProxy())
    {
      InternalEObject oldWidget = (InternalEObject)widget;
      widget = (WidgetDef)eResolveProxy(oldWidget);
      if (widget != oldWidget)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, AtsDslPackage.WIDGET_REF__WIDGET, oldWidget, widget));
      }
    }
    return widget;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WidgetDef basicGetWidget()
  {
    return widget;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setWidget(WidgetDef newWidget)
  {
    WidgetDef oldWidget = widget;
    widget = newWidget;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.WIDGET_REF__WIDGET, oldWidget, widget));
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
      case AtsDslPackage.WIDGET_REF__WIDGET:
        if (resolve) return getWidget();
        return basicGetWidget();
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
      case AtsDslPackage.WIDGET_REF__WIDGET:
        setWidget((WidgetDef)newValue);
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
      case AtsDslPackage.WIDGET_REF__WIDGET:
        setWidget((WidgetDef)null);
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
      case AtsDslPackage.WIDGET_REF__WIDGET:
        return widget != null;
    }
    return super.eIsSet(featureID);
  }

} //WidgetRefImpl
