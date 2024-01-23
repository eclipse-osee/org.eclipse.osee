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
import { enableProdMode, importProvidersFrom } from '@angular/core';

import { environment, UserHeaderService } from '@osee/environments';
import { AppComponent } from './app/app.component';
import { MarkdownModule, MarkedOptions } from 'ngx-markdown';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { bootstrapApplication } from '@angular/platform-browser';
import { OseeUrlSerializer } from './app/UrlSerializer';
import {
	provideRouter,
	UrlSerializer,
	withComponentInputBinding,
	withInMemoryScrolling,
} from '@angular/router';
import { GlobalHttpInterceptors } from '@osee/interceptors';
import { routes } from './app/app.routes';
import { extra_auth_deps } from './extra_auth_deps';

if (environment.production) {
	enableProdMode();
}

bootstrapApplication(AppComponent, {
	providers: [
		importProvidersFrom(
			MarkdownModule.forRoot({
				markedOptions: {
					provide: MarkedOptions,
					useValue: {
						gfm: true,
						breaks: true,
					},
				},
			}),
			...extra_auth_deps
		),
		{ provide: UserHeaderService, useClass: environment.headerService },
		{ provide: UrlSerializer, useClass: OseeUrlSerializer },
		provideRouter(
			routes,
			withInMemoryScrolling({
				anchorScrolling: 'enabled',
				scrollPositionRestoration: 'enabled',
			}),
			withComponentInputBinding()
		),
		provideHttpClient(withInterceptors(GlobalHttpInterceptors)),
		provideAnimations(),
	],
}).catch((err) => console.error(err));
