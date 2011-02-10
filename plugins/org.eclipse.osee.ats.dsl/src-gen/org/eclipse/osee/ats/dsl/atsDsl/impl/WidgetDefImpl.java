/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetDef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Widget Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl#getAttributeName <em>Attribute Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl#getXWidgetName <em>XWidget Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl#getHeight <em>Height</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.WidgetDefImpl#getOption <em>Option</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WidgetDefImpl extends MinimalEObjectImpl.Container implements WidgetDef
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
   * The default value of the '{@link #getAttributeName() <em>Attribute Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributeName()
   * @generated
   * @ordered
   */
  protected static final String ATTRIBUTE_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttributeName() <em>Attribute Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributeName()
   * @generated
   * @ordered
   */
  protected String attributeName = ATTRIBUTE_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected static final String DESCRIPTION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected String description = DESCRIPTION_EDEFAULT;

  /**
   * The default value of the '{@link #getXWidgetName() <em>XWidget Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getXWidgetName()
   * @generated
   * @ordered
   */
  protected static final String XWIDGET_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getXWidgetName() <em>XWidget Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getXWidgetName()
   * @generated
   * @ordered
   */
  protected String xWidgetName = XWIDGET_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDefaultValue()
   * @generated
   * @ordered
   */
  protected static final String DEFAULT_VALUE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDefaultValue()
   * @generated
   * @ordered
   */
  protected String defaultValue = DEFAULT_VALUE_EDEFAULT;

  /**
   * The default value of the '{@link #getHeight() <em>Height</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getHeight()
   * @generated
   * @ordered
   */
  protected static final int HEIGHT_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getHeight() <em>Height</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getHeight()
   * @generated
   * @ordered
   */
  protected int height = HEIGHT_EDEFAULT;

  /**
   * The cached value of the '{@link #getOption() <em>Option</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOption()
   * @generated
   * @ordered
   */
  protected EList<String> option;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WidgetDefImpl()
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
    return AtsDslPackage.Literals.WIDGET_DEF;
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.WIDGET_DEF__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getAttributeName()
  {
    return attributeName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAttributeName(String newAttributeName)
  {
    String oldAttributeName = attributeName;
    attributeName = newAttributeName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.WIDGET_DEF__ATTRIBUTE_NAME, oldAttributeName, attributeName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDescription(String newDescription)
  {
    String oldDescription = description;
    description = newDescription;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.WIDGET_DEF__DESCRIPTION, oldDescription, description));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getXWidgetName()
  {
    return xWidgetName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setXWidgetName(String newXWidgetName)
  {
    String oldXWidgetName = xWidgetName;
    xWidgetName = newXWidgetName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.WIDGET_DEF__XWIDGET_NAME, oldXWidgetName, xWidgetName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDefaultValue(String newDefaultValue)
  {
    String oldDefaultValue = defaultValue;
    defaultValue = newDefaultValue;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.WIDGET_DEF__DEFAULT_VALUE, oldDefaultValue, defaultValue));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getHeight()
  {
    return height;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setHeight(int newHeight)
  {
    int oldHeight = height;
    height = newHeight;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.WIDGET_DEF__HEIGHT, oldHeight, height));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getOption()
  {
    if (option == null)
    {
      option = new EDataTypeEList<String>(String.class, this, AtsDslPackage.WIDGET_DEF__OPTION);
    }
    return option;
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
      case AtsDslPackage.WIDGET_DEF__NAME:
        return getName();
      case AtsDslPackage.WIDGET_DEF__ATTRIBUTE_NAME:
        return getAttributeName();
      case AtsDslPackage.WIDGET_DEF__DESCRIPTION:
        return getDescription();
      case AtsDslPackage.WIDGET_DEF__XWIDGET_NAME:
        return getXWidgetName();
      case AtsDslPackage.WIDGET_DEF__DEFAULT_VALUE:
        return getDefaultValue();
      case AtsDslPackage.WIDGET_DEF__HEIGHT:
        return getHeight();
      case AtsDslPackage.WIDGET_DEF__OPTION:
        return getOption();
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
      case AtsDslPackage.WIDGET_DEF__NAME:
        setName((String)newValue);
        return;
      case AtsDslPackage.WIDGET_DEF__ATTRIBUTE_NAME:
        setAttributeName((String)newValue);
        return;
      case AtsDslPackage.WIDGET_DEF__DESCRIPTION:
        setDescription((String)newValue);
        return;
      case AtsDslPackage.WIDGET_DEF__XWIDGET_NAME:
        setXWidgetName((String)newValue);
        return;
      case AtsDslPackage.WIDGET_DEF__DEFAULT_VALUE:
        setDefaultValue((String)newValue);
        return;
      case AtsDslPackage.WIDGET_DEF__HEIGHT:
        setHeight((Integer)newValue);
        return;
      case AtsDslPackage.WIDGET_DEF__OPTION:
        getOption().clear();
        getOption().addAll((Collection<? extends String>)newValue);
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
      case AtsDslPackage.WIDGET_DEF__NAME:
        setName(NAME_EDEFAULT);
        return;
      case AtsDslPackage.WIDGET_DEF__ATTRIBUTE_NAME:
        setAttributeName(ATTRIBUTE_NAME_EDEFAULT);
        return;
      case AtsDslPackage.WIDGET_DEF__DESCRIPTION:
        setDescription(DESCRIPTION_EDEFAULT);
        return;
      case AtsDslPackage.WIDGET_DEF__XWIDGET_NAME:
        setXWidgetName(XWIDGET_NAME_EDEFAULT);
        return;
      case AtsDslPackage.WIDGET_DEF__DEFAULT_VALUE:
        setDefaultValue(DEFAULT_VALUE_EDEFAULT);
        return;
      case AtsDslPackage.WIDGET_DEF__HEIGHT:
        setHeight(HEIGHT_EDEFAULT);
        return;
      case AtsDslPackage.WIDGET_DEF__OPTION:
        getOption().clear();
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
      case AtsDslPackage.WIDGET_DEF__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case AtsDslPackage.WIDGET_DEF__ATTRIBUTE_NAME:
        return ATTRIBUTE_NAME_EDEFAULT == null ? attributeName != null : !ATTRIBUTE_NAME_EDEFAULT.equals(attributeName);
      case AtsDslPackage.WIDGET_DEF__DESCRIPTION:
        return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
      case AtsDslPackage.WIDGET_DEF__XWIDGET_NAME:
        return XWIDGET_NAME_EDEFAULT == null ? xWidgetName != null : !XWIDGET_NAME_EDEFAULT.equals(xWidgetName);
      case AtsDslPackage.WIDGET_DEF__DEFAULT_VALUE:
        return DEFAULT_VALUE_EDEFAULT == null ? defaultValue != null : !DEFAULT_VALUE_EDEFAULT.equals(defaultValue);
      case AtsDslPackage.WIDGET_DEF__HEIGHT:
        return height != HEIGHT_EDEFAULT;
      case AtsDslPackage.WIDGET_DEF__OPTION:
        return option != null && !option.isEmpty();
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
    result.append(", attributeName: ");
    result.append(attributeName);
    result.append(", description: ");
    result.append(description);
    result.append(", xWidgetName: ");
    result.append(xWidgetName);
    result.append(", defaultValue: ");
    result.append(defaultValue);
    result.append(", height: ");
    result.append(height);
    result.append(", option: ");
    result.append(option);
    result.append(')');
    return result.toString();
  }

} //WidgetDefImpl
