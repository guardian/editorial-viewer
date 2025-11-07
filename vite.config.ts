import { resolve } from 'node:path'

import { defineConfig } from 'vite'

export default defineConfig({
    publicDir: false,
    build: {
        outDir: 'public',
        emptyOutDir: false,
        lib: {
            entry: resolve(__dirname, 'src/index.ts'),
            fileName: 'bundle',
            formats: ['es'],
        },
    },
});
