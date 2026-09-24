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
import { Component, input, model } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { Subject } from 'rxjs';
import {
	attributeConflict,
	attributeConflictResolutionDialogData,
	attributeConflictResolutionDialogResult,
	conflictResolutionAction,
	liveConflictUpdate,
	resolvedConflict,
} from '../types/attribute-conflict.types';
import { AttributeValueEditorComponent } from '../components/attribute-value-editor.component';
import { AttributeConflictResolutionDialogComponent } from './attribute-conflict-resolution-dialog.component';

/**
 * Lightweight stand-in for the real AttributeValueEditorComponent so the dialog can render without
 * pulling in the heavy MarkdownEditor/FocusLostInput dependency graph. Keeps the test focused on
 * the dialog's own resolution logic and robust against unrelated child-component changes.
 */
@Component({
	selector: 'osee-attribute-value-editor',
	template: '',
})
class StubAttributeValueEditorComponent {
	attr = input<attribute<string, ATTRIBUTETYPEID>>();
	artifactId = input<string>('');
	disabled = input(false);
	value = model<string>('');
}

type attr = attribute<string, ATTRIBUTETYPEID>;

function makeAttr(id: string, value: string): attr {
	return {
		id,
		typeId: '1000',
		gammaId: '7',
		value,
		name: 'Description',
		storeType: 'String',
		multiplicity: { id: '1', name: 'exactly one' },
	} as unknown as attr;
}

function valueConflict(
	localValue = 'mine',
	serverValue = 'theirs'
): attributeConflict {
	return {
		baseAttr: makeAttr('a1', 'orig'),
		localValue,
		serverAttr: makeAttr('a1', serverValue),
		serverDeleted: false,
		allowsMultiple: true,
	};
}

function deleteConflict(localValue = 'mine'): attributeConflict {
	return {
		baseAttr: makeAttr('a2', 'orig'),
		localValue,
		serverAttr: undefined,
		serverDeleted: true,
		allowsMultiple: false,
	};
}

