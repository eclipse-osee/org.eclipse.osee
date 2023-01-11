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
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { httpInterceptorProviders } from './interceptors/http/interceptor-provider';

import { MarkdownModule, MarkedOptions } from 'ngx-markdown';

//Sub-component imports
import { environment } from '../environments/environment';
import { UserHeaderService } from './userdata/services/user-header.service';
import { UrlSerializer } from '@angular/router';
import { OseeUrlSerializer } from './UrlSerializer';
import { NavContainerComponent } from './layout/lib/containers/nav-container.component';

@NgModule({
	declarations: [AppComponent],
	imports: [
		BrowserModule,
		AppRoutingModule,
		HttpClientModule,
		BrowserAnimationsModule,
		NavContainerComponent,
		MarkdownModule.forRoot({
			markedOptions: {
				provide: MarkedOptions,
				useValue: {
					gfm: true,
					breaks: true,
				},
			},
		}),
	],
	providers: [
		httpInterceptorProviders,
		{ provide: UserHeaderService, useClass: environment.headerService },
		{ provide: UrlSerializer, useClass: OseeUrlSerializer },
	],
	bootstrap: [AppComponent],
})
export class AppModule {}
