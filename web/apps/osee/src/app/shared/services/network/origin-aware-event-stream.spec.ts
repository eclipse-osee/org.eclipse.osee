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
import { Subject } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { OriginAwareEventStream } from './origin-aware-event-stream';

type testEvent = { originId?: string | null; key: string; payload?: string };

const MINE = 'my-tab';

function makeStream(
	overrides: Partial<{
		suppressDuplicates: boolean;
		debounceMs: number;
		maxRecentKeys: number;
	}> = {}
) {
	return new OriginAwareEventStream<testEvent>({
		myOriginId: () => MINE,
		getOriginId: (e) => e.originId,
		getEventKey: (e) => e.key,
		...overrides,
	});
}

describe('OriginAwareEventStream', () => {
	it('drops an event whose originId matches this tab (self-echo)', () => {
		const stream = makeStream();
		const received: testEvent[] = [];
		stream.output.subscribe((e) => received.push(e));

		stream.next({ originId: MINE, key: 'a' });
		stream.next({ originId: 'other-tab', key: 'b' });

		expect(received.map((e) => e.key)).toEqual(['b']);
	});

	it('passes events with no originId (remote/desktop-originated)', () => {
		const stream = makeStream();
		const received: testEvent[] = [];
		stream.output.subscribe((e) => received.push(e));

		stream.next({ key: 'a' });
		stream.next({ originId: null, key: 'b' });
		stream.next({ originId: '', key: 'c' });

		expect(received.map((e) => e.key)).toEqual(['a', 'b', 'c']);
	});

	it('suppresses duplicate delivery of the same key', () => {
		const stream = makeStream();
		const received: testEvent[] = [];
		stream.output.subscribe((e) => received.push(e));

		stream.next({ originId: 'other', key: 'dup' });
		stream.next({ originId: 'other', key: 'dup' });
		stream.next({ originId: 'other', key: 'unique' });

		expect(received.map((e) => e.key)).toEqual(['dup', 'unique']);
	});

	it('re-admits a key after eviction beyond maxRecentKeys', () => {
		const stream = makeStream({ maxRecentKeys: 2 });
		const received: testEvent[] = [];
		stream.output.subscribe((e) => received.push(e));

		// Fill the recent-key set past its bound so 'a' is evicted (oldest-first).
		stream.next({ originId: 'other', key: 'a' });
		stream.next({ originId: 'other', key: 'b' });
		stream.next({ originId: 'other', key: 'c' }); // evicts 'a'
		// 'a' is no longer remembered, so it is admitted again (not treated as a duplicate).
		stream.next({ originId: 'other', key: 'a' });

		expect(received.map((e) => e.key)).toEqual(['a', 'b', 'c', 'a']);
	});

	it('does not suppress duplicates when suppressDuplicates is false', () => {
		const stream = makeStream({ suppressDuplicates: false });
		const received: testEvent[] = [];
		stream.output.subscribe((e) => received.push(e));

		stream.next({ originId: 'other', key: 'x' });
		stream.next({ originId: 'other', key: 'x' });

		expect(received.map((e) => e.key)).toEqual(['x', 'x']);
	});

	it('emitLocal bypasses self-echo and dedup filters', () => {
		const stream = makeStream();
		const received: testEvent[] = [];
		stream.output.subscribe((e) => received.push(e));

		// A local emit carries this tab's own originId; it must still be delivered (the acting tab
		// refreshes immediately), unlike the same event arriving over SSE.
		stream.emitLocal({ originId: MINE, key: 'local' });
		// And it bypasses dedup: the same key emitted locally twice both come through.
		stream.emitLocal({ originId: MINE, key: 'local' });

		expect(received.map((e) => e.key)).toEqual(['local', 'local']);
	});

	it('reset() re-admits previously seen keys (post-reconnect resync)', () => {
		const stream = makeStream();
		const received: testEvent[] = [];
		stream.output.subscribe((e) => received.push(e));

		stream.next({ originId: 'other', key: 'k' });
		stream.reset();
		// After reconnect the client re-GETs and may legitimately see 'k' again.
		stream.next({ originId: 'other', key: 'k' });

		expect(received.map((e) => e.key)).toEqual(['k', 'k']);
	});

	it('coalesces rapid same-key events within the debounce window', () => {
		const scheduler = new TestScheduler((actual, expected) =>
			expect(actual).toEqual(expected)
		);
		scheduler.run(({ cold, expectObservable }) => {
			const stream = new OriginAwareEventStream<testEvent>({
				myOriginId: () => MINE,
				getOriginId: (e) => e.originId,
				getEventKey: (e) => e.key,
				debounceMs: 30,
			});
			// key 'a' at frame 0 and 10 (within the 30ms window -> collapse to latest '2');
			// key 'b' at frame 51 (independent).
			const input = new Subject<testEvent>();
			cold('a 9ms b 40ms c|', {
				a: { originId: 'o', key: 'a', payload: '1' },
				b: { originId: 'o', key: 'a', payload: '2' },
				c: { originId: 'o', key: 'b', payload: '3' },
			}).subscribe(input);
			input.subscribe((e) => stream.next(e));

			// 'a' emits at frame 10+30=40; 'b' emits at frame 51+30=81.
			expectObservable(stream.output).toBe('40ms x 40ms y', {
				x: { originId: 'o', key: 'a', payload: '2' },
				y: { originId: 'o', key: 'b', payload: '3' },
			});
		});
	});

	it('throws if dedup/debounce is enabled without a getEventKey', () => {
		expect(
			() =>
				new OriginAwareEventStream<testEvent>({
					myOriginId: () => MINE,
					getOriginId: (e) => e.originId,
					// suppressDuplicates defaults true, no getEventKey -> invalid config
				})
		).toThrow();
	});

	it('allows omitting getEventKey when suppression is disabled', () => {
		expect(
			() =>
				new OriginAwareEventStream<testEvent>({
					myOriginId: () => MINE,
					getOriginId: (e) => e.originId,
					suppressDuplicates: false,
				})
		).not.toThrow();
	});
});
