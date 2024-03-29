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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BatComponent } from './bat.component';
import { provideMarkdown } from 'ngx-markdown';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpClient, provideHttpClient } from '@angular/common/http';

describe('BatComponent', () => {
	let component: BatComponent;
	let fixture: ComponentFixture<BatComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [BatComponent],
			providers: [
				provideHttpClient(),
				provideHttpClientTesting(),
				provideMarkdown({ loader: HttpClient }),
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(BatComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
