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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { emptyNodeMock } from '@osee/messaging/shared/testing';
import { MockApplicabilitySelectorComponent } from '@osee/shared/components/testing';
import { NewNodeFormComponent } from './new-node-form.component';

describe('NewNodeFormComponent', () => {
	let component: NewNodeFormComponent;
	let fixture: ComponentFixture<NewNodeFormComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(NewNodeFormComponent, {
			set: {
				imports: [
					CommonModule,
					MatFormFieldModule,
					MatInputModule,
					FormsModule,
					MatButtonModule,
					MatSlideToggleModule,
					MockApplicabilitySelectorComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					NewNodeFormComponent,
					MatFormFieldModule,
					MatInputModule,
					FormsModule,
					MatButtonModule,
					MatSlideToggleModule,
					NoopAnimationsModule,
					MockApplicabilitySelectorComponent,
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(NewNodeFormComponent);
		component = fixture.componentInstance;
		component.node = emptyNodeMock;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
