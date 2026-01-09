document.addEventListener("DOMContentLoaded", () => {
    const rows = document.querySelectorAll("table tbody tr");
    const yearSelect = document.getElementById("yearSelect");
    const secondTableBody = document.querySelector("#indicationsTable tbody");
    const tableRows = document.querySelectorAll("#clientsTab tbody tr"); // <-- оставляем только это
    const submitBtn = document.getElementById("submitIndBtn");

    tableRows.forEach(row => {
        row.addEventListener("click", () => {
            // убрать выделение со всех строк
            tableRows.forEach(r => r.classList.remove("table-active"));
            // подсветить текущую
            row.classList.add("table-active");
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
});
