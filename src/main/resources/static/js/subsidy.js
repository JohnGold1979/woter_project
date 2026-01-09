document.addEventListener("DOMContentLoaded", () => {
  const monthSelect = document.getElementById("monthSelect");
  const yearSelect = document.getElementById("yearSelect");

    const urlParams = new URLSearchParams(window.location.search);
    const monthParam = urlParams.get("month");
    const yearParam = urlParams.get("year");

    if (monthParam && yearParam) {
                  // Если есть параметры в URL → используем их
                  monthSelect.value = monthParam;
                  yearSelect.value = yearParam;
                  loadTotals(monthParam, yearParam);
              } else {
                  // Если параметров нет → тянем текущий период с бэка
                  fetch("/clients/period/last")
                      .then(res => res.json())
                      .then(data => {
                          if (monthSelect && yearSelect) {
                              monthSelect.value = data.monthId;
                              yearSelect.value = data.yearId;
                              loadTotals(data.monthId, data.yearId);
                          }
                      })
                      .catch(err => console.error("Ошибка загрузки периода:", err));
              }


     function loadTotals(month, year) {

                fetch(`/subsidy/totalSubsidy?month=${month}&year=${year}`)
                    .then(res => res.json())
                    .then(data => {
                        const tfoot = document.querySelector("#clientsTab tfoot tr");
                        if (tfoot) {
                            const cells = tfoot.querySelectorAll("td");
                              cells[1].innerText = data.totalAmount ?? "0";
                        }
                    })
                    .catch(err => console.error("Ошибка загрузки итогов:", err));
     }

 });

  document.addEventListener("keydown", function (event) {
        if (event.code == 'Enter' && event.ctrlKey) {
            let modal = new bootstrap.Modal(document.getElementById("addSub"));
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

 function uploadFile() {
     const formData = new FormData(document.getElementById("uploadForm"));
     fetch("/upload/payments", {
         method: "POST",
         body: formData
     })
     .then(res => res.text())
     .then(msg => alert(msg))
     .catch(err => alert("Ошибка загрузки: " + err));
 }

 function onPeriodChange() {
          const month = monthSelect.value;
          const year = yearSelect.value;
          console.log("Выбран период:", month, year);
          window.location.href = `/subsidy?month=${month}&year=${year}`;

  }

      if (monthSelect) {
          monthSelect.addEventListener("change", onPeriodChange);
      }

      if (yearSelect) {
          yearSelect.addEventListener("change", onPeriodChange);
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