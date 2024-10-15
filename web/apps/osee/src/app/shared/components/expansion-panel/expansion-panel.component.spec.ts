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
import { ExpansionPanelComponent } from './expansion-panel.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('ExpansionPanelComponent', () => {
	let component: ExpansionPanelComponent;
	let fixture: ComponentFixture<ExpansionPanelComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ExpansionPanelComponent],
			providers: [provideNoopAnimations()],
		}).compileComponents();

		fixture = TestBed.createComponent(ExpansionPanelComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
