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
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { DisplayUserComponent } from './display-user.component';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('DisplayUserComponent', () => {
	let component: DisplayUserComponent;
	let fixture: ComponentFixture<DisplayUserComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatIconModule,
				MatMenuModule,
				MatButtonModule,
				RouterTestingModule,
				NoopAnimationsModule,
				DisplayUserComponent,
			],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(DisplayUserComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
