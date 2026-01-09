 document.querySelectorAll('#printForm button[type="submit"]').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('hiddenMonth').value = document.getElementById('monthSelect').value;
            document.getElementById('hiddenYear').value = document.getElementById('yearSelect').value;
        });
 });