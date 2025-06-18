export class ModalHandler {
    constructor() {
        this.init();
    }

    init() {
        this.handleSuccessModal();
        this.handleErrorModal();
        this.attachCloseHandlers();
    }

    handleSuccessModal() {
        const successModal = document.getElementById('successMessage');
        if (successModal) {
            // Auto-cerrar después de 3 segundos
            setTimeout(() => {
                this.closeModal(successModal);
            }, 3000);
        }
    }

    handleErrorModal() {
        const errorModal = document.getElementById('errorModal');
        if (errorModal) {
            // El modal de error no se cierra automáticamente
        }
    }

    attachCloseHandlers() {
        // Cerrar con botón
        document.querySelectorAll('.modal-close, .modal-content button').forEach(button => {
            button.addEventListener('click', (e) => {
                const modal = e.target.closest('.modal-overlay');
                if (modal) {
                    this.closeModal(modal);
                }
            });
        });

        // Cerrar al hacer clic fuera
        document.querySelectorAll('.modal-overlay').forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    this.closeModal(modal);
                }
            });
        });
    }

    closeModal(modal) {
        modal.style.animation = 'fadeOut 0.3s ease';
        setTimeout(() => {
            modal.style.display = 'none';
        }, 300);
    }

    showModal(message, type = 'success') {
        const modalHTML = `
            <div class="modal-overlay" id="dynamicModal">
                <div class="modal-content ${type}">
                    <button class="modal-close">&times;</button>
                    <p>${message}</p>
                    <button class="btn btn-primary">Close</button>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', modalHTML);
        this.attachCloseHandlers();
    }
}