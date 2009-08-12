/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.directory;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Jeff C. Phillips
 */
public class DirectoryPerson implements Comparable<Object>, Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = -2333305323300083640L;
   private ArrayList<Integer> policies = null;
   private int bemsid;
   private int bluesId;
   private String building;
   private String city;
   private String country;
   private String department;
   private String departmentName;
   private String email;
   private String externalCompany;
   private String fullName;
   private String hRDeptDesc;
   private String hRDeptNumber;
   private String mailCode;
   private String mailStop;
   private String manager;
   private String personType;
   private String phone;
   private String searchName;
   private String sponsor;
   private String state;
   private String title;
   private String uSPersonStatus;
   private boolean hasPolicy = false;

   public DirectoryPerson() {
      policies = new ArrayList<Integer>();
      bemsid = -1;
      bluesId = -1;
      building = "";
      city = "";
      country = "";
      department = "";
      departmentName = "";
      email = "";
      externalCompany = "";
      fullName = "";
      hRDeptDesc = "";
      hRDeptNumber = "";
      mailCode = "";
      mailStop = "";
      manager = "";
      personType = "";
      phone = "";
      searchName = "";
      sponsor = "";
      state = "";
      title = "";
      uSPersonStatus = "";
   }

   /**
    * @return Returns the fullName.
    */
   public String getFullName() {
      return fullName;
   }

   /**
    * @param fullName The fullName to set.
    */
   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   /**
    * @return Returns the bems.
    */
   public int getBemsid() {
      return bemsid;
   }

   /**
    * @param bems The bems to set.
    */
   public void setBemsid(int bems) {
      this.bemsid = bems;
   }

   /**
    * @return Returns the bluesId.
    */
   public int getBluesId() {
      return bluesId;
   }

   /**
    * @param bluesId The bluesId to set.
    */
   public void setBluesId(int bluesId) {
      this.bluesId = bluesId;
   }

   /**
    * @return Returns the building.
    */
   public String getBuilding() {
      return building;
   }

   /**
    * @param building The building to set.
    */
   public void setBuilding(String building) {
      this.building = building;
   }

   /**
    * @return Returns the city.
    */
   public String getCity() {
      return city;
   }

   /**
    * @param city The city to set.
    */
   public void setCity(String city) {
      this.city = city;
   }

   /**
    * @return Returns the country.
    */
   public String getCountry() {
      return country;
   }

   /**
    * @param country The country to set.
    */
   public void setCountry(String country) {
      this.country = country;
   }

   /**
    * @return Returns the department.
    */
   public String getDepartment() {
      return department;
   }

   /**
    * @param department The department to set.
    */
   public void setDepartment(String department) {
      this.department = department;
   }

   /**
    * @return Returns the departmentName.
    */
   public String getDepartmentName() {
      return departmentName;
   }

   /**
    * @param departmentName The departmentName to set.
    */
   public void setDepartmentName(String departmentName) {
      this.departmentName = departmentName;
   }

   /**
    * @return Returns the email.
    */
   public String getEmail() {
      return email;
   }

   /**
    * @param email The email to set.
    */
   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @return Returns the externalCompany.
    */
   public String getExternalCompany() {
      return externalCompany;
   }

   /**
    * @param externalCompany The externalCompany to set.
    */
   public void setExternalCompany(String externalCompany) {
      this.externalCompany = externalCompany;
   }

   /**
    * @return Returns the hRDeptDesc.
    */
   public String getHRDeptDesc() {
      return hRDeptDesc;
   }

   /**
    * @param deptDesc The hRDeptDesc to set.
    */
   public void setHRDeptDesc(String deptDesc) {
      hRDeptDesc = deptDesc;
   }

   /**
    * @return Returns the hRDeptNumber.
    */
   public String getHRDeptNumber() {
      return hRDeptNumber;
   }

   /**
    * @param deptNumber The hRDeptNumber to set.
    */
   public void setHRDeptNumber(String deptNumber) {
      hRDeptNumber = deptNumber;
   }

   /**
    * @return Returns the mailCode.
    */
   public String getMailCode() {
      return mailCode;
   }

   /**
    * @param mailCode The mailCode to set.
    */
   public void setMailCode(String mailCode) {
      this.mailCode = mailCode;
   }

   /**
    * @return Returns the mailStop.
    */
   public String getMailStop() {
      return mailStop;
   }

   /**
    * @param mailStop The mailStop to set.
    */
   public void setMailStop(String mailStop) {
      this.mailStop = mailStop;
   }

   /**
    * @return Returns the manager.
    */
   public String getManager() {
      return manager;
   }

   /**
    * @param manager The manager to set.
    */
   public void setManager(String manager) {
      this.manager = manager;
   }

   /**
    * @return Returns the personType.
    */
   public String getPersonType() {
      return personType;
   }

   /**
    * @param personType The personType to set.
    */
   public void setPersonType(String personType) {
      this.personType = personType;
   }

   /**
    * @return Returns the phone.
    */
   public String getPhone() {
      return phone;
   }

   /**
    * @param phone The phone to set.
    */
   public void setPhone(String phone) {
      this.phone = phone;
   }

   /**
    * @return Returns the searchName.
    */
   public String getSearchName() {
      return searchName;
   }

   /**
    * @param searchName The searchName to set.
    */
   public void setSearchName(String searchName) {
      this.searchName = searchName;
   }

   /**
    * @return Returns the sponsor.
    */
   public String getSponsor() {
      return sponsor;
   }

   /**
    * @param sponsor The sponsor to set.
    */
   public void setSponsor(String sponsor) {
      this.sponsor = sponsor;
   }

   /**
    * @return Returns the state.
    */
   public String getState() {
      return state;
   }

   /**
    * @param state The state to set.
    */
   public void setState(String state) {
      this.state = state;
   }

   /**
    * @return Returns the title.
    */
   public String getTitle() {
      return title;
   }

   /**
    * @param title The title to set.
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * @return Returns the uSPersonStatus.
    */
   public String getUSPersonStatus() {
      return uSPersonStatus;
   }

   /**
    * @param personStatus The uSPersonStatus to set.
    */
   public void setUSPersonStatus(String personStatus) {
      uSPersonStatus = personStatus;
   }

   public int compareTo(Object person) {
      return fullName.compareTo(((DirectoryPerson) person).fullName);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof DirectoryPerson)) {
         return false;
      }
      DirectoryPerson dp = (DirectoryPerson) obj;
      return this.fullName.equals(dp.fullName);
   }

   /**
    * @return Returns the policies.
    */
   public ArrayList<Integer> getPolicies() {
      return policies;
   }

   /**
    * @param policies The policies to set.
    */
   public void setPolicies(ArrayList<Integer> policies) {
      this.policies = policies;
   }

   public boolean isHasPolicy() {
      return hasPolicy;
   }

   public void setHasPolicy(boolean hasPolicy) {
      this.hasPolicy = hasPolicy;
   }

   @Override
   public String toString() {
      return fullName + " : " + this.bemsid + " : " + email;
   }
}
