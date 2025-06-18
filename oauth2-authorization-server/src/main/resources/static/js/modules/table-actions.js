export class TableActions {
    constructor() {
        this.confirmMessages = {
            account: '¿Estás seguro de que quieres eliminar esta cuenta?',
            client: '¿Estás seguro de que quieres eliminar este cliente?',
            roles: '¿Estás seguro de que quieres eliminar este rol?'
        };

        this.init();
    }

    init() {
        this.attachDeleteHandlers();
        this.attachSortHandlers();
    }

    attachDeleteHandlers() {
        // Cuentas - mantener página actual y búsqueda
        document.querySelectorAll('.btn-delete-account').forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                const id = button.getAttribute('data-idAccount');
                const url = new URL(window.location);
                const page = url.searchParams.get('page') || '0';
                const search = url.searchParams.get('search') || '';
                const size = url.searchParams.get('size') || '10';

                this.confirmDelete('account', id,
                    `/delete-account/${id}?page=${page}&search=${encodeURIComponent(search)}&size=${size}`);
            });
        });

        // Clientes - actualizado para crear un form y hacer POST
        document.querySelectorAll('.delete-button-client').forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                const id = button.getAttribute('data-primaryidclient');
                if (confirm(this.confirmMessages.client)) {
                    // Crear y enviar formulario para DELETE de cliente
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = `/delete-client/${id}`;
                    document.body.appendChild(form);
                    form.submit();
                }
            });
        });

        // Roles
        document.querySelectorAll('.delete-button').forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                const id = button.getAttribute('data-id');
                this.confirmDelete('roles', id, `/delete-role/${id}`);
            });
        });
    }

    confirmDelete(type, id, url) {
        const message = this.confirmMessages[type];

        if (confirm(message)) {
            if (type === 'client') {
                // Para clientes, crear form y hacer POST
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = url;
                document.body.appendChild(form);
                form.submit();
            } else {
                // Para otros, usar navegación directa
                window.location.href = url;
            }
        }
    }

    attachSortHandlers() {
        document.querySelectorAll('.styled-table th').forEach(header => {
            header.style.cursor = 'pointer';
            header.addEventListener('click', () => {
                this.sortTable(header);
            });
        });
    }

    sortTable(header) {
        const table = header.closest('table');
        const tbody = table.querySelector('tbody');
        const index = Array.from(header.parentElement.children).indexOf(header);
        const rows = Array.from(tbody.querySelectorAll('tr'));

        const isAscending = header.classList.contains('sort-asc');

        rows.sort((a, b) => {
            const aValue = a.children[index].textContent.trim();
            const bValue = b.children[index].textContent.trim();

            // Intentar parsear como número si es posible
            const aNum = parseFloat(aValue);
            const bNum = parseFloat(bValue);

            if (!isNaN(aNum) && !isNaN(bNum)) {
                return isAscending ? bNum - aNum : aNum - bNum;
            }

            // Si no son números, comparar como strings
            if (isAscending) {
                return bValue.localeCompare(aValue);
            } else {
                return aValue.localeCompare(bValue);
            }
        });

        // Limpiar clases de ordenamiento
        table.querySelectorAll('th').forEach(th => {
            th.classList.remove('sort-asc', 'sort-desc');
        });

        // Agregar clase al header actual
        header.classList.add(isAscending ? 'sort-desc' : 'sort-asc');

        // Reordenar filas
        rows.forEach(row => tbody.appendChild(row));
    }
}