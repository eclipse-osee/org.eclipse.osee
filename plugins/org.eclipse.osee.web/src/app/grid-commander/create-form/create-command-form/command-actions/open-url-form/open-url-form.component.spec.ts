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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { OpenUrlFormComponent } from './open-url-form.component';

describe('OpenUrlFormComponent', () => {
	let component: OpenUrlFormComponent;
	let fixture: ComponentFixture<OpenUrlFormComponent>;
	let httpTestingController: HttpTestingController;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				OpenUrlFormComponent,
				HttpClientTestingModule,
				RouterTestingModule,
				NoopAnimationsModule,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(OpenUrlFormComponent);
		component = fixture.componentInstance;
		httpTestingController = TestBed.inject(HttpTestingController);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
