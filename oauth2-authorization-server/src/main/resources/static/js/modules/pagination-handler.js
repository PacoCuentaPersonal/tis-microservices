export class PaginationHandler {
    constructor() {
        this.init();
    }

    init() {
        this.attachPageSizeHandler();
        this.attachSearchInputHandler();
    }

    attachPageSizeHandler() {
        const pageSizeSelect = document.querySelector('.page-size-select');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                this.changePageSize(e.target.value);
            });
        }
    }

    changePageSize(size) {
        const url = new URL(window.location.href);
        url.searchParams.set('size', size);
        url.searchParams.set('page', '0'); // Reset a primera página
        window.location.href = url.toString();
    }

    attachSearchInputHandler() {
        const searchInput = document.querySelector('.search-input');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                // Sanitización: remover caracteres peligrosos
                e.target.value = this.sanitizeInput(e.target.value);
            });

            // Prevenir caracteres especiales mientras se escribe
            searchInput.addEventListener('keypress', (e) => {
                const char = String.fromCharCode(e.which);
                if (!/[a-zA-Z0-9@._\-\s]/.test(char)) {
                    e.preventDefault();
                }
            });
        }
    }

    sanitizeInput(input) {
        // Solo permitir caracteres válidos para email y espacios
        return input.replace(/[^a-zA-Z0-9@._\-\s]/g, '');
    }

    // Método para mantener los parámetros de paginación en los enlaces
    updateUrlWithParams(baseUrl, params = {}) {
        const url = new URL(baseUrl, window.location.origin);
        const currentUrl = new URL(window.location.href);

        // Mantener parámetros actuales
        ['page', 'size', 'search', 'sortBy', 'sortDirection'].forEach(param => {
            const value = params[param] || currentUrl.searchParams.get(param);
            if (value) {
                url.searchParams.set(param, value);
            }
        });

        return url.toString();
    }
}