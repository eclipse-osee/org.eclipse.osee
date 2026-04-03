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
import { test, expect } from '@playwright/test';

test('test', async ({ page }) => {
  await page.goto('http://localhost:4200/ple');
  await page.getByRole('link', { name: 'Product Line Configuration' }).click();
  await page.getByRole('radio', { name: 'Product Line' }).check();
  await page.goto('http://localhost:4200/ple/plconfig/baseline');
  await page.getByText('Select a Branch').click();
  await page.getByText('SAW PL Hardening Branch').click();
  await page.getByRole('button', { name: 'Create Action' }).click();
  await page.getByTestId('action-title').click();
  await page.getByTestId('action-title').fill('Test');
  await page.locator('div').filter({ hasText: /^Actionable Item$/ }).nth(2).click();
  await page.getByTestId('option-SAW PL ARB').click();
  await page.locator('div').filter({ hasText: /^Description$/ }).nth(2).click();
  await page.getByTestId('action-description').fill('Desc');
  await page.locator('div').filter({ hasText: /^Change Type$/ }).nth(2).click();
  await page.getByTestId('option-Improvement').click();
  await page.getByRole('button', { name: 'Create Action' }).click();
  await page.getByRole('button', { name: 'Edit Definitions' }).click();
  await page.getByRole('menuitem', { name: 'Change Compound Applicabities' }).click();
  await page.getByRole('menuitem', { name: 'Add Compound Applicability' }).click();
  await page.locator('div').filter({ hasText: /^Select a Feature$/ }).nth(2).click();
  await page.getByRole('option', { name: 'JHU_CONTROLLER' }).click();
  await page.locator('div').filter({ hasText: /^Select a Value$/ }).nth(2).click();
  await page.getByRole('option', { name: 'Included' }).click();
  await page.locator('div').filter({ hasText: /^Select a Relationship$/ }).nth(2).click();
  await page.getByRole('option', { name: 'AND' }).click();
  await page.locator('div').filter({ hasText: /^Select a Feature$/ }).nth(2).click();
  await page.getByRole('option', { name: 'ROBOT_ARM_LIGHT' }).click();
  await page.locator('div').filter({ hasText: /^Select a Value$/ }).nth(2).click();
  await page.getByRole('option', { name: 'Included' }).click();
  await page.getByRole('button', { name: 'Confirm' }).click();
  await page.getByRole('button', { name: 'In Work' }).click();
  await page.getByRole('menuitem', { name: 'Transition to Review' }).click();
  await page.getByRole('button', { name: 'Review' }).click();
  await page.getByRole('menuitem', { name: 'Approve Transition to' }).click();
  await page.getByRole('button', { name: 'Review' }).click();
  await page.getByRole('menuitem', { name: 'Commit Branch' }).click();
  await expect(page.getByRole('cell', { name: 'JHU_CONTROLLER = Included &' })).toBeVisible();
});
