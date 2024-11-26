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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	input,
	model,
} from '@angular/core';
import {
	takeUntilDestroyed,
	toObservable,
	toSignal,
} from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { applicabilitySentinel } from '@osee/applicability/types';
import { NodeDropdownComponent } from '@osee/messaging/nodes/dropdown';
import {
	TransportTypeUiService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import { nodeData } from '@osee/messaging/shared/types';
import { RELATIONTYPEIDENUM } from '@osee/shared/types/constants';
import { addRelations, deleteRelations } from '@osee/transactions/functions';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	combineLatest,
	debounceTime,
	filter,
	map,
	of,
	pairwise,
	switchMap,
} from 'rxjs';

@Component({
	selector: 'osee-persisted-subscriber-node-dropdown',
	imports: [NodeDropdownComponent, FormsModule],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `<form>
		<osee-node-dropdown
			[(selectedNodes)]="nodes"
			[transportType]="transportType()"
			[connectionId]="connectionId()"
			validationType="subscribe"
			[hintHidden]="true" />
	</form>`,
})
export class PersistedSubscriberNodeDropdownComponent {
	private transportTypeService = inject(TransportTypeUiService);
	protected transportType = toSignal(
		this.transportTypeService.currentTransportType,
		{
			initialValue: {
				id: '-1',
				gammaId: '-1',
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: '',
				},
				byteAlignValidation: {
					id: '-1',
					typeId: '1682639796635579163',
					gammaId: '-1',
					value: false,
				},
				byteAlignValidationSize: {
					id: '-1',
					typeId: '6745328086388470469',
					gammaId: '-1',
					value: 0,
				},
				messageGeneration: {
					id: '-1',
					typeId: '6696101226215576386',
					gammaId: '-1',
					value: false,
				},
				messageGenerationType: {
					id: '-1',
					typeId: '7121809480940961886',
					gammaId: '-1',
					value: '',
				},
				messageGenerationPosition: {
					id: '-1',
					typeId: '7004358807289801815',
					gammaId: '-1',
					value: '',
				},
				minimumPublisherMultiplicity: {
					id: '-1',
					typeId: '7904304476851517',
					gammaId: '-1',
					value: 0,
				},
				maximumPublisherMultiplicity: {
					id: '-1',
					typeId: '8536169210675063038',
					gammaId: '-1',
					value: 0,
				},
				minimumSubscriberMultiplicity: {
					id: '-1',
					typeId: '6433031401579983113',
					gammaId: '-1',
					value: 0,
				},
				maximumSubscriberMultiplicity: {
					id: '-1',
					typeId: '7284240818299786725',
					gammaId: '-1',
					value: 0,
				},
				availableMessageHeaders: {
					id: '-1',
					typeId: '2811393503797133191',
					gammaId: '-1',
					value: [],
				},
				availableSubmessageHeaders: {
					id: '-1',
					typeId: '3432614776670156459',
					gammaId: '-1',
					value: [],
				},
				availableStructureHeaders: {
					id: '-1',
					typeId: '3020789555488549747',
					gammaId: '-1',
					value: [],
				},
				availableElementHeaders: {
					id: '-1',
					typeId: '3757258106573748121',
					gammaId: '-1',
					value: [],
				},
				interfaceLevelsToUse: {
					id: '-1',
					typeId: '1668394842614655222',
					gammaId: '-1',
					value: ['message', 'submessage', 'structure', 'element'],
				},
				dashedPresentation: {
					id: '-1',
					typeId: '3564212740439618526',
					gammaId: '-1',
					value: false,
				},
				spareAutoNumbering: {
					id: '-1',
					typeId: '6696101226215576390',
					gammaId: '-1',
					value: false,
				},
				directConnection: false,
				applicability: applicabilitySentinel,
			},
		}
	);
	artifactId = input.required<`${number}`>();
	connectionId = input.required<`${number}`>();

	nodes = model.required<nodeData[]>();

	private _nodeIds = computed(() => this.nodes().map((v) => v.id));

	private _nodeIds$ = toObservable(this._nodeIds).pipe(
		debounceTime(500),
		pairwise(),
		filter(([prev, curr]) => {
			const toRemove = prev.filter(
				(v) => !curr.includes(v) && v !== '-1'
			);
			const toAdd = curr.filter((v) => !prev.includes(v) && v !== '-1');
			return (
				(toAdd.length > 0 || toRemove.length > 0) &&
				prev.length !== 0 &&
				curr.length !== 0
			);
		})
	);
	private _warningService = inject(WarningDialogService);
	private _artifactId$ = toObservable(this.artifactId);
	private _ids = combineLatest([this._artifactId$, this._nodeIds$]).pipe(
		map(([artId, nodeIds]) => {
			return [artId, nodeIds[0], nodeIds[1]] as const;
		})
	);

	private _warning = this._ids.pipe(
		switchMap((ids) =>
			this._warningService
				.openMessageDialog({ id: ids[0] })
				.pipe(map((_) => ids))
		)
	);

	private _currentTxService = inject(CurrentTransactionService);
	private _tx = toSignal(
		this._warning.pipe(
			switchMap(([artId, prev, curr]) => {
				let tx = this._currentTxService.createTransaction(
					`Modifying publisher nodes for ${artId}`
				);
				const toRemove = prev.filter(
					(v) => !curr.includes(v) && v !== '-1'
				);
				const toAdd = curr.filter(
					(v) => !prev.includes(v) && v !== '-1'
				);
				const removeRelations = toRemove.map((x) => {
					return {
						typeId: RELATIONTYPEIDENUM.INTERFACEMESSAGESUBNODE,
						bArtId: x,
						aArtId: artId,
					};
				});
				const relationsToAdd = toAdd.map((x) => {
					return {
						typeId: RELATIONTYPEIDENUM.INTERFACEMESSAGESUBNODE,
						bArtId: x,
						aArtId: artId,
					};
				});
				tx = addRelations(tx, relationsToAdd);
				tx = deleteRelations(tx, removeRelations);
				return of(tx);
			}),
			this._currentTxService.performMutation(),
			takeUntilDestroyed()
		)
	);
}
