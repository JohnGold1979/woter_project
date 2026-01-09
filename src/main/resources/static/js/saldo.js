document.addEventListener("DOMContentLoaded", () => {
    const jumpBtn = document.getElementById("jumpMonthBtn");
    const monthSelect = document.getElementById("monthSelect");
    const yearSelect = document.getElementById("yearSelect");
    const toggle = document.querySelector("#stations-toggle");
    const list = document.querySelector("#stations-list");
    let isLoaded = false;

    // Читаем параметры из URL
    const urlParams = new URLSearchParams(window.location.search);
    const monthParam = urlParams.get("month");
    const yearParam = urlParams.get("year");

    if (monthParam && yearParam) {
        // Если есть параметры в URL → используем их
        monthSelect.value = monthParam;
        yearSelect.value = yearParam;
      //  loadTotals(monthParam, yearParam);
    } else {
        // Если параметров нет → тянем текущий период с бэка
        fetch("/clients/period/last")
            .then(res => res.json())
            .then(data => {
                if (monthSelect && yearSelect) {
                    monthSelect.value = data.monthId;
                    yearSelect.value = data.yearId;
                  //  loadTotals(data.monthId, data.yearId);
                }
            })
            .catch(err => console.error("Ошибка загрузки периода:", err));
    }

    if (jumpBtn) {
        jumpBtn.addEventListener("click", async () => {
            try {
                console.log('jumpBtn click');
                const response = await fetch("/saldo/jumpMonth");
                const text = await response.text();
                alert("Ответ сервера: " + text);
            } catch (err) {
                alert("Ошибка: " + err.message);
            }
        });
    }

    /* function loadTotals(month, year) {
        fetch(`/saldo/total?month=${month}&year=${year}`)
            .then(res => res.json())
            .then(data => {
                const tfoot = document.querySelector("#saldoTab tfoot tr");
                if (tfoot) {
                    const cells = tfoot.querySelectorAll("td");
                    cells[1].innerText = data.debetIn ?? "0";
                    cells[2].innerText = data.credetIn ?? "0";
                    cells[3].innerText = data.chargedMoney ?? "0";
                    cells[4].innerText = data.paydIn ?? "0";
                    cells[5].innerText = data.taxIn ?? "0";
                    cells[6].innerText = data.subsidy ?? "0";
                    cells[7].innerText = data.taxOut ?? "0";
                    cells[8].innerText = data.removalIn ?? "0";
                    cells[9].innerText = data.debetOut ?? "0";
                    cells[10].innerText = data.credetOut ?? "0";
                }
            })
            .catch(err => console.error("Ошибка загрузки итогов:", err));
    }
    */

    function onPeriodChange() {
        const month = monthSelect.value;
        const year = yearSelect.value;
        console.log("Выбран период:", month, year);
        // Перегружаем с новыми параметрами → сохранится выбор
        window.location.href = `/saldo?month=${month}&year=${year}`;
    }

    if (monthSelect) { monthSelect.addEventListener("change", onPeriodChange); }
    if (yearSelect) { yearSelect.addEventListener("change", onPeriodChange); }

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
                                   <a class="nav-link py-1 px-3 text-secondary" href="/saldo/getSaldoByStation?stationId=${s.stationId}">
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
});

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
