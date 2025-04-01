//import { test, expect } from '@playwright/test';
import { test, expect } from '@ngx-playwright/test';

test('test', async ({ page }) => {
  //Go to Artifact Explorer page
  await page.goto('http://localhost:4200/ple/artifact/explorer');

  //Choose a working branch, view the artifact
  await page.getByRole('radio', { name: 'Working' }).check();
  await page.goto('http://localhost:4200/ple/artifact/explorer/working');
  await page.getByText('Select a Branch').click();
  await page.getByText('SAW PL Working Branch').click();
  await page.getByRole('button', { name: 'Software Requirements', exact: true }).click();
  await page.getByRole('textbox', { name: 'value' }).click();
  await page.getByRole('radio', { name: 'Product Line' }).check();

  //Choose a baseline branch, view the artifact
  await page.goto('http://localhost:4200/ple/artifact/explorer/baseline');
  await page.getByRole('combobox', { name: 'Select a Branch' }).click();
  await page.getByText('Common').click();
  await page.getByRole('button', { name: 'OSEE Configuration' }).click();
  await page.getByRole('button', { name: 'Artifact Info' }).click();
  await page.getByLabel('folder OSEE Configuration').getByText('OSEE Configuration').click();
});