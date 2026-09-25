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
import { TestBed } from '@angular/core/testing';
import { Subject } from 'rxjs';
import {
	OriginIdService,
	SseEventService,
	artifactChangeEvent,
} from '@osee/shared/services/network';
import {
	ArtifactChangeNotificationService,
	artifactInvalidation,
} from './artifact-change-notification.service';

const MY_ORIGIN = 'my-tab';

describe('ArtifactChangeNotificationService', () => {
	let service: ArtifactChangeNotificationService;
	let artifactChanges$: Subject<artifactChangeEvent>;
	let connectionReestablished$: Subject<void>;
	let connect: ReturnType<typeof vi.fn>;

	beforeEach(() => {
		artifactChanges$ = new Subject();
		connectionReestablished$ = new Subject();
		connect = vi.fn();

		TestBed.configureTestingModule({
			providers: [
				ArtifactChangeNotificationService,
				{
					provide: SseEventService,
					useValue: {
						artifactChanges$,
						connectionReestablished$,
						connect,
					},
				},
				{ provide: OriginIdService, useValue: { originId: MY_ORIGIN } },
			],
		});
		service = TestBed.inject(ArtifactChangeNotificationService);
		service.initialize();
	});

	function emitSse(event: Partial<artifactChangeEvent>): void {
		artifactChanges$.next({
			branchId: '570',
			artifactIds: ['11'],
			transactionId: '999',
			changeTypes: ['attribute_modified'],
			...event,
		} as artifactChangeEvent);
	}

	it('fans one SSE event with multiple artifactIds into one invalidation per artifact', () => {
		const received: artifactInvalidation[] = [];
		service.artifactInvalidations$.subscribe((i) => received.push(i));

		emitSse({ artifactIds: ['11', '22'], originId: 'other' });

		expect(received.map((i) => i.artifactId)).toEqual(['11', '22']);
	});

	it('drops this tab own echo by originId', () => {
		const received: artifactInvalidation[] = [];
		service.artifactInvalidations$.subscribe((i) => received.push(i));

		emitSse({ artifactIds: ['11'], originId: MY_ORIGIN });

		expect(received).toEqual([]);
	});

	it('forArtifact filters to the matching branch + artifact', () => {
		const received: artifactInvalidation[] = [];
		service.forArtifact('570', '11').subscribe((i) => received.push(i));

		emitSse({
			artifactIds: ['11'],
			transactionId: 't1',
			originId: 'other',
		});
		emitSse({
			artifactIds: ['22'],
			transactionId: 't2',
			originId: 'other',
		});
		emitSse({
			branchId: '999',
			artifactIds: ['11'],
			transactionId: 't3',
			originId: 'other',
		});

		expect(received.map((i) => i.transactionId)).toEqual(['t1']);
	});

	it('structuralChangesForBranch ignores attribute-only changes', () => {
		const received: artifactInvalidation[] = [];
		service
			.structuralChangesForBranch('570')
			.subscribe((i) => received.push(i));

		emitSse({
			artifactIds: ['11'],
			transactionId: 't1',
			changeTypes: ['attribute_modified'],
			originId: 'other',
		});
		emitSse({
			artifactIds: ['22'],
			transactionId: 't2',
			changeTypes: ['artifact_created'],
			originId: 'other',
		});

		expect(received.map((i) => i.artifactId)).toEqual(['22']);
	});

	it('forAssociatedUser matches art-id-encoded user references', () => {
		const received: artifactInvalidation[] = [];
		service
			.forAssociatedUser('570', 'user-3')
			.subscribe((i) => received.push(i));

		emitSse({
			artifactIds: ['11'],
			transactionId: 't1',
			associatedUsers: [
				{ typeId: '1', encoding: 'artId', userIds: ['user-3'] },
			],
			originId: 'other',
		});
		emitSse({
			artifactIds: ['22'],
			transactionId: 't2',
			associatedUsers: [
				{ typeId: '1', encoding: 'artId', userIds: ['user-9'] },
			],
			originId: 'other',
		});

		expect(received.map((i) => i.artifactId)).toEqual(['11']);
	});

	it('forChangedAttributeType matches events that changed the given attribute type', () => {
		const received: artifactInvalidation[] = [];
		service
			.forChangedAttributeType('570', '1152921504606847088')
			.subscribe((i) => received.push(i));

		emitSse({
			artifactIds: ['11'],
			transactionId: 't1',
			changedAttributeTypeIds: ['1152921504606847088', '999'],
			originId: 'other',
		});
		emitSse({
			artifactIds: ['22'],
			transactionId: 't2',
			changedAttributeTypeIds: ['999'],
			originId: 'other',
		});

		expect(received.map((i) => i.artifactId)).toEqual(['11']);
	});

	it('emitLocalChange marks the invalidation isLocal and delivers immediately', () => {
		const received: artifactInvalidation[] = [];
		service.artifactInvalidations$.subscribe((i) => received.push(i));

		service.emitLocalChange('570', ['11'], '999', ['attribute_modified']);

		expect(received).toHaveLength(1);
		expect(received[0].isLocal).toBe(true);
		expect(received[0].artifactId).toBe('11');
	});

	it('resync$ passes through the SSE reconnect signal', () => {
		let fired = 0;
		service.resync$.subscribe(() => fired++);
		connectionReestablished$.next();
		expect(fired).toBe(1);
	});
});
