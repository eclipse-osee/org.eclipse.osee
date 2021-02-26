import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { PlConfigSetLoadingIndicatorInterceptor } from './plconfig-set-loading-indicator-interceptor';


/** Http interceptor providers in outside-in order */
export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: PlConfigSetLoadingIndicatorInterceptor, multi: true },
];