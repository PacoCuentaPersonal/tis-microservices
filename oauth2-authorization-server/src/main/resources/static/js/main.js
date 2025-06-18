import { ModalHandler } from './modules/modal-handler.js';
import { FormHandler } from './modules/form-handler.js';
import { TableActions } from './modules/table-actions.js';
import { PaginationHandler } from './modules/pagination-handler.js';

class App {
    constructor() {
        this.init();
    }

    init() {
        // Esperar a que el DOM esté listo
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.initializeModules());
        } else {
            this.initializeModules();
        }
    }

    initializeModules() {
        // Inicializar módulos
        this.modalHandler = new ModalHandler();
        this.formHandler = new FormHandler();
        this.tableActions = new TableActions();
        this.paginationHandler = new PaginationHandler();

        // Inicializar otras funcionalidades
        this.initializeTooltips();
        this.initializeAccessibility();
    }

    initializeTooltips() {
        // Agregar tooltips a botones
        document.querySelectorAll('[title]').forEach(element => {
            element.addEventListener('mouseenter', (e) => {
                const tooltip = document.createElement('div');
                tooltip.className = 'tooltip';
                tooltip.textContent = e.target.title;
                document.body.appendChild(tooltip);

                const rect = e.target.getBoundingClientRect();
                tooltip.style.top = `${rect.top - 30}px`;
                tooltip.style.left = `${rect.left + rect.width / 2}px`;
            });

            element.addEventListener('mouseleave', () => {
                document.querySelectorAll('.tooltip').forEach(t => t.remove());
            });
        });
    }

    initializeAccessibility() {
        // Agregar navegación por teclado
        document.addEventListener('keydown', (e) => {
            // Esc para cerrar modales
            if (e.key === 'Escape') {
                const modal = document.querySelector('.modal-overlay');
                if (modal) {
                    modal.style.display = 'none';
                }
            }
        });
    }
}

// Inicializar la aplicación
new App();