document.addEventListener("DOMContentLoaded", () => {
    const rows = document.querySelectorAll("table tbody tr");
    const yearSelect = document.getElementById("yearSelect");
    const secondTableBody = document.querySelector("#indicationsTable tbody");
    const tableRows = document.querySelectorAll("#clientsTab tbody tr"); // <-- оставляем только это
    const submitBtn = document.getElementById("submitIndBtn");

    // Замените существующий обработчик кликов по строкам на этот:

    tableRows.forEach(row => {
        row.addEventListener("click", () => {
            // убрать выделение со всех строк
            tableRows.forEach(r => r.classList.remove("table-active"));
            // подсветить текущую
            row.classList.add("table-active");

            // Загрузить показания для выбранного клиента
            const persAccount = row.querySelector("td:nth-child(3)").innerText;
            const year = yearSelect.value;

            fetchIndicationsForClient(persAccount, year);
        });
    });

    fetch("/clients/period/last")
        .then(res => res.json())
        .then(data => {
            const monthSelect = document.getElementById("monthSelect");
            const yearSelect = document.getElementById("yearSelect");

            if (monthSelect && yearSelect) {
                monthSelect.value = data.monthId;
                yearSelect.value = data.yearId;
            }
        })
        .catch(err => console.error("Ошибка загрузки периода:", err));

    rows.forEach(row => {
        row.addEventListener("click", () => {
            const persAccount = row.querySelector("td:nth-child(3)").innerText;
            const year = yearSelect.value;

            fetch(`/indications/${persAccount}/${year}`)
                .then(res => res.json())
                .then(data => {
                    secondTableBody.innerHTML = "";
                    data.forEach(ind => {
                        secondTableBody.innerHTML += `
                          <tr>
                            <td>${ind.monthId}</td>
                            <td>${ind.indication}</td>
                            <td>${ind.m3}</td>
                            <td>${ind.tariff}</td>
                            <td>${ind.summa}</td>
                            <td class="text-center">
                              <button class="btn btn-sm btn-success">Ред-ть</button>
                            </td>
                          </tr>`;
                    });
                });
        });
    });

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
            console.log(persAcc);

            fetch(`/clients/${persAcc}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Клиент не найден");
                    }
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
    //---------------------------------------------------------------------------------
    if (submitBtn) {
            submitBtn.addEventListener("click", async () => {
                const persAcc = document.getElementById("persAccInd").value.trim();
                const clInd = document.getElementById("clInd").value.trim();
                const month = document.getElementById("monthSelect").value;
                const year = document.getElementById("yearSelect").value;

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
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(payload)
                    });

                    const text = await response.text();

                    if (!response.ok) {
                        alert("Ошибка: " + text);
                        return;
                    }

                    alert("Успешно: " + text);

                    // закрыть модалку
                    const modalEl = document.getElementById("indAdd");
                    const modal = bootstrap.Modal.getInstance(modalEl);
                    if (modal) modal.hide();

                    // очистить поле показаний
                    document.getElementById("clInd").value = "";
                } catch (err) {
                    console.error("Ошибка отправки:", err);
                    alert("Ошибка отправки: " + err.message);
                }
            });
        }

    // Добавьте в indication.js

    // Обработка загрузки Excel файла
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

                // Обновляем таблицу показаний для текущего выбранного клиента
                const selectedRow = document.querySelector("#clientsTab tbody tr.table-active");
                if (selectedRow) {
                    const persAccount = selectedRow.cells[2].textContent.trim();
                    const yearVal = document.getElementById("yearSelect").value;
                    fetchIndicationsForClient(persAccount, yearVal);
                }

                // Очищаем поле файла
                fileInput.value = '';

                // Через 3 секунды закрываем модалку
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

    // Функция для обновления таблицы показаний
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
                    secondTableBody.innerHTML += `
                      <tr>
                        <td>${getMonthName(ind.monthId)}</td>
                        <td>${ind.indication || 0}</td>
                        <td>${ind.m3 || 0}</td>
                        <td>${ind.tariff || 0}</td>
                        <td>${ind.summa || 0}</td>
                        <td class="text-center">
                          <button class="btn btn-sm btn-success" onclick="editIndication(${ind.id})">Ред-ть</button>
                         </td>
                      </tr>`;
                });
            })
            .catch(err => console.error("Ошибка загрузки показаний:", err));
    }

    // Вспомогательная функция для получения названия месяца
    function getMonthName(monthId) {
        const months = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
                        'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'];
        return months[monthId - 1] || monthId;
    }
});
