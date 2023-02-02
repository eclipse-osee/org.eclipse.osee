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

import { CreateCommandFormComponent } from './create-command-form.component';

describe('CreateCommandFormComponent', () => {
	let component: CreateCommandFormComponent;
	let fixture: ComponentFixture<CreateCommandFormComponent>;
	let httpTestingController: HttpTestingController;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				CreateCommandFormComponent,
				HttpClientTestingModule,
				RouterTestingModule,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateCommandFormComponent);
		component = fixture.componentInstance;
		httpTestingController = TestBed.inject(HttpTestingController);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
