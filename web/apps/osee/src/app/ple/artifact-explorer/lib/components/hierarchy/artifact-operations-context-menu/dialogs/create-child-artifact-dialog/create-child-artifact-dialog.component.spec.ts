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
});
