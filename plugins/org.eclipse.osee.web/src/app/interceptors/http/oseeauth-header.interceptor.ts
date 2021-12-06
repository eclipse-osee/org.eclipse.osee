/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpHeaders
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { apiURL, OSEEAuthURL } from 'src/environments/environment';
import { take } from 'rxjs/operators';
import { user } from 'src/app/userdata/types/user-data-user';

@Injectable()
export class OSEEAuthHeaderInterceptor implements HttpInterceptor {
  currentUser!:user|undefined

  constructor (private accountService: UserDataAccountService) {
    this.accountService.user.pipe(
      take(1)
    ).subscribe((user) => {
      this.currentUser = user;
    })
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (request.url.includes(apiURL) && this.currentUser!==undefined && request.url!==OSEEAuthURL) {
      request = request.clone({ headers: request.headers.set('osee.account.id', this.currentUser?.id || '') })
    }
    return next.handle(request);
  }
}
