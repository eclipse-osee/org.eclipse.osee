/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpMethods } from 'src/app/types/http-methods';
import { apiURL } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FilesService {

  constructor(private http: HttpClient) { }

  getFileAsBlob(httpMethod: HttpMethods, url: string, body: string|File|undefined) {
    const headers = new HttpHeaders().set('Content-Type', body instanceof File ? body.type : 'application/json');
    const bodyToSend = body instanceof File ? new Blob([body]) : body;
    return this.http.request(httpMethod, apiURL + url, {body: bodyToSend, headers,  responseType: 'blob'})
  }

}
