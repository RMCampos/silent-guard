import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    {
      name: 'build-html',
      apply: 'build',
      transformIndexHtml: (html) => {
        return {
          html,
          tags: [
            {
              tag: 'script',
              attrs: {
                src: '/env.js'
              },
              injectTo: 'head'
            }
          ]
        };
      }
    },
    tailwindcss(),
    react()
  ],
});
