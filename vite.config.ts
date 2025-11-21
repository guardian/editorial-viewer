import { resolve } from 'node:path'

import { defineConfig } from 'vite'

export default defineConfig({
    publicDir: false,
    build: {
        outDir: 'public',
        emptyOutDir: false,
        lib: {
            entry: resolve(__dirname, 'src/index.ts'),
            // Just doing fileName: 'bundle' results in bundle.iife.js being generated
            fileName: () => 'bundle.js',
            formats: ['iife'],
            name: 'editorialViewer',
        },
    },
});
