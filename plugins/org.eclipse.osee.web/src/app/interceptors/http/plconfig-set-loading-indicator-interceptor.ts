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
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpHeaderResponse, HttpProgressEvent, HttpSentEvent, HttpUserEvent } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, of, OperatorFunction } from "rxjs";
import { catchError, filter, finalize, tap } from "rxjs/operators";
import { HttpLoadingService } from "src/app/services/http-loading.service";

/** Pass untouched request through to the next request handler. */
@Injectable()
export class PlConfigSetLoadingIndicatorInterceptor implements HttpInterceptor {
    requests: HttpRequest<any>[] = [];
    constructor( private loadingService:HttpLoadingService) { };
    intercept(req: HttpRequest<any>, next: HttpHandler) {
        this.requests.push(req);
        this.loadingService.loading = true;
        return next.handle(req).pipe(
            tap((event) => {
                if (event instanceof HttpResponse) {
                   this.removeRequest(req)
               }
            }, (error) => {
                alert('Request ' + req.url + " returned an error.");
                this.removeRequest(req)
            }),
            finalize(() => {
                this.removeRequest(req)
            })
        )
    }
    removeRequest(req: HttpRequest<any>) {
        const index = this.requests.indexOf(req);
        if (index >= 0) {
            this.requests.splice(index, 1);
        }
        this.loadingService.loading = this.requests.length > 0;
    }
}

