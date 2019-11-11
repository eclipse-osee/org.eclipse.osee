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
import { Router } from "@angular/router";
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};


@Injectable()
export class AuthService {

  authenticated = false;
  userDetails = {};

  constructor(private http: HttpClient, private router: Router) {

    this.authenticated = false;

  }

  login(loginData: any) {
    return this.authenticate(loginData);
  }

  authenticate(credentials) {
    var headers = credentials ? {
      authorization: "Basic " + btoa(credentials.userId + ":" + credentials.Password),
      // 'X-Requested-With': 'XMLHttpRequest'
    } : {};
    console.log(headers);
    return this.http.get('user', { headers: headers });
  }

  logout() {
    return this.http.post('logout',{});
  }


}
