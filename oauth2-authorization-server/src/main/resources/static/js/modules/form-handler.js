export class FormHandler {
    constructor() {
        this.forms = {
            account: {
                fields: ['id', 'rolesId', 'username', 'email', 'password'],
                editButton: '.btn-edit-account',
                clearButton: '.btn-clear-account',
                dynamicIdField: true  // Agregar esto para manejar dinámicamente el ID
            },
            client: {
                fields: ['id', 'clientType', 'clientId', 'clientSecret', 'scopes', 'redirectUri', 'postLogoutRedirectUri', 'accessTokenHours', 'refreshTokenDays', 'enableClientCredentials'],
                editButton: '.edit-button-client',
                clearButton: '.btn-clear-client',
                dynamicIdField: true
            },
            roles: {
                fields: ['id', 'name'],
                editButton: '.update-button',
                clearButton: '.btn-clear-roles'
            }
        };

        this.init();
    }

    init() {
        Object.keys(this.forms).forEach(formType => {
            this.attachEditHandlers(formType);
            this.attachClearHandlers(formType);
        });

        this.attachFormValidation();

        // Inicializar funcionalidad específica de client
        this.initClientTypeHandlers();
    }

    initClientTypeHandlers() {
        const clientTypeSelect = document.getElementById('clientType');
        if (clientTypeSelect) {
            // Agregar event listener para cambios
            clientTypeSelect.addEventListener('change', () => {
                this.toggleClientTypeFields();
            });

            // Toggle inicial basado en el valor actual
            this.toggleClientTypeFields();
        }
    }

    toggleClientTypeFields() {
        const clientType = document.getElementById('clientType')?.value;
        const clientSecretGroup = document.getElementById('clientSecretGroup');
        const clientCredentialsGroup = document.getElementById('clientCredentialsGroup');
        const clientSecretInput = document.getElementById('clientSecret');
        const clientTypeInfo = document.getElementById('clientTypeInfo');
        const publicInfo = document.getElementById('publicClientInfo');
        const confidentialInfo = document.getElementById('confidentialClientInfo');
        const enableClientCredentialsInput = document.getElementById('enableClientCredentials');

        if (!clientSecretGroup || !clientCredentialsGroup) return;

        if (clientType === 'PUBLIC') {
            clientSecretGroup.style.display = 'none';
            clientCredentialsGroup.style.display = 'none';
            clientSecretInput.removeAttribute('required');
            clientSecretInput.value = ''; // Clear secret for public clients
            if (enableClientCredentialsInput) {
                enableClientCredentialsInput.checked = false; // Uncheck client credentials
            }
            clientTypeInfo.style.display = 'block';
            publicInfo.style.display = 'block';
            confidentialInfo.style.display = 'none';
        } else if (clientType === 'CONFIDENTIAL') {
            clientSecretGroup.style.display = 'block';
            clientCredentialsGroup.style.display = 'block';
            // Solo requerir si es nuevo cliente (no tiene ID)
            const idField = document.getElementById('primaryIdClient');
            if (!idField || !idField.value) {
                clientSecretInput.setAttribute('required', 'required');
            }
            clientTypeInfo.style.display = 'block';
            publicInfo.style.display = 'none';
            confidentialInfo.style.display = 'block';
        } else {
            clientSecretGroup.style.display = 'none';
            clientCredentialsGroup.style.display = 'none';
            clientSecretInput.removeAttribute('required');
            clientTypeInfo.style.display = 'none';
        }
    }

    attachEditHandlers(formType) {
        const config = this.forms[formType];

        document.querySelectorAll(config.editButton).forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                this.populateForm(formType, button);
            });
        });
    }

    populateForm(formType, button) {
        const config = this.forms[formType];

        if (formType === 'account') {
            this.populateAccountForm(button);
        } else if (formType === 'client') {
            this.populateClientForm(button);
        } else if (formType === 'roles') {
            this.populateRolesForm(button);
        }

        // Scroll al formulario
        this.scrollToForm();
    }

    populateAccountForm(button) {
        const data = {
            id: button.getAttribute('data-idAccount'),
            rolesId: button.getAttribute('data-idRoles'),
            username: button.getAttribute('data-username'),
            email: button.getAttribute('data-email')
        };

        // Manejar campo ID dinámico para account
        this.handleDynamicIdField(data.id, 'account');

        this.setFormValues('account', data);

        // Limpiar el campo de password al editar
        const passwordInput = document.getElementById('password');
        if (passwordInput) {
            passwordInput.value = '';
            passwordInput.placeholder = 'Leave empty to keep current password';
            passwordInput.removeAttribute('required'); // Hacer opcional al editar
        }
    }

    populateClientForm(button) {
        const data = {
            id: button.getAttribute('data-primaryidclient'),
            clientType: button.getAttribute('data-clienttype'),
            clientId: button.getAttribute('data-clientid'),
            scopes: button.getAttribute('data-scopes'),
            redirectUri: button.getAttribute('data-redirecturi'),
            postLogoutRedirectUri: button.getAttribute('data-postlogoutredirecturi'),
            accessTokenHours: button.getAttribute('data-accesstokenhours'),
            refreshTokenDays: button.getAttribute('data-refreshtokendays')
        };

        // Manejar campo ID dinámico para client
        this.handleDynamicIdField(data.id, 'client');

        this.setFormValues('client', data);

        // Limpiar el campo de client secret al editar
        const clientSecretInput = document.getElementById('clientSecret');
        if (clientSecretInput) {
            clientSecretInput.value = '';
            clientSecretInput.placeholder = 'Leave empty to keep current secret';
        }

        // Toggle fields basado en el tipo de cliente
        this.toggleClientTypeFields();
    }

    populateRolesForm(button) {
        const data = {
            id: button.getAttribute('data-id'),
            name: button.getAttribute('data-name')
        };

        this.setFormValues('roles', data);
    }

    setFormValues(formType, data) {
        Object.keys(data).forEach(field => {
            const input = document.getElementById(this.getFieldId(formType, field));
            if (input) {
                input.value = data[field] || '';
            }
        });
    }

    getFieldId(formType, field) {
        const fieldMappings = {
            account: {
                id: 'idAccount',
                rolesId: 'rolesId',
                username: 'username',
                email: 'email',
                password: 'password'
            },
            client: {
                id: 'primaryIdClient',
                clientType: 'clientType',
                clientId: 'clientId',
                clientSecret: 'clientSecret',
                scopes: 'scopes',
                redirectUri: 'redirectUri',
                postLogoutRedirectUri: 'postLogoutRedirectUri',
                accessTokenHours: 'accessTokenHours',
                refreshTokenDays: 'refreshTokenDays',
                enableClientCredentials: 'enableClientCredentials'
            },
            roles: {
                id: 'id',
                name: 'name'
            }
        };

        return fieldMappings[formType][field];
    }

    handleDynamicIdField(idValue, formType = 'client') {
        let idFieldId;
        let formSelector;

        if (formType === 'account') {
            idFieldId = 'idAccount';
            formSelector = 'form[action*="save-account"]';
        } else {
            idFieldId = 'primaryIdClient';
            formSelector = '.form-box';
        }

        let idField = document.getElementById(idFieldId);

        // Si el campo no existe y tenemos un ID, crearlo
        if (!idField && idValue) {
            const formBox = document.querySelector(formSelector);
            const firstFormGroup = formBox.querySelector('.form-group');

            const idContainer = document.createElement('div');
            idContainer.className = 'form-group dynamic-id-field';
            idContainer.innerHTML = `
                <label for="${idFieldId}">ID</label>
                <input type="text" id="${idFieldId}" name="id" class="form-control" readonly value="${idValue}"/>
            `;

            formBox.insertBefore(idContainer, firstFormGroup);
        } else if (idField && idValue) {
            // Si el campo existe, actualizar su valor
            idField.value = idValue;
            // Asegurarse de que el campo sea visible
            const container = idField.closest('.form-group');
            if (container) {
                container.style.display = 'block';
            }
        }
    }

    attachClearHandlers(formType) {
        const config = this.forms[formType];
        const clearButton = document.querySelector(config.clearButton);

        if (clearButton) {
            clearButton.addEventListener('click', (e) => {
                e.preventDefault();
                this.clearForm(formType);
            });
        }
    }

    clearForm(formType) {
        const config = this.forms[formType];

        config.fields.forEach(field => {
            const input = document.getElementById(this.getFieldId(formType, field));
            if (input && !input.readOnly) {
                if (input.type === 'checkbox') {
                    input.checked = false;
                } else {
                    input.value = '';
                }
            }
        });

        // Remover campos ID dinámicos
        if (formType === 'account') {
            const idField = document.getElementById('idAccount');
            if (idField && idField.closest('.dynamic-id-field')) {
                idField.closest('.form-group').remove();
            }
            // Restaurar el campo password como requerido
            const passwordInput = document.getElementById('password');
            if (passwordInput) {
                passwordInput.setAttribute('required', 'required');
                passwordInput.placeholder = 'Enter your password';
            }
        } else if (formType === 'client') {
            const idField = document.getElementById('primaryIdClient');
            if (idField && idField.closest('.dynamic-id-field')) {
                idField.closest('.form-group').remove();
            }

            // Reset placeholder del client secret
            const clientSecretInput = document.getElementById('clientSecret');
            if (clientSecretInput) {
                clientSecretInput.placeholder = 'Enter Client Secret';
            }

            // Reset client type fields
            this.toggleClientTypeFields();
        }
    }

    attachFormValidation() {
        document.querySelectorAll('form').forEach(form => {
            form.addEventListener('submit', (e) => {
                if (!this.validateForm(form)) {
                    e.preventDefault();
                }
            });
        });
    }

    validateForm(form) {
        let isValid = true;

        form.querySelectorAll('[required]').forEach(field => {
            // Solo validar campos visibles
            if (field.offsetParent !== null && !field.value.trim()) {
                field.classList.add('error');
                isValid = false;
            } else {
                field.classList.remove('error');
            }
        });

        return isValid;
    }

    scrollToForm() {
        const formBox = document.querySelector('.form-box');
        if (formBox) {
            formBox.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }
}