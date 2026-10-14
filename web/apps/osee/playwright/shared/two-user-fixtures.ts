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
import { test as base, expect, Page } from '@ngx-playwright/test';

/**
 * Fixture for the two-user (multi-context) SSE tests.
 *
 * These tests need each "user" in its OWN browser context (distinct auth/session, its own SSE
 * connection), which means creating contexts directly off `browser`. Playwright's config-level
 * `video`/`trace` only cover the fixture-managed context, so those manually-created contexts would
 * otherwise have no video AND could be leaked if a test forgot to close them.
 *
 * This `userPage` fixture removes both problems: it is a factory a test calls once per user
 * (`const joe = await userPage(DEMO_USERS.joe)`), and the fixture OWNS teardown — so it always
 * closes every context it created and keeps each context's video ONLY when the test failed
 * (attached to the report), deleting it on success. No per-test close, no per-test testInfo, and
 * nothing to forget.
 */

/** A demo user identity; only the account id is needed to authenticate the context. */
export type demoUser = { accountId: string };

/** Factory: opens a page in a fresh, video-recording context authenticated as the given user. */
export type userPageFactory = (user: demoUser) => Promise<Page>;

export const test = base.extend<{ userPage: userPageFactory }>({
	userPage: async ({ browser }, use, testInfo) => {
		const pages: Page[] = [];

		const factory: userPageFactory = async (user) => {
			// Record video per context (config `video` can't see browser-created contexts). Demo auth
			// reads `osee.account.id` from localStorage at app boot, so seed it before the app loads.
			const context = await browser.newContext({
				recordVideo: { dir: testInfo.outputDir },
			});
			await context.addInitScript((accountId) => {
				localStorage.setItem('osee.account.id', accountId);
			}, user.accountId);
			const page = await context.newPage();
			pages.push(page);
			return page;
		};

		await use(factory);

		// Teardown (always runs): close each context, retain its video only on failure.
		const failed =
			testInfo.status !== testInfo.expectedStatus &&
			testInfo.status !== 'skipped';
		for (let i = 0; i < pages.length; i++) {
			const page = pages[i];
			const video = page.video();
			await page.context().close(); // flushes the video to disk
			if (!video) {
				continue;
			}
			if (failed) {
				await testInfo
					.attach(`user-video-${i}`, {
						path: await video.path(),
						contentType: 'video/webm',
					})
					.catch(() => undefined);
			} else {
				await video.delete().catch(() => undefined);
			}
		}
	},
});

export { expect };
