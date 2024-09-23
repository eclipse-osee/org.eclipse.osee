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
      customCss: ["./src/tailwind.css"],
      // logo: {
      //   src: "./src/assets/logos/OSEE_logo.svg",
      // },
      social: {
        github: "https://github.com/eclipse-osee/org.eclipse.osee",
      },
      sidebar: [
        {
          label: "Getting Started",
          items: [{ label: "What is OSEE?", slug: "getting-started/overview" }],
        },
        {
          label: "Messaging",
          items: [
            { label: "MIM Overview", slug: "messaging/mim-overview" },
            // { label: "Data Model", slug: "messaging/datamodel" },
            // {
            //   label: "Pages",
            //   items: [
            //     { label: "Connections", slug: "messaging/pages/connections" },
            //   ],
            // },
          ],
        },
        {
          label: "BAT",
          items: [{ label: "Overview", slug: "bat/bat-overview" }],
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
