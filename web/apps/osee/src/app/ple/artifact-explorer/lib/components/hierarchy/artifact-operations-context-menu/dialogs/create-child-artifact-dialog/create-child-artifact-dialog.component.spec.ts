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
import { NgForm } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { CreateChildArtifactDialogComponent } from './create-child-artifact-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ArtifactExplorerHttpService } from '../../../../../services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../../../testing/artifact-explorer-http.service.mock';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ArtifactService } from '../../../../../../../../shared/services/ple_aware/http/artifact.service';
import { artifactServiceMock } from '../../../../../../../../shared/services/ple_aware/http/artifact.service.mock';
import { FormDirective } from '@osee/shared/directives';
import { operationTypeMock } from '../../../../../testing/artifact-explorer.data.mock';
import { createChildArtifactDialogData } from '../../../../../types/artifact-explorer';
import type { WritableSignal } from '@angular/core';
import type { attribute } from '@osee/attributes/types';
import type { ATTRIBUTETYPEID } from '@osee/attributes/constants';

/** Accessor for the protected `visibleAttributes` signal (test-only). */
function visibleAttributes(
	component: CreateChildArtifactDialogComponent
): WritableSignal<attribute<string, ATTRIBUTETYPEID>[]> {
	return (
		component as unknown as {
			visibleAttributes: WritableSignal<
				attribute<string, ATTRIBUTETYPEID>[]
			>;
		}
	).visibleAttributes;
}

/** Accessor for the protected `removeAttribute` handler (test-only). */
function removeAttribute(
	component: CreateChildArtifactDialogComponent,
	attr: attribute<string, ATTRIBUTETYPEID>
): void {
	(
		component as unknown as {
			removeAttribute: (a: attribute<string, ATTRIBUTETYPEID>) => void;
		}
	).removeAttribute(attr);
}

/** The trimmed text content of the required-field legend in the title header. */
function legendText(fixture: ComponentFixture<unknown>): string {
	const el = fixture.nativeElement as HTMLElement;
	const title = el.querySelector('[mat-dialog-title]');
	return (title?.textContent ?? '').replace(/\s+/g, ' ').trim();
}

/** The template-driven NgForm instance backing the dialog. */
function getForm(fixture: ComponentFixture<unknown>): NgForm {
	return fixture.debugElement
		.query(By.directive(NgForm))
		.injector.get(NgForm);
}

/**
 * Sets a form control's value through the forms API (not the DOM), so validity
 * updates without firing the input's `(input)` handler — which would trigger
 * the artifact-types httpResource and a real network call in the test.
 */
function setControlValue(
	fixture: ComponentFixture<unknown>,
	name: string,
	value: string
): void {
	const control = getForm(fixture).controls[name];
	if (!control) {
		throw new Error(`form control "${name}" not found`);
	}
	control.setValue(value);
	control.markAsDirty();
	control.updateValueAndValidity();
}

