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
import { TestScheduler } from 'rxjs/testing';
import {
	OriginIdService,
	SseEventService,
	branchChangeEvent,
} from '@osee/shared/services/network';
import { BranchChangeEventService } from './branch-change-event.service';

const MY_ORIGIN = 'my-tab';

/**
 * The branch bus applies a 300ms burst-coalescing debounce, so timing tests use rxjs
 * TestScheduler virtual time (the house convention; fakeAsync is not wired into this
 * Vitest + Zone setup). Each test builds its own service so the source Subject and the
 * scheduler-patched debounce share one virtual clock.
 */
function setup(): {
	service: BranchChangeEventService;
	branchChanges$: Subject<branchChangeEvent>;
	connectionReestablished$: Subject<void>;
} {
	const branchChanges$ = new Subject<branchChangeEvent>();
	const connectionReestablished$ = new Subject<void>();

	TestBed.configureTestingModule({
		providers: [
			BranchChangeEventService,
			{
				provide: SseEventService,
				useValue: {
					branchChanges$,
					connectionReestablished$,
					connect: vi.fn(),
				},
			},
			{ provide: OriginIdService, useValue: { originId: MY_ORIGIN } },
		],
	});
	const service = TestBed.inject(BranchChangeEventService);
	service.initialize();
	return { service, branchChanges$, connectionReestablished$ };
}

function event(overrides: Partial<branchChangeEvent>): branchChangeEvent {
	return {
		branchId: '570',
		changeType: 'renamed',
		...overrides,
	} as branchChangeEvent;
}

describe('BranchChangeEventService', () => {
	afterEach(() => TestBed.resetTestingModule());

	it('drops this tab own echo by originId', () => {
		new TestScheduler((a, e) => expect(a).toEqual(e)).run(
			({ cold, expectObservable }) => {
				const { service, branchChanges$ } = setup();
				cold('a|', { a: event({ originId: MY_ORIGIN }) }).subscribe(
					branchChanges$
				);
				// Nothing ever emits.
				expectObservable(service.branchChanges$).toBe('');
			}
		);
	});

	it('delivers a remote branch change after the coalesce window', () => {
		new TestScheduler((a, e) => expect(a).toEqual(e)).run(
			({ cold, expectObservable }) => {
				const { service, branchChanges$ } = setup();
				const e = event({ changeType: 'renamed', originId: 'other' });
				// Emit at frame 1 so the share()-based branchChanges$ subscription is wired first.
				cold('-a', { a: e }).subscribe(branchChanges$);
				// Emitted 300ms after the event (the coalesce window): frame 1 + 300 = 301.
				expectObservable(service.branchChanges$).toBe('301ms x', {
					x: e,
				});
			}
		);
	});

	it('coalesces a burst of same-(branch,type) events, keeping the richest', () => {
		new TestScheduler((a, e) => expect(a).toEqual(e)).run(
			({ cold, expectObservable }) => {
				const { service, branchChanges$ } = setup();
				const rich = event({
					changeType: 'created',
					originId: 'other',
					associatedArtifactId: 'wf-123',
				});
				// Two 'created' events 50ms apart (one logical op via topic + desktop relay).
				// Only the second carries associatedArtifactId; the burst must keep that one.
				cold('a 49ms b', {
					a: event({ changeType: 'created', originId: 'other' }),
					b: rich,
				}).subscribe(branchChanges$);
				// Window resets on each event; emits 300ms after 'b' (frame 50) -> frame 350.
				expectObservable(service.branchChanges$).toBe('350ms x', {
					x: rich,
				});
			}
		);
	});

	it('treats events separated by more than the window as separate bursts', () => {
		new TestScheduler((a, e) => expect(a).toEqual(e)).run(
			({ cold, expectObservable }) => {
				const { service, branchChanges$ } = setup();
				const e1 = event({
					changeType: 'state_changed',
					originId: 'other',
				});
				const e2 = event({
					changeType: 'state_changed',
					originId: 'other',
				});
				// Start at frame 1; 400ms apart -> two independent bursts.
				// e1@1 -> emit @301; e2@401 -> emit @701.
				cold('-a 399ms b', { a: e1, b: e2 }).subscribe(branchChanges$);
				expectObservable(
					service.forBranchAndType('570', 'state_changed')
				).toBe('301ms x 399ms y', { x: e1, y: e2 });
			}
		);
	});

	it('listAffectingChanges$ emits only for list-affecting types', () => {
		new TestScheduler((a, e) => expect(a).toEqual(e)).run(
			({ cold, expectObservable }) => {
				const { service, branchChanges$ } = setup();
				const created = event({
					branchId: 'a',
					changeType: 'created',
					originId: 'other',
				});
				const stateChanged = event({
					branchId: 'b',
					changeType: 'state_changed',
					originId: 'other',
				});
				const deleted = event({
					branchId: 'c',
					changeType: 'deleted',
					originId: 'other',
				});
				// Start at frame 1; different keys debounce in parallel, each emitting 300ms
				// after its own arrival. created@1 -> 301; stateChanged@2 (filtered out);
				// deleted@1002 -> 1302.
				cold('-a b 999ms c', {
					a: created,
					b: stateChanged,
					c: deleted,
				}).subscribe(branchChanges$);
				expectObservable(service.listAffectingChanges$).toBe(
					'301ms x 1000ms y',
					{ x: created, y: deleted }
				);
			}
		);
	});

	it('emitLocalChange flows through branchChanges$ after the coalesce window', () => {
		new TestScheduler((a, e) => expect(a).toEqual(e)).run(
			({ cold, expectObservable }) => {
				const { service } = setup();
				const emit = event({
					changeType: 'committed',
					associatedArtifactId: 'wf-9',
				});
				// Drive the local emit at frame 0 via a scheduled marble.
				cold('-a', { a: emit }).subscribe((e) =>
					service.emitLocalChange(
						e.branchId,
						e.changeType,
						e.associatedArtifactId
					)
				);
				// Local emit at frame 1, coalesced downstream -> frame 1 + 300 = 301.
				expectObservable(service.branchChanges$).toBe('301ms x', {
					x: emit,
				});
			}
		);
	});

	it('forRebaseline emits the swap for the old branch id', () => {
		new TestScheduler((a, e) => expect(a).toEqual(e)).run(
			({ cold, expectObservable }) => {
				const { service, branchChanges$ } = setup();
				const rebase = event({
					branchId: '570',
					changeType: 'rebaselined',
					newBranchId: '571',
					originId: 'other',
				});
				cold('-a', { a: rebase }).subscribe(branchChanges$);
				expectObservable(service.forRebaseline('570')).toBe('301ms x', {
					x: rebase,
				});
			}
		);
	});

	it('resync$ passes through the SSE reconnect signal', () => {
		const { service, connectionReestablished$ } = setup();
		let fired = 0;
		service.resync$.subscribe(() => fired++);
		connectionReestablished$.next();
		expect(fired).toBe(1);
	});
});
