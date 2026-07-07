document.addEventListener("DOMContentLoaded", () => {
    const yearSelect = document.getElementById("yearSelect");
    const tableRows = document.querySelectorAll("#clientsTab tbody tr");
    const submitBtn = document.getElementById("submitIndBtn");
    const indicationsHeader = document.getElementById("indicationsHeader");
    const headerPersAcc = document.getElementById("headerPersAcc");
    const headerClName = document.getElementById("headerClName");

    // Row click handler for clients table
    tableRows.forEach(row => {
        row.addEventListener("click", () => {
            tableRows.forEach(r => r.classList.remove("table-active"));
            row.classList.add("table-active");

            const persAccount = row.cells[2].innerText;
            const clientName = row.cells[1].innerText;
            const year = yearSelect.value;
            
            // Update header with client info
            if (indicationsHeader && headerPersAcc && headerClName) {
                headerPersAcc.textContent = persAccount;
                headerClName.textContent = clientName;
                indicationsHeader.style.display = 'block';
            }
            
            fetchIndicationsForClient(persAccount, year);
        });
    });

    // Load last period
    fetch("/clients/period/last")
        .then(res => res.json())
        .then(data => {
            const monthSelect = document.getElementById("monthSelect");
            if (monthSelect && yearSelect) {
                monthSelect.value = data.monthId;
                yearSelect.value = data.yearId;
            }
        })
        .catch(err => console.error("Ошибка загрузки периода:", err));

    // Ctrl+Enter to add new indication
    document.addEventListener("keydown", function (event) {
        if (event.code == 'Enter' && event.ctrlKey) {
            let modal = new bootstrap.Modal(document.getElementById("indAdd"));
            modal.show();

            let selectedRow = document.querySelector("#clientsTab tbody tr.table-active");
            if (!selectedRow) {
                console.warn("Абонент не выбран!");
                return;
            }

            let persAcc = selectedRow.cells[2].textContent.trim();

            fetch(`/clients/${persAcc}`)
                .then(response => {
                    if (!response.ok) throw new Error("Клиент не найден");
                    return response.json();
                })
                .then(data => {
                    document.getElementById("persAccInd").value = data.personalAccount || "";
                    document.getElementById("clNameInd").value = data.clientName || "";
                    document.getElementById("addressInd").value = data.address || "";
                })
                .catch(err => console.error("Ошибка загрузки данных клиента:", err));
        }
    });

    // Submit new indication
    if (submitBtn) {
        submitBtn.addEventListener("click", async () => {
            const persAcc = document.getElementById("persAccInd").value.trim();
            const clInd = document.getElementById("clInd").value.trim();
            const month = document.getElementById("monthSelect").value;
            const year = yearSelect.value;

            if (!persAcc) {
                alert("Ошибка: лицевой счёт пустой!");
                return;
            }
            if (!clInd) {
                alert("Ошибка: введите показания!");
                return;
            }

            const payload = {
                personalAccount: persAcc,
                monthId: parseInt(month),
                yearId: parseInt(year),
                m3: parseFloat(clInd)
            };

            try {
                const response = await fetch("/indications/addind", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });

                const text = await response.text();
                if (!response.ok) {
                    alert("Ошибка: " + text);
                    return;
                }

                alert("Успешно: " + text);

                const modalEl = document.getElementById("indAdd");
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();

                document.getElementById("clInd").value = "";
            } catch (err) {
                console.error("Ошибка отправки:", err);
                alert("Ошибка отправки: " + err.message);
            }
        });
    }

    // Excel import handler
    document.getElementById('uploadExcelBtn')?.addEventListener('click', async function() {
        const fileInput = document.getElementById('excelFile');
        const file = fileInput.files[0];

        if (!file) {
            alert('Пожалуйста, выберите файл Excel');
            return;
        }

        const month = document.getElementById('importMonth').value;
        const year = document.getElementById('importYear').value;

        const formData = new FormData();
        formData.append('file', file);
        formData.append('month', month);
        formData.append('year', year);

        const progressDiv = document.getElementById('importProgress');
        const resultDiv = document.getElementById('importResult');
        const uploadBtn = document.getElementById('uploadExcelBtn');

        progressDiv.style.display = 'block';
        resultDiv.style.display = 'none';
        uploadBtn.disabled = true;

        try {
            const response = await fetch('/indications/import-excel', {
                method: 'POST',
                body: formData
            });

            const result = await response.json();
            resultDiv.style.display = 'block';

            if (response.ok || response.status === 206) {
                let html = `<div class="alert alert-success">
                                <i class="fa-solid fa-check-circle me-2"></i>
                                <strong>Импорт завершен!</strong><br>
                                Успешно загружено: ${result.successCount} из ${result.totalRows}
                            </div>`;

                if (result.errors && result.errors.length > 0) {
                    html += `<div class="alert alert-warning mt-2">
                                <i class="fa-solid fa-triangle-exclamation me-2"></i>
                                <strong>Ошибки при импорте:</strong>
                                <ul class="mb-0 mt-2">`;
                    result.errors.forEach(error => {
                        html += `<li>Лицевой счет: ${error.personalAccount || '?'} - ${error.message}</li>`;
                    });
                    html += `</ul></div>`;
                }

                resultDiv.innerHTML = html;

                const selectedRow = document.querySelector("#clientsTab tbody tr.table-active");
                if (selectedRow) {
                    const persAccount = selectedRow.cells[2].textContent.trim();
                    const yearVal = yearSelect.value;
                    fetchIndicationsForClient(persAccount, yearVal);
                }

                fileInput.value = '';
                setTimeout(() => {
                    const modal = bootstrap.Modal.getInstance(document.getElementById('importExcelModal'));
                    if (modal) modal.hide();
                    resultDiv.style.display = 'none';
                }, 3000);
            } else {
                resultDiv.innerHTML = `<div class="alert alert-danger">
                                            <i class="fa-solid fa-circle-exclamation me-2"></i>
                                            <strong>Ошибка!</strong><br>${result.message || result}
                                        </div>`;
            }
        } catch (error) {
            console.error('Ошибка загрузки:', error);
            resultDiv.style.display = 'block';
            resultDiv.innerHTML = `<div class="alert alert-danger">
                                        <i class="fa-solid fa-circle-exclamation me-2"></i>
                                        <strong>Ошибка!</strong><br>${error.message}
                                    </div>`;
        } finally {
            progressDiv.style.display = 'none';
            uploadBtn.disabled = false;
        }
    });

    // Fetch indications for selected client
    function fetchIndicationsForClient(persAccount, year) {
        const secondTableBody = document.querySelector("#indicationsTable tbody");

        fetch(`/indications/${persAccount}/${year}`)
            .then(res => res.json())
            .then(data => {
                secondTableBody.innerHTML = "";
                if (data.length === 0) {
                    secondTableBody.innerHTML = `<tr><td colspan="6" class="text-center">Нет данных за ${year} год</td></tr>`;
                    return;
                }
                data.forEach(ind => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                      <td>${getMonthName(ind.monthId)}</td>
                      <td>${ind.indication || 0}</td>
                      <td>${ind.m3 || 0}</td>
                      <td>${ind.tariff || 0}</td>
                      <td>${ind.summa || 0}</td>
                      <td class="text-center">
                        <button class="btn btn-sm btn-success edit-ind-btn" 
                                data-id="${ind.id}" 
                                data-month="${ind.monthId}" 
                                data-year="${ind.yearId || year}"
                                data-indication="${ind.indication || 0}">Ред-ть</button>
                       </td>
                    `;
                    secondTableBody.appendChild(row);
                });
                
                // Add click handlers to edit buttons
                document.querySelectorAll('.edit-ind-btn').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const indId = this.dataset.id;
                        const month = this.dataset.month;
                        const yearVal = this.dataset.year;
                        const indication = this.dataset.indication;
                        openEditModal(indId, month, yearVal, indication);
                    });
                });
            })
            .catch(err => console.error("Ошибка загрузки показаний:", err));
    }

    function getMonthName(monthId) {
        const months = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
                        'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'];
        return months[monthId - 1] || monthId;
    }

    function openEditModal(indId, month, year, indicationValue) {
        const selectedRow = document.querySelector("#clientsTab tbody tr.table-active");
        if (!selectedRow) {
            alert("Выберите клиента из таблицы");
            return;
        }

        const persAcc = selectedRow.cells[2].textContent.trim();
        const clientName = selectedRow.cells[1].textContent.trim();
        const address = selectedRow.cells[3].textContent.trim();

        document.getElementById("editIndId").value = indId;
        document.getElementById("editPersAcc").value = persAcc;
        document.getElementById("editClName").value = clientName;
        document.getElementById("editAddress").value = address;
        document.getElementById("editMonth").value = getMonthName(parseInt(month));
        document.getElementById("editYear").value = year;
        document.getElementById("editIndication").value = indicationValue;

        window.editingIndication = {
            id: indId,
            personalAccount: persAcc,
            monthId: parseInt(month),
            yearId: parseInt(year)
        };

        const modal = new bootstrap.Modal(document.getElementById("indEdit"));
        modal.show();
    }

    // Update button handler
    const updateBtn = document.getElementById("updateIndBtn");
    if (updateBtn) {
        updateBtn.addEventListener("click", async function() {
            if (!window.editingIndication) return;

            const newIndication = document.getElementById("editIndication").value.trim();
            if (!newIndication) {
                alert("Введите показания");
                return;
            }

            const payload = {
                personalAccount: window.editingIndication.personalAccount,
                monthId: window.editingIndication.monthId,
                yearId: window.editingIndication.yearId,
                m3: parseFloat(newIndication)
            };

            try {
                const response = await fetch("/indications/addind", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });

                const text = await response.text();
                if (!response.ok) {
                    alert("Ошибка: " + text);
                    return;
                }

                alert("Показания обновлены!");
                
                const modalEl = document.getElementById("indEdit");
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();

                window.editingIndication = null;

                const selectedRow = document.querySelector("#clientsTab tbody tr.table-active");
                if (selectedRow) {
                    const persAcc = selectedRow.cells[2].textContent.trim();
                    const yearVal = yearSelect.value;
                    fetchIndicationsForClient(persAcc, yearVal);
                }
            } catch (err) {
                console.error("Ошибка отправки:", err);
                alert("Ошибка отправки: " + err.message);
            }
        });
    }
});