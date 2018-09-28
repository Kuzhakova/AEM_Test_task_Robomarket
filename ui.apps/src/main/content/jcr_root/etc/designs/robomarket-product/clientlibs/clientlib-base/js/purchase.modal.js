purchaseModal = (function () {
    var modal;
    var span;

    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    };

    var showModal = function (status) {
        if (!modal) {
            var id;
            if (status === "success") {
                id = 'success-modal';
            }
            else if (status === "failure") {
                id = 'failure-modal';
            }
            modal = document.getElementById(id);
            span = modal.getElementsByClassName("close")[0];
            span.onclick = function () {
                modal.style.display = "none";
            };
        }
        modal.style.display = "block";
    };

    return {
        onLoad: function () {
            var status = window
                .location
                .search
                .replace('?u=', '');
            showModal(status);
        }
    }
}());
