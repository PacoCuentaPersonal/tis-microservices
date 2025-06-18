// Archivo: /js/modules/auth/login.js

export class LoginHandler {
    constructor() {
        this.form = document.getElementById('loginForm');
        this.emailInput = document.getElementById('username'); // Spring Security usa 'username'
        this.passwordInput = document.getElementById('password');
        this.passwordToggle = document.querySelector('.password-toggle');
        this.checkIcon = document.querySelector('.input-icon');

        this.init();
    }

    init() {
        if (this.form) {
            this.form.addEventListener('submit', (e) => this.handleSubmit(e));
        }

        if (this.passwordToggle) {
            this.passwordToggle.addEventListener('click', () => this.togglePassword());
        }

        if (this.emailInput) {
            this.emailInput.addEventListener('input', () => this.validateEmail());
            this.emailInput.addEventListener('blur', () => this.validateEmail());
        }

        // Prevenir zoom en iOS
        this.preventZoom();
    }

    handleSubmit(e) {
        const button = this.form.querySelector('.login-button');
        if (button) {
            button.classList.add('is-loading');
        }

        // Spring manejará el resto del submit
    }

    togglePassword() {
        if (this.passwordInput.type === 'password') {
            this.passwordInput.type = 'text';
            this.passwordToggle.classList.add('showing');
        } else {
            this.passwordInput.type = 'password';
            this.passwordToggle.classList.remove('showing');
        }
    }

    validateEmail() {
        const value = this.emailInput.value.trim();

        // Validar formato de email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const isValid = emailRegex.test(value);

        if (isValid) {
            this.emailInput.classList.add('is-valid');
            this.emailInput.classList.remove('is-invalid');
            if (this.checkIcon) {
                this.checkIcon.style.display = 'block';
            }
        } else {
            this.emailInput.classList.remove('is-valid');
            if (this.checkIcon) {
                this.checkIcon.style.display = 'none';
            }
        }
    }

    preventZoom() {
        // Prevenir zoom en dispositivos móviles
        const inputs = document.querySelectorAll('input[type="email"], input[type="password"]');
        inputs.forEach(input => {
            input.addEventListener('focus', () => {
                if (window.innerWidth < 768) {
                    document.querySelector('meta[name="viewport"]').setAttribute(
                        'content',
                        'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0'
                    );
                }
            });

            input.addEventListener('blur', () => {
                document.querySelector('meta[name="viewport"]').setAttribute(
                    'content',
                    'width=device-width, initial-scale=1.0'
                );
            });
        });
    }
}

// Auto-inicializar si estamos en la página de login
if (document.querySelector('.login-page')) {
    new LoginHandler();
}