describe('CreateChildArtifactDialogComponent', () => {
	let component: CreateChildArtifactDialogComponent;
	let fixture: ComponentFixture<CreateChildArtifactDialogComponent>;
	let dialogRefCloseSpy: ReturnType<typeof vi.fn>;
	let dialogData: createChildArtifactDialogData;

	beforeEach(async () => {
		dialogRefCloseSpy = vi.fn();
		dialogData = {
			name: 'My Artifact',
			artifactTypeId: '123',
			parentArtifactId: '1111',
			attributes: [
				{
					name: 'Extension',
					value: 'md',
					typeId: '1152921504606847064' as never,
					id: '-1',
					gammaId: '-1',
					storeType: 'String',
				},
			],
			operationType: operationTypeMock,
		} as createChildArtifactDialogData;

		await TestBed.configureTestingModule({
			imports: [
				CreateChildArtifactDialogComponent,
				BrowserAnimationsModule,
				FormDirective,
			],
			providers: [
				{
					provide: MatDialogRef,
					useValue: { close: dialogRefCloseSpy },
				},
				{
					provide: MAT_DIALOG_DATA,
					useValue: dialogData,
				},
				{
					provide: ArtifactExplorerHttpService,
					useValue: ArtifactExplorerHttpServiceMock,
				},
				{
					provide: ArtifactService,
					useValue: artifactServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateChildArtifactDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('createAndClose should emit a create request with keepOpen false and close the dialog', () => {
		const emitted: {
			data: createChildArtifactDialogData;
			keepOpen: boolean;
		}[] = [];
		component.create.subscribe((req) => emitted.push(req));

		component.createAndClose();

		expect(emitted.length).toBe(1);
		expect(emitted[0].keepOpen).toBe(false);
		expect(emitted[0].data.name).toBe('My Artifact');
		expect(emitted[0].data.artifactTypeId).toBe('123');
		expect(dialogRefCloseSpy).toHaveBeenCalledTimes(1);
	});

	it('createAndAddAnother should emit keepOpen true, clear the name, and keep the dialog open', () => {
		const emitted: {
			data: createChildArtifactDialogData;
			keepOpen: boolean;
		}[] = [];
		component.create.subscribe((req) => emitted.push(req));

		component.createAndAddAnother();

		expect(emitted.length).toBe(1);
		expect(emitted[0].keepOpen).toBe(true);
		// The emitted snapshot keeps the name that was submitted...
		expect(emitted[0].data.name).toBe('My Artifact');
		// ...but the live form name is cleared for the next entry.
		expect(component.data.name).toBe('');
		// Dialog stays open.
		expect(dialogRefCloseSpy).not.toHaveBeenCalled();
	});

	it('createAndAddAnother should preserve type and attribute values across entries', () => {
		// The visible attributes (seeded from the selected type) are the source
		// of truth for the submission payload.
		visibleAttributes(component).set([...dialogData.attributes]);
		const emitted: {
			data: createChildArtifactDialogData;
			keepOpen: boolean;
		}[] = [];
		component.create.subscribe((req) => emitted.push(req));

		component.createAndAddAnother();

		expect(component.data.artifactTypeId).toBe('123');
		// Attributes carry over (dialog stays open with the same visible attrs).
		expect(visibleAttributes(component)()).toEqual(dialogData.attributes);
		expect(emitted[0].data.attributes[0].value).toBe('md');
	});

	it('createAndAddAnother should emit an independent attribute snapshot', () => {
		visibleAttributes(component).set([...dialogData.attributes]);
		const emitted: {
			data: createChildArtifactDialogData;
			keepOpen: boolean;
		}[] = [];
		component.create.subscribe((req) => emitted.push(req));

		component.createAndAddAnother();
		// Mutate the live attributes after the emit; the snapshot must not change.
		visibleAttributes(component)()[0].value = 'txt';

		expect(emitted[0].data.attributes[0].value).toBe('md');
	});

	it('includes every visible attribute even when left empty', () => {
		// An added-but-untouched attribute (empty, no default) must still be
		// submitted so it is created with its (empty) default value.
		const withDefault = { ...dialogData.attributes[0], value: 'md' };
		const emptyAdded = {
			...dialogData.attributes[0],
			typeId: '999' as never,
			name: 'Qualification Method',
			value: '',
		};
		visibleAttributes(component).set([withDefault, emptyAdded]);
		const emitted: {
			data: createChildArtifactDialogData;
			keepOpen: boolean;
		}[] = [];
		component.create.subscribe((req) => emitted.push(req));

		component.createAndClose();

		expect(emitted[0].data.attributes).toHaveLength(2);
		expect(emitted[0].data.attributes[0].value).toBe('md');
		expect(emitted[0].data.attributes[1].value).toBe('');
	});

	describe('required-field legend', () => {
		it('shows the red "Required fields (*) are not all filled out" message when the name is empty (form invalid)', () => {
			// Clear the required name -> the form is invalid.
			setControlValue(fixture, 'name', '');
			fixture.detectChanges();

			const text = legendText(fixture);
			expect(text).toContain(
				'Required fields (*) are not all filled out'
			);
			expect(text).not.toContain('* indicates a required field');

			// The message renders in the warning color.
			const warning = (
				fixture.nativeElement as HTMLElement
			).querySelector('[mat-dialog-title] .tw-text-warning');
			expect(warning).toBeTruthy();
		});

		it('shows the informational "* indicates a required field" message when all required fields are filled (form valid)', () => {
			// Fill both required controls (name + artifact type) via the forms
			// API so validity updates without triggering the type-ahead resource.
			setControlValue(fixture, 'name', 'My Artifact');
			setControlValue(fixture, 'artifactTypes', 'Software Requirement');
			fixture.detectChanges();

			const text = legendText(fixture);
			expect(text).toContain('* indicates a required field');
			expect(text).not.toContain(
				'Required fields (*) are not all filled out'
			);
		});
	});

	it('excludes removed attribute instances from the emitted create payload (no lingering after delete)', () => {
		// Two instances of a repeatable type plus a distinct one.
		const qualA = {
			...dialogData.attributes[0],
			typeId: '317' as never,
			name: 'Qualification Method',
			value: 'Unspecified',
		};
		const qualB = {
			...dialogData.attributes[0],
			typeId: '317' as never,
			name: 'Qualification Method',
			value: 'Test',
		};
		const other = {
			...dialogData.attributes[0],
			typeId: '999' as never,
			name: 'Partition',
			value: 'Unspecified',
		};
		visibleAttributes(component).set([qualA, qualB, other]);

		// User removes the second Qualification Method instance.
		removeAttribute(component, qualB);

		// The working set no longer contains the removed instance.
		expect(visibleAttributes(component)()).toEqual([qualA, other]);

		const emitted: {
			data: createChildArtifactDialogData;
			keepOpen: boolean;
		}[] = [];
		component.create.subscribe((req) => emitted.push(req));

		component.createAndClose();

		// The emitted payload must not include the removed instance.
		const payload = emitted[0].data.attributes;
		expect(payload).toHaveLength(2);
		expect(
			payload.filter((a) => a.typeId === ('317' as never))
		).toHaveLength(1);
		expect(payload.some((a) => a.value === 'Test')).toBe(false);
	});
});
