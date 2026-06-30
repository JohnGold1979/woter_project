document.addEventListener("DOMContentLoaded", () => {
    const tableHeader = document.getElementById("tableHeader");
    const tableBody = document.getElementById("tableBody");
    const formFields = document.getElementById("formFields");
    const dictForm = document.getElementById("dictForm");
    const addEditModal = new bootstrap.Modal(document.getElementById('addEditModal'));

    let currentType = null;
    let editingId = null;

    // Получаем тип справочника из URL
    const pathParts = window.location.pathname.split('/');
    currentType = pathParts[pathParts.length - 1]; // stations, houses, streets, tariffs

    if (!['stations', 'houses', 'streets', 'tariffs'].includes(currentType)) {
        currentType = 'stations';
    }

    // Настройка таблицы и полей формы для каждого типа
    const config = {
        stations: {
            headers: ['ID', 'Название станции'],
            fields: [
                { name: 'stationId', label: 'ID', type: 'hidden' },
                { name: 'stationName', label: 'Название станции', type: 'text', required: true }
            ],
            api: '/dictionary/list/stations',
            saveApi: '/dictionary/stations/save',
            updateApi: '/dictionary/stations/update',
            deleteApi: '/dictionary/stations/delete/'
        },
        streets: {
            headers: ['ID', 'Название улицы', 'Станция'],
            fields: [
                { name: 'streetId', label: 'ID', type: 'hidden' },
                { name: 'streetName', label: 'Название улицы', type: 'text', required: true },
                { name: 'stationId', label: 'Станция', type: 'select', required: true, api: '/dictionary/list/stations' }
            ],
            api: '/dictionary/list/streets',
            saveApi: '/dictionary/streets/save',
            updateApi: '/dictionary/streets/update',
            deleteApi: '/dictionary/streets/delete/'
        },
        houses: {
            headers: ['ID', 'Дом', 'Улица', 'Станция'],
            fields: [
                { name: 'houseId', label: 'ID', type: 'hidden' },
                { name: 'house', label: 'Дом', type: 'text', required: true },
                { name: 'streetId', label: 'Улица', type: 'select', required: true, api: '/dictionary/list/streets' },
                { name: 'stationId', label: 'Станция', type: 'select', required: true, api: '/dictionary/list/stations' }
            ],
            api: '/dictionary/list/houses',
            saveApi: '/dictionary/houses/save',
            updateApi: '/dictionary/houses/update',
            deleteApi: '/dictionary/houses/delete/'
        },
        tariffs: {
            headers: ['ID', 'Название тарифа', 'Ставка', 'Статус'],
            fields: [
                { name: 'tariffId', label: 'ID', type: 'hidden' },
                { name: 'tariffName', label: 'Название тарифа', type: 'text', required: true },
                { name: 'tariffRate', label: 'Ставка', type: 'number', required: true, step: '0.01' },
                { name: 'statusId', label: 'Статус', type: 'select', required: true, options: [
                    { value: 1, text: 'Активный' },
                    { value: 2, text: 'Неактивный' }
                ]}
            ],
            api: '/dictionary/list/tariffs',
            saveApi: '/dictionary/tariffs/save',
            updateApi: '/dictionary/tariffs/update',
            deleteApi: '/dictionary/tariffs/delete/'
        }
    };

    const currentConfig = config[currentType];

    // Загрузка данных
    async function loadData() {
        try {
            const resp = await fetch(currentConfig.api);
            const data = await resp.json();
            renderTable(data);
        } catch (err) {
            console.error("Ошибка загрузки данных:", err);
        }
    }

    // Рендер таблицы
    function renderTable(data) {
        // Заголовки
        tableHeader.innerHTML = currentConfig.headers.map(h => `<th scope="col">${h}</th>`).join('') + '<th scope="col">Действия</th>';

        // Строки
        tableBody.innerHTML = data.map(item => {
            const id = item.stationId || item.streetId || item.houseId || item.tariffId;
            let row = '<tr>';
            currentConfig.fields.forEach(field => {
                const val = item[field.name];
                if (field.type === 'select') {
                    // Для select: показываем связанное название
                    if (field.name === 'streetId') {
                        row += `<td>${item.streetName ?? ''}</td>`;
                    } else if (field.name === 'stationId') {
                        row += `<td>${item.stationName ?? ''}</td>`;
                    } else {
                        row += `<td></td>`;
                    }
                } else {
                    row += `<td>${val ?? ''}</td>`;
                }
            });
            row += `
                <td>
                    <div class="btn-group btn-group-sm" role="group">
                        <button class="btn btn-outline-primary btn-edit" data-id="${id}">Ред-ть</button>
                        <button class="btn btn-outline-danger btn-delete" data-id="${id}">Удалить</button>
                    </div>
                </td>
            </tr>`;
            return row;
        }).join('');

        // Обработчики кнопок
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.addEventListener('click', () => openEditModal(btn.dataset.id, data));
        });
        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', () => deleteItem(btn.dataset.id));
        });
    }

    // Формирование полей формы
    function buildFormFields() {
        formFields.innerHTML = currentConfig.fields.map(field => {
            let html = `<div class="col-md-6"><label for="${field.name}" class="form-label">${field.label}</label>`;
            if (field.type === 'select') {
                html += `<select id="${field.name}" class="form-select" name="${field.name}" ${field.required ? 'required' : ''}>`;
                html += `<option value="">Выберите...</option>`;
                if (field.options) {
                    field.options.forEach(opt => {
                        html += `<option value="${opt.value}">${opt.text}</option>`;
                    });
                }
                html += `</select>`;
            } else {
                html += `<input type="${field.type}" class="form-control" id="${field.name}" name="${field.name}" 
                    ${field.required ? 'required' : ''} ${field.step ? `step="${field.step}"` : ''}>`;
            }
            html += `</div>`;
            return html;
        }).join('');
    }

    // Заполнение селектов (если нужно)
    async function loadSelectOptions() {
        for (const field of currentConfig.fields) {
            if (field.type === 'select' && field.api && !field.options) {
                try {
                    const resp = await fetch(field.api);
                    const data = await resp.json();
                    const select = document.getElementById(field.name);
                    if (select && data.length > 0) {
                        const currentVal = select.value;
                        // Оставляем первый option "Выберите..."
                        select.innerHTML = '<option value="">Выберите...</option>';
                        data.forEach(item => {
                            let val, text;
                            if (field.name === 'streetId') {
                                val = item.streetId;
                                text = item.streetName;
                            } else if (field.name === 'stationId') {
                                val = item.stationId;
                                text = item.stationName;
                            } else {
                                val = item[field.name];
                                text = item[field.name + 'Name'] || item[field.name];
                            }
                            select.innerHTML += `<option value="${val}">${text}</option>`;
                        });
                        if (currentVal) select.value = currentVal;
                    }
                } catch (err) {
                    console.error("Ошибка загрузки опций для", field.name, err);
                }
            }
        }
    }

    // Открытие модалки добавления
    function openAddModal() {
        editingId = null;
        dictForm.reset();
        buildFormFields();
        loadSelectOptions().then(() => {
            addEditModal.show();
        });
    }

    // Открытие модалки редактирования
    function openEditModal(id, data) {
        editingId = id;
        buildFormFields();
        const item = data.find(i => (i.stationId || i.streetId || i.houseId || i.tariffId) == id);
        if (!item) return;

        loadSelectOptions().then(() => {
            currentConfig.fields.forEach(field => {
                const el = document.getElementById(field.name);
                if (el && item[field.name] !== undefined) {
                    el.value = item[field.name];
                }
            });
            addEditModal.show();
        });
    }

    // Удаление элемента
    async function deleteItem(id) {
        if (!confirm('Вы уверены, что хотите удалить эту запись?')) return;
        try {
            const resp = await fetch(`${currentConfig.deleteApi}${id}`, { method: 'DELETE' });
            const text = await resp.text();
            if (resp.ok) {
                alert(text);
                loadData();
            } else {
                alert('Ошибка: ' + text);
            }
        } catch (err) {
            console.error("Ошибка удаления:", err);
            alert("Ошибка при удалении");
        }
    }

    // Сохранение (добавление/обновление)
    dictForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(dictForm);
        const data = {};
        formData.forEach((val, key) => {
            data[key] = val;
        });

        // Валидация
        for (const field of currentConfig.fields) {
            if (field.required && !data[field.name]) {
                alert(`Поле "${field.label}" обязательно для заполнения`);
                return;
            }
        }

        try {
            const url = editingId ? currentConfig.updateApi : currentConfig.saveApi;
            const method = 'POST';
            
            // Для edit добавляем ID в данные
            if (editingId && currentType === 'stations') {
                data['stationId'] = editingId;
            } else if (editingId && currentType === 'streets') {
                data['streetId'] = editingId;
            } else if (editingId && currentType === 'houses') {
                data['houseId'] = editingId;
            } else if (editingId && currentType === 'tariffs') {
                data['tariffId'] = editingId;
            }

            const resp = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            const text = await resp.text();
            if (resp.ok) {
                alert(text);
                addEditModal.hide();
                loadData();
            } else {
                alert('Ошибка: ' + text);
            }
        } catch (err) {
            console.error("Ошибка сохранения:", err);
            alert("Не удалось сохранить данные");
        }
    });

    // Кнопка "Добавить"
    document.getElementById('openAddModalBtn').addEventListener('click', openAddModal);

    // Первичная загрузка
    buildFormFields();
    loadData();
});