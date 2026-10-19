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
import { Locator, expect } from '@ngx-playwright/test';

/**
 * Opens a `mat-select`-style dropdown and clicks one of its options, resiliently.
 *
 * Opening a mat-select with a single `.click()` can be swallowed when the trigger
 * just rendered or reflowed (e.g. a select inside a just-opened dialog or wizard
 * step): the overlay never opens (`aria-expanded` stays `false`, no listbox
 * renders), so the option never appears and the test times out waiting for it --
 * even though the option's data loaded fine. This re-drives the open until the
 * chosen option is actually visible, guarding each retry so it never re-clicks an
 * already-open panel (which would toggle it shut), capped so it cannot spin
 * forever. Then it clicks the option (or, for a multi-select checkbox list, the
 * caller passes `clickTarget` to click the pseudo-checkbox / inner element).
 *
 * Works regardless of how the trigger is located (combobox role, label+span,
 * placeholder, trigger text) because it probes the OPTION's visibility to decide
 * whether the panel opened, rather than reading state off the trigger element.
 *
 * NOTE: this is for plain mat-selects with (possibly async-loaded) static option
 * lists. It is NOT for debounced-search autocompletes driven by `.fill()` -- those
 * need per-character `pressSequentially`, a different fix.
 */
export const openSelectAndChooseOption = async (
	trigger: Locator,
	option: Locator,
	opts: { clickTarget?: Locator; optionTimeout?: number } = {}
) => {
	const optionTimeout = opts.optionTimeout ?? 60000;
	await expect(async () => {
		if (!(await option.isVisible())) {
			await trigger.click();
		}
		await expect(option).toBeVisible({ timeout: 2000 });
	}).toPass({ timeout: optionTimeout });
	await (opts.clickTarget ?? option).click();
};
