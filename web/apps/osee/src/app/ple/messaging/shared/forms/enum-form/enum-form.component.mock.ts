/*********************************************************************
 * Copyright (c) 2022 Boeing
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
	Component,
	Output,
	computed,
	input,
	model,
	output,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import {
	EnumerationSetQuery,
	andDescriptionQuery,
} from '@osee/messaging/shared/query';
import type { enumeration } from '@osee/messaging/shared/types';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
} from '@osee/transactions/types';
import { map, of, switchMap } from 'rxjs';
import { EnumFormComponent } from './enum-form.component';

@Component({
	selector: 'osee-enum-form',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockEnumFormUniqueComponent implements Partial<EnumFormComponent> {
	bitSize = input.required<string>();
	enumSetName = input.required<string>();

	enumSetId = input.required<string>();

	enums = model.required<enumeration[]>();
	private _enumString = computed(() =>
		this.enums()
			.map((x) => `${x.ordinal.value} = ${x.name.value}`)
			.join('\n')
	);

	private _enumString$ = toObservable(this._enumString);

	@Output() enumSetString = this._enumString$;
	@Output() unique = this._enumString$.pipe(
		switchMap((description) =>
			of(description).pipe(
				switchMap((description) =>
					of(
						new EnumerationSetQuery(undefined, [
							new andDescriptionQuery(description),
						])
					).pipe(switchMap((query) => of([])))
				)
			)
		),
		map((results) => (results.length > 0 ? false : true))
	);
	tx = output<{
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}>();
}
