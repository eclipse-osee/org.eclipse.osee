/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpEvent } from '@angular/common/http';

@Injectable()
export class UsersService {

  constructor(private http: HttpClient) { }


  getAllUsers() {
    return this.http.get('service?url=/getproject/Users/allWeb');
  }

  searchUserFromLdap(userData: String) {
    return this.http.post('service?url=/getproject/Users/getLdapUsers', userData);
  }

  saveLdapUser(ldapUser: any) {
    console.log(ldapUser);

    return this.http.post('service?url=/getproject/Users/createUser', ldapUser);
  }

}
