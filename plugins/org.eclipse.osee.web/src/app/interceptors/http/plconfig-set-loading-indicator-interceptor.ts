import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { finalize } from "rxjs/operators";
import { PlConfigUIStateService } from "src/app/ple/plconfig/services/pl-config-uistate.service";

/** Pass untouched request through to the next request handler. */
@Injectable()
export class PlConfigSetLoadingIndicatorInterceptor implements HttpInterceptor {
    requests: HttpRequest<any>[] = [];
    constructor(private uiStateService: PlConfigUIStateService) { };
    intercept(req: HttpRequest<any>, next: HttpHandler):
        Observable<HttpEvent<any>> {
        this.requests.push(req);
        this.uiStateService.loadingBool = true;
        return new Observable((observer: { next: (arg0: HttpEvent<any>) => void; error: (arg0: any) => void; complete: () => void; }) => {
            const subscription = next.handle(req)
                .subscribe(
                    event => {
                        if (event instanceof HttpResponse) {
                            this.removeRequest(req);
                            observer.next(event);
                        }
                    },
                    err => {
                        alert('error returned');
                        this.removeRequest(req);
                        observer.error(err);
                    },
                    () => {
                        this.removeRequest(req);
                        observer.complete();
                    });
            // remove request from queue when cancelled
            return () => {
                this.removeRequest(req);
                subscription.unsubscribe();
            };
        });
    }
    removeRequest(req: HttpRequest<any>) {
        const index = this.requests.indexOf(req);
        if (index >= 0) {
            this.requests.splice(index, 1);
        }
        this.uiStateService.loadingBool = this.requests.length > 0;
    }
}

