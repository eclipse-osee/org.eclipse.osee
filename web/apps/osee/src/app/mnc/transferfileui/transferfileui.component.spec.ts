/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TransferfileuiComponent } from './transferfileui.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('TransferfileuiComponent', () => {
	let component: TransferfileuiComponent;
	let fixture: ComponentFixture<TransferfileuiComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			providers: [
				provideHttpClient(),
				provideHttpClientTesting(),
				NoopAnimationsModule,
			],

			imports: [TransferfileuiComponent, NoopAnimationsModule],
		}).compileComponents();

		fixture = TestBed.createComponent(TransferfileuiComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