describe('AttributeConflictResolutionDialogComponent', () => {
	let closeSpy: ReturnType<typeof vi.fn>;

	function create(
		conflicts: attributeConflict[],
		liveUpdates$?: Subject<liveConflictUpdate>
	): ComponentFixture<AttributeConflictResolutionDialogComponent> {
		closeSpy = vi.fn();
		const data: attributeConflictResolutionDialogData = {
			conflicts,
			autoResolved: [],
			entityName: 'WF 1',
			entityId: '100',
			liveUpdates$: liveUpdates$?.asObservable(),
		};

		TestBed.configureTestingModule({
			imports: [AttributeConflictResolutionDialogComponent],
			providers: [
				{ provide: MatDialogRef, useValue: { close: closeSpy } },
				{ provide: MAT_DIALOG_DATA, useValue: data },
			],
		});
		// Swap the heavy value-editor child for a stub so we test dialog logic, not the editor.
		TestBed.overrideComponent(AttributeConflictResolutionDialogComponent, {
			remove: { imports: [AttributeValueEditorComponent] },
			add: { imports: [StubAttributeValueEditorComponent] },
		});
		const fixture = TestBed.createComponent(
			AttributeConflictResolutionDialogComponent
		);
		fixture.detectChanges();
		return fixture;
	}

	/** Reach the protected members under test without rendering-specific coupling. */
	function api(
		fixture: ComponentFixture<AttributeConflictResolutionDialogComponent>
	) {
		return fixture.componentInstance as unknown as {
			conflicts: () => attributeConflict[];
			states: () => {
				action: conflictResolutionAction;
				manualValue: string;
				serverChanged?: boolean;
			}[];
			allResolved: () => boolean;
			setAction: (i: number, a: conflictResolutionAction) => void;
			setManualValue: (i: number, v: string) => void;
			onSubmit: () => void;
			onCancel: () => void;
		};
	}

	it('defaults a value conflict to take-theirs (accept server truth)', () => {
		const dialog = api(create([valueConflict()]));
		expect(dialog.states()[0].action).toBe('take-theirs');
	});

	it('defaults a delete conflict to accept-deletion', () => {
		const dialog = api(create([deleteConflict()]));
		expect(dialog.states()[0].action).toBe('accept-deletion');
	});

	it('allResolved is true for default (non-manual) selections', () => {
		const dialog = api(create([valueConflict(), deleteConflict()]));
		expect(dialog.allResolved()).toBe(true);
	});

	it('allResolved is false when a manual selection has an empty value', () => {
		const dialog = api(create([valueConflict('   ')]));
		dialog.setAction(0, 'manual');
		dialog.setManualValue(0, '   '); // whitespace only
		expect(dialog.allResolved()).toBe(false);

		dialog.setManualValue(0, 'a real value');
		expect(dialog.allResolved()).toBe(true);
	});

	it('onCancel closes with undefined (no resolution)', () => {
		const dialog = api(create([valueConflict()]));
		dialog.onCancel();
		expect(closeSpy).toHaveBeenCalledWith(undefined);
	});

	it('onSubmit maps take-yours to the local value', () => {
		const dialog = api(create([valueConflict('mine', 'theirs')]));
		dialog.setAction(0, 'take-yours');
		dialog.onSubmit();

		const result = closeSpy.mock
			.calls[0][0] as attributeConflictResolutionDialogResult;
		expect(result.resolutions[0].action).toBe('take-yours');
		expect(result.resolutions[0].resolvedValues).toEqual(['mine']);
	});

	it('onSubmit maps take-theirs to an empty resolved value (discard local)', () => {
		const dialog = api(create([valueConflict()]));
		dialog.setAction(0, 'take-theirs');
		dialog.onSubmit();

		const result = closeSpy.mock
			.calls[0][0] as attributeConflictResolutionDialogResult;
		expect(result.resolutions[0].resolvedValues).toEqual([]);
	});

	it('onSubmit maps take-both to [serverValue, localValue]', () => {
		const dialog = api(create([valueConflict('mine', 'theirs')]));
		dialog.setAction(0, 'take-both');
		dialog.onSubmit();

		const result = closeSpy.mock
			.calls[0][0] as attributeConflictResolutionDialogResult;
		expect(result.resolutions[0].resolvedValues).toEqual([
			'theirs',
			'mine',
		]);
	});

	it('onSubmit maps manual to the typed value', () => {
		const dialog = api(create([valueConflict()]));
		dialog.setAction(0, 'manual');
		dialog.setManualValue(0, 'merged text');
		dialog.onSubmit();

		const result = closeSpy.mock
			.calls[0][0] as attributeConflictResolutionDialogResult;
		expect(result.resolutions[0].action).toBe('manual');
		expect(result.resolutions[0].resolvedValues).toEqual(['merged text']);
	});

	it('onSubmit maps re-add (delete conflict) to the local value', () => {
		const dialog = api(create([deleteConflict('restore me')]));
		dialog.setAction(0, 're-add');
		dialog.onSubmit();

		const result = closeSpy.mock
			.calls[0][0] as attributeConflictResolutionDialogResult;
		expect(result.resolutions[0].action).toBe('re-add');
		expect(result.resolutions[0].resolvedValues).toEqual(['restore me']);
	});

	it('onSubmit maps accept-deletion to an empty resolved value', () => {
		const dialog = api(create([deleteConflict()]));
		dialog.onSubmit(); // default is accept-deletion

		const result = closeSpy.mock
			.calls[0][0] as attributeConflictResolutionDialogResult;
		expect(result.resolutions[0].action).toBe('accept-deletion');
		expect(result.resolutions[0].resolvedValues).toEqual([]);
	});

	it('onSubmit produces one resolution per conflict, in order', () => {
		const dialog = api(
			create([valueConflict('m1', 't1'), deleteConflict('m2')])
		);
		dialog.setAction(0, 'take-yours');
		dialog.setAction(1, 're-add');
		dialog.onSubmit();

		const result = closeSpy.mock
			.calls[0][0] as attributeConflictResolutionDialogResult;
		const resolutions: resolvedConflict[] = result.resolutions;
		expect(resolutions).toHaveLength(2);
		expect(resolutions[0].resolvedValues).toEqual(['m1']);
		expect(resolutions[1].resolvedValues).toEqual(['m2']);
	});

	it('preserves the user selection on a live update whose server value is unchanged', () => {
		const live = new Subject<liveConflictUpdate>();
		const conflict = valueConflict('mine', 'theirs');
		const dialog = api(create([conflict], live));
		dialog.setAction(0, 'take-yours');

		// Same server value/gamma -> user's decision must be kept.
		live.next({ conflicts: [conflict], autoResolved: [] });

		expect(dialog.states()[0].action).toBe('take-yours');
		expect(dialog.states()[0].serverChanged).toBeFalsy();
	});

	it('resets and flags a row when a live update moves its server value', () => {
		const live = new Subject<liveConflictUpdate>();
		const dialog = api(create([valueConflict('mine', 'theirs')], live));
		dialog.setAction(0, 'take-yours');

		// Server value moved again -> reset to safe default + flag it.
		const moved: attributeConflict = {
			baseAttr: makeAttr('a1', 'orig'),
			localValue: 'mine',
			serverAttr: makeAttr('a1', 'theirs-2'),
			serverDeleted: false,
			allowsMultiple: true,
		};
		live.next({ conflicts: [moved], autoResolved: [] });

		expect(dialog.states()[0].action).toBe('take-theirs'); // back to safe default
		expect(dialog.states()[0].serverChanged).toBe(true);
	});

	it('clears the serverChanged flag once the user re-acts on the row', () => {
		const live = new Subject<liveConflictUpdate>();
		const dialog = api(create([valueConflict('mine', 'theirs')], live));
		const moved: attributeConflict = {
			baseAttr: makeAttr('a1', 'orig'),
			localValue: 'mine',
			serverAttr: makeAttr('a1', 'theirs-2'),
			serverDeleted: false,
			allowsMultiple: true,
		};
		live.next({ conflicts: [moved], autoResolved: [] });
		expect(dialog.states()[0].serverChanged).toBe(true);

		dialog.setAction(0, 'take-yours');
		expect(dialog.states()[0].serverChanged).toBe(false);
	});

	it('onSubmit after a live update carries the fresh server value', () => {
		const live = new Subject<liveConflictUpdate>();
		const dialog = api(create([valueConflict('mine', 'theirs')], live));

		const moved: attributeConflict = {
			baseAttr: makeAttr('a1', 'orig'),
			localValue: 'mine',
			serverAttr: makeAttr('a1', 'theirs-2'),
			serverDeleted: false,
			allowsMultiple: true,
		};
		live.next({ conflicts: [moved], autoResolved: [] });

		// Choose take-both so the resolution echoes the (fresh) server value.
		dialog.setAction(0, 'take-both');
		dialog.onSubmit();

		const result = closeSpy.mock
			.calls[0][0] as attributeConflictResolutionDialogResult;
		expect(result.resolutions[0].resolvedValues).toEqual([
			'theirs-2',
			'mine',
		]);
	});
});
