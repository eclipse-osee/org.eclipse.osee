/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import {
	HttpTestingController,
	HttpClientTestingModule,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NonOseeToolbarComponent } from './non-osee-toolbar.component';

describe('GCToolbarComponent', () => {
	let component: NonOseeToolbarComponent;
	let fixture: ComponentFixture<NonOseeToolbarComponent>;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				NonOseeToolbarComponent,
				HttpClientTestingModule,
				RouterTestingModule,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(NonOseeToolbarComponent);
		httpTestingController = TestBed.inject(HttpTestingController);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
