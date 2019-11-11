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
import {throwError as observableThrowError,  Observable } from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import { Injectable, forwardRef, Inject } from '@angular/core';
import { HttpRequest, HttpInterceptor, HttpHandler, HttpErrorResponse, HttpEvent, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoaderService } from '../service/loader.service';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Injectable()
export class HttpInterceptorService implements HttpInterceptor {

  // ,private activeModel:NgbActiveModal
  // , @Inject(forwardRef(() => NgbActiveModal)) private activeModel: NgbActiveModal
  constructor(private router: Router, private loaderService: LoaderService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.loaderService.show();
    var headers = {
      'X-Requested-With': 'XMLHttpRequest'
    };
    req = req.clone({
      setHeaders: headers
    });
    return next.handle(req).pipe(
      map(event => {
        if (event instanceof HttpResponse) {
          this.loaderService.hide();
        }
        return event;
      }),
      catchError(this.handleError),);

  }

  public handleError = (err: HttpErrorResponse) => {
    this.loaderService.hide();
    if (this.router.url !== '/login' && (err.status === 401 || err.status === 403)) {
      // this.activeModel.close();
      this.router.navigate(['/login']);
    }
    return observableThrowError(err);
  }

}
