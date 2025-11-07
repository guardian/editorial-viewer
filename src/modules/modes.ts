export interface ViewportConfig {
    isMobile?: boolean;
    isReader?: boolean;
    isSocial?: boolean;
}

export type Mode = 'mobile-portrait' | 'mobile-landscape' | 'desktop' | 'reader' | 'social-share'

export const modes: Record<Mode, ViewportConfig> = {
    'mobile-portrait': {
        isMobile: true
    },
    'mobile-landscape': {
        isMobile: true
    },
    'desktop': {
    },
    'reader': {
        isReader: true
    },
    'social-share': {
        isSocial: true
    }
};


