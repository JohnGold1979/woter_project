document.addEventListener("DOMContentLoaded", () => {
const accountInput = document.getElementById("searchAccount");
const nameInput = document.getElementById("searchName");
const table = document.getElementById("clientsTab").getElementsByTagName("tbody")[0];
const addEditClientModal = new bootstrap.Modal(document.getElementById('AddEditClient'));
const payModal = new bootstrap.Modal(document.getElementById('PayClient'));
const payBtn = document.getElementById("openPaymentModal");
document.getElementById("submitPaymentBtn").addEventListener("click", submitPayment);
document.getElementById("submitPaymentSubBtn").addEventListener("click", submitPaymentSub);
const toggle = document.querySelector("#stations-toggle");
const list = document.querySelector("#stations-list");
const btn = document.getElementById("catalogToggle");
const menu = document.getElementById("sidebarCatalog");

let isLoaded = false;




    function filterTable() {
        let accVal = accountInput.value.toLowerCase();
        let nameVal = nameInput.value.toLowerCase();

        Array.from(table.rows).forEach(row => {
            let acc = row.cells[1].textContent.toLowerCase();
            let name = row.cells[2].textContent.toLowerCase();

            if ((acc.includes(accVal) || accVal === "") &&
                (name.includes(nameVal) || nameVal === "")) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    }

    accountInput.addEventListener("input", filterTable);
    nameInput.addEventListener("input", filterTable);

    fetch("/clients/period/last")
            .then(res => res.json())
            .then(data => {
                const monthSelect = document.getElementById("monthSelect");
                const yearSelect = document.getElementById("yearSelect");

                if (monthSelect && yearSelect) {
                    monthSelect.value = data.monthId; // например 11 → Ноябрь
                    yearSelect.value = data.yearId;   // например 2011
                }
            })
    .catch(err => console.error("Ошибка загрузки периода:", err));




    document.querySelectorAll("#clientsTab tbody tr").forEach(row => {
     row.addEventListener("click", () => {
         document.querySelectorAll("#clientsTab tbody tr").forEach(r => r.classList.remove("table-active"));
         row.classList.add("table-active");
     });
   });

     document.addEventListener("keydown", function (event) {

     if (event.code == 'Enter' && (event.ctrlKey)) {
        let modal = new bootstrap.Modal(document.getElementById("PayClient"));
        modal.show();

        // допустим, у тебя выбранный абонент в таблице
        let selectedRow = document.querySelector("#clientsTab tbody tr.table-active");
        if (!selectedRow) {
            console.warn("Абонент не выбран!");
            return;
        }

        let persAcc = selectedRow.cells[1].textContent.trim();

        // Делаем запрос в Spring API
       fetch(`/clients/${persAcc}`)
           .then(response => {
               if (!response.ok) {
                   throw new Error("Клиент не найден");
               }
               return response.json();
           })
           .then(data => {
               document.getElementById("persAcc").value = data.personalAccount || "";
               document.getElementById("clName").value = data.clientName || "";
               document.getElementById("address").value = data.address || "";
           })
           .catch(err => console.error("Ошибка загрузки данных клиента:", err));
     }

     if (event.code == 'Enter' && (event.altKey)) {
         let modal = new bootstrap.Modal(document.getElementById("PayClientSubsidy"));
         modal.show();

          let selectedRow = document.querySelector("#clientsTab tbody tr.table-active");
          if (!selectedRow) {
             console.warn("Абонент не выбран!");
             return;
          }

          let persAcc = selectedRow.cells[1].textContent.trim();

          // Делаем запрос в Spring API
          fetch(`/clients/${persAcc}`)
          .then(response => {
              if (!response.ok) {
                  throw new Error("Клиент не найден");
              }
             return response.json();
          })
          .then(data => {
             document.getElementById("persAccSub").value = data.personalAccount || "";
             document.getElementById("clNameSub").value = data.clientName || "";
             document.getElementById("addressSub").value = data.address || "";
           })
           .catch(err => console.error("Ошибка загрузки данных клиента:", err));
     }

        const clPayInput = document.getElementById("clPay");
        const taxInput = document.getElementById("tax");

        if (clPayInput && taxInput) {
            clPayInput.addEventListener("input", () => {
                const amount = parseFloat(clPayInput.value);

                if (isNaN(amount) || amount <= 0) {
                    taxInput.value = "";
                    return;
                }

                fetch(`/clients/calc?amount=${encodeURIComponent(amount)}`)
                    .then(res => res.json())
                    .then(data => {
                        taxInput.value = data.tax.toFixed(2);
                    })
                    .catch(err => {
                        console.error("Ошибка при расчёте налога:", err);
                        taxInput.value = "Ошибка";
                    });
            });
        }
   });

    document.querySelector("table").addEventListener("click", function (event) {
           // Проверяем, куда кликнули
           if (event.target.classList.contains("pay-outline-btn")) {
               // Найдём строку (родительский <tr>)
               const row = event.target.closest("tr");
               const account = row.querySelector("td:nth-child(2)").innerText;
               console.log("Нажата кнопка 'Оплата' для счёта:", account);
               submitPayOutline(account);
           }

           if (event.target.classList.contains("subsidy-outline-btn")) {
               const row = event.target.closest("tr");
               const account = row.querySelector("td:nth-child(2)").innerText;
               console.log("Нажата кнопка 'Субсидия' для счёта:", account);
               submitSubsidyOutline(account);
           }

           if (event.target.classList.contains("addEditClient-outline-btn")) {
               const row = event.target.closest("tr");
               const account = row.querySelector("td:nth-child(2)").innerText;
               console.log("Нажата кнопка 'Добавить редактировать клиента' для счёта:", account);
               addEditClientOutline(account);
           }
       });


       toggle.addEventListener("click", async (e) => {
               e.preventDefault();
               // Переключаем видимость списка
               list.classList.toggle("show");

               // Если список уже был загружен — не подгружаем заново
               if (isLoaded) return;

               try {
                   const resp = await fetch("/clients/allStations");
                   const stations = await resp.json();

                   if (stations.length === 0) {
                       list.innerHTML = `<li class="nav-item px-3 text-muted small">Нет станций</li>`;
                   } else {
                       list.innerHTML = stations.map(s =>
                           `<li class="nav-item">
                               <a class="nav-link py-1 px-3 text-secondary" href="/clients/getByStation?stationId=${s.stationId}">
                                   <i class="fa-regular fa-circle me-2 text-info"></i>${s.stationName}
                               </a>
                           </li>`
                       ).join("");
                   }

                   isLoaded = true;
               } catch (err) {
                   console.error("Ошибка при загрузке станций:", err);
                   list.innerHTML = `<li class="nav-item px-3 text-danger small">Ошибка загрузки</li>`;
               }
           });

          //открыть/закрыть меню на планшете
         btn?.addEventListener("click", () => {
                 menu.classList.toggle("show");
         });

});
//**********************************************************************************************************************
//**********************************************************************************************************************
function submitPayOutline(account) {
    console.log("submitPayOutline ->", account);
    let modal = new bootstrap.Modal(document.getElementById("PayClient"));
    modal.show();
    let persAcc = account
                // Делаем запрос в Spring API
      fetch(`/clients/${persAcc}`)
              .then(response => {
                   if (!response.ok) {
                       throw new Error("Клиент не найден");
                   }
                   return response.json();
               })
               .then(data => {
                   document.getElementById("persAcc").value = data.personalAccount || "";
                   document.getElementById("clName").value = data.clientName || "";
                   document.getElementById("address").value = data.address || "";
               })
               .catch(err => console.error("Ошибка загрузки данных клиента:", err));
}
//**********************************************************************************************************************
function submitSubsidyOutline(account) {
    console.log("submitSubsidyOutline ->", account);
      let modal = new bootstrap.Modal(document.getElementById("PayClientSubsidy"));
      modal.show();
      let persAcc = account;

              // Делаем запрос в Spring API
              fetch(`/clients/${persAcc}`)
              .then(response => {
                  if (!response.ok) {
                      throw new Error("Клиент не найден");
                  }
                 return response.json();
              })
              .then(data => {
                 document.getElementById("persAccSub").value = data.personalAccount || "";
                 document.getElementById("clNameSub").value = data.clientName || "";
                 document.getElementById("addressSub").value = data.address || "";
               })
               .catch(err => console.error("Ошибка загрузки данных клиента:", err));
}
//***********************************************************************************************************************
function addEditClientOutline(account) {
    console.log("AddEditClient ->", account);
      let modal = new bootstrap.Modal(document.getElementById("AddEditClient"));
      modal.show();
      let persAcc = account;

              // Делаем запрос в Spring API
              fetch(`/clients/${persAcc}`)
              .then(response => {
                  if (!response.ok) {
                      throw new Error("Клиент не найден");
                  }
                 return response.json();
              })
              .then(data => {
                 document.getElementById("persAccEditAdd").value = data.personalAccount || "";
                 document.getElementById("clNameEditAdd").value = data.clientName || "";
               })
               .catch(err => console.error("Ошибка загрузки данных клиента:", err));


               fetch('/clients/streets/findByPersAcc?persAcc=' + persAcc)
                     .then(r => r.json())
                        .then(data => {
                         console.log("Получен список улиц:", data);

                            if (Array.isArray(data) && data.length > 0) {
                                // Берём первую улицу (или другую логику)
                                const s = data[0];

                                // Если select ещё не заполнен — заполняем
                                const select = $('#selectStreet');
                                select.empty().append('<option value="">Выберите улицу</option>');

                                data.forEach(street =>
                                    select.append(new Option(street.streetName, street.streetId))
                                );

                                // Выставляем выбранную улицу
                                select.val(s.streetId).change();
                            }
                       })
                       .catch(err => console.error("Ошибка поиска клиента по ЛС:", err));
}
//***********************************************************************************************************************
function submitPayment() {
    const persAcc = document.getElementById("persAcc").value.trim();
    const amount  = parseFloat(document.getElementById("clPay").value);
    const tax     = parseFloat(document.getElementById("tax").value) || 0;

    if (!persAcc) {
        alert("Ошибка: лицевой счёт обязателен!");
        return;
    }

    if (isNaN(amount) || amount <= 0) {
        alert("Ошибка: введите корректную сумму!");
        return;
    }

    const payment = { persAcc, amount, tax };

    fetch("/clients/payments", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payment)
    })
    .then(res => {
        if (!res.ok) throw new Error("Ошибка сервера");
        return res.text();
    })
    .then(msg => {
        alert(msg);
        bootstrap.Modal.getInstance(document.getElementById('PayClient')).hide();
        document.getElementById("payForm").reset();
    })
    .catch(err => {
        console.error("Ошибка:", err);
        alert("Не удалось добавить оплату");
    });
}
//***************************************************************************************
function submitPaymentSub() {
    const persAcc = document.getElementById("persAccSub").value.trim();
    const amount  = parseFloat(document.getElementById("clPaySub").value);

    if (!persAcc) {
        alert("Ошибка: лицевой счёт обязателен!");
        return;
    }

    if (isNaN(amount) || amount <= 0) {
        alert("Ошибка: введите корректную сумму!");
        return;
    }

    const payment = { persAcc, amount};

    fetch("/clients/paymentsSub", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payment)
    })
    .then(res => {
        if (!res.ok) throw new Error("Ошибка сервера");
        return res.text();
    })
    .then(msg => {
        alert(msg);
        bootstrap.Modal.getOrCreateInstance(document.getElementById('PayClientSub')).hide();
        document.getElementById("payFormSub").reset();
    })
    .catch(err => {
        console.error("Ошибка:", err);
        alert("Не удалось добавить оплату");
    });
}
//******** Ссылки **********************************************************************************
document.querySelectorAll(".sidebar a").forEach(link => {
    link.addEventListener("click", function (e) {
        e.preventDefault();
        let baseUrl = this.getAttribute("href");
        if (baseUrl && baseUrl !== "#") {
            window.location.href = baseUrl + getPeriodParams();
        }
    });
});

function getPeriodParams() {
    let month = document.getElementById("monthSelect").value;
    let year = document.getElementById("yearSelect").value;
    return `?month=${month}&year=${year}`;
}


