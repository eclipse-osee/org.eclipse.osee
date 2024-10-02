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
// @ts-check
import { defineConfig } from "astro/config";
import starlight from "@astrojs/starlight";
import tailwind from "@astrojs/tailwind";

// https://astro.build/config
export default defineConfig({
  integrations: [
    starlight({
      title: "OSEE",
      favicon: "/favicon.ico",
      customCss: ["./src/tailwind.css"],
      social: {
        github: "https://github.com/eclipse-osee/org.eclipse.osee",
      },
      sidebar: [
        {
          label: "Getting Started",
          items: [{ label: "What is OSEE?", slug: "getting-started/overview" }],
        },
        {
          label: "MIM",
          items: [
            { label: "MIM Overview", slug: "mim/mim-overview" },
            { label: "Data Model", slug: "mim/datamodel" },
            { label: "Example ICD", slug: "mim/example-icd" },
            {
              label: "Guides",
              items: [
                { label: "Creating an ICD", slug: "mim/guides/create-icd" },
                {
                  label: "Peer Review Workflow",
                  slug: "mim/guides/peer-review",
                },
              ],
            },
            {
              label: "Pages",
              items: [
                {
                  label: "Enumeration List Configuration",
                  slug: "mim/pages/enum-list-config",
                },
              ],
            },
          ],
        },
        {
          label: "BAT",
          items: [{ label: "BAT Overview", slug: "bat/bat-overview" }],
        },
      ],
    }),
    tailwind({
      // Disable the default base styles:
      applyBaseStyles: false,
    }),
  ],
  site: "https://eclipse-osee.github.io",
  base: "/org.eclipse.osee",
});
