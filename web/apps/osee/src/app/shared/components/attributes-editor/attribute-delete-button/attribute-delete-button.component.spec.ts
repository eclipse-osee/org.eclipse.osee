/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { AttributeDeleteButtonComponent } from './attribute-delete-button.component';

describe('AttributeDeleteButtonComponent', () => {
	let component: AttributeDeleteButtonComponent;
	let fixture: ComponentFixture<AttributeDeleteButtonComponent>;

	const button = () =>
		(fixture.nativeElement as HTMLElement).querySelector('button');

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [AttributeDeleteButtonComponent, NoopAnimationsModule],
		}).compileComponents();

		fixture = TestBed.createComponent(AttributeDeleteButtonComponent);
		component = fixture.componentInstance;
	});

	it('should create', () => {
		fixture.componentRef.setInput('deletable', true);
		fixture.detectChanges();
		expect(component).toBeTruthy();
	});

	describe('enabled variant (deletable = true)', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('deletable', true);
			fixture.componentRef.setInput('ariaLabel', 'Remove Partition');
			fixture.detectChanges();
		});

		it('renders an enabled button with the provided aria-label', () => {
			const btn = button();
			expect(btn).toBeTruthy();
			expect(btn?.disabled).toBe(false);
			expect(btn?.getAttribute('aria-label')).toBe('Remove Partition');
		});

		it('emits delete when clicked', () => {
			let emitted = 0;
			component.delete.subscribe(() => emitted++);
			button()?.click();
			expect(emitted).toBe(1);
		});
	});

	describe('disabled variant (deletable = false)', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('deletable', false);
			fixture.componentRef.setInput(
				'disabledReason',
				'Name cannot be deleted.'
			);
			fixture.componentRef.setInput(
				'disabledAriaLabel',
				'Cannot delete Name'
			);
			fixture.detectChanges();
		});

		it('renders a disabled button with the disabled aria-label', () => {
			const btn = button();
			expect(btn).toBeTruthy();
			expect(btn?.disabled).toBe(true);
			expect(btn?.getAttribute('aria-label')).toBe('Cannot delete Name');
		});

		it('does not emit delete (no click handler on the disabled variant)', () => {
			let emitted = 0;
			component.delete.subscribe(() => emitted++);
			button()?.click();
			expect(emitted).toBe(0);
		});
	});
});
