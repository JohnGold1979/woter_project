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
            fetch(`/pay/totalPay?month=${month}&year=${year}`)
                .then(res => res.json())
                .then(data => {
                    const tfoot = document.querySelector("#payTable tfoot tr");
                    if (tfoot) {
                        const cells = tfoot.querySelectorAll("td");
                          cells[1].innerText = data.totalPayIn ?? "0";
                          cells[2].innerText = data.totalAmount ?? "0";
                          cells[3].innerText = data.totalTaxIn ?? "0";
                    }
                })
                .catch(err => console.error("Ошибка загрузки итогов:", err));
        }

    function onPeriodChange() {
             const month = monthSelect.value;
             const year = yearSelect.value;
             console.log("Выбран период:", month, year);
             window.location.href = `/pay?month=${month}&year=${year}`;

    }

     if (monthSelect) { monthSelect.addEventListener("change", onPeriodChange); }
     if (yearSelect) {  yearSelect.addEventListener("change", onPeriodChange);  }

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
