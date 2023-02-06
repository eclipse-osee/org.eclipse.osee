/*********************************************************************
 * Copyright (c) 2021 Boeing
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
	BaseHarnessFilters,
	ComponentHarness,
	HarnessPredicate,
} from '@angular/cdk/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatIconHarness } from '@angular/material/icon/testing';

export class TwoLayerAddButtonHarness extends ComponentHarness {
	static hostSelector = 'osee-two-layer-add-button';
	getBaseButton = this.locatorFor('.add-button-base');
	getNestedButton = this.locatorFor(TwoLayerAddButtonNestedButtonHarness);
	getNestedButtons = this.locatorForAll(TwoLayerAddButtonNestedButtonHarness);
	private _getNestedButtonContainer = this.locatorForOptional(
		'.nested-add-buttons-container'
	);
	private _getBaseButtonContainer = this.locatorForOptional(
		'.base-add-button-container'
	);
	private async _forceOpen() {
		if (await !this.isOpen()) {
			this.toggleOpen();
		}
	}
	async toggleOpen() {
		const button = await this.getBaseButton();
		await button.click();
	}
	async isOpen() {
		const nested = await this._getNestedButtonContainer();
		const base = await this._getBaseButtonContainer();
		return nested !== null && base !== null;
	}

	async clickFirstOption() {
		const base = await this._getBaseButtonContainer();
		await base?.click();
	}

	async getButtonCount() {
		await this.isOpen();
		const buttons = await this.getNestedButtons();
		return (await (await this.getNestedButtons()).length) + 1;
	}
	async clickItem(options?: TwoLayerAddButtonNestedButtonHarnessFilters) {
		await this._forceOpen();
		const buttons =
			(options &&
				(await this.locatorForAll(
					TwoLayerAddButtonNestedButtonHarness.with(options)
				)())) ||
			([] as TwoLayerAddButtonNestedButtonHarness[]);
		if (!buttons.length) {
			throw Error(
				`Could not find item matching ${JSON.stringify(options)}`
			);
		}
		return buttons[0].click();
	}
}
export interface TwoLayerAddButtonNestedButtonHarnessFilters
	extends BaseHarnessFilters {
	text?: string | RegExp;
}
export class TwoLayerAddButtonNestedButtonHarness extends ComponentHarness {
	static hostSelector = '.nested-add-button-container';
	getButton = this.locatorFor(MatButtonHarness);
	getIcon = this.locatorFor(MatIconHarness);
	static with(options: TwoLayerAddButtonNestedButtonHarnessFilters) {
		return new HarnessPredicate(
			TwoLayerAddButtonNestedButtonHarness,
			options
		).addOption('text', options.text, (harness, text) =>
			HarnessPredicate.stringMatches(harness.getText(), text)
		);
	}
	async getText() {
		const button = await this.getButton();
		return await button.getText();
	}
	async click() {
		await (await this.getButton()).click();
	}
}
