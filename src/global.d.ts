declare global {
    interface Window {
        _baseAppUrl: string;
        _previewEnv: string;
        _actualUrl: string;
        _proxyBase: string;
        _originalPath: string;
        _csrfToken: {
            name: string;
            value: string;
        };
    }
}

// Forces TypeScript to treat this file as a module
// Otherwise the above declaration isn't used
export {}
