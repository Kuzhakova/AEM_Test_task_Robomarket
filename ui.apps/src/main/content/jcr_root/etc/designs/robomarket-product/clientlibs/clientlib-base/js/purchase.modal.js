if (purchaseModal === undefined) {
    var purchaseModal = (function () {
        var modal;
        var span;

        window.onclick = function (event) {
            if (event.target == modal) {
                modal.style.display = "none";
            }
        };

        return {
            showModal: function (status) {
                if (!modal) {
                    var id;
                    if (status === "success") {
                        id = 'success-modal';
                    }
                    else if (status === "failure") {
                        id = 'failure-modal';
                    }
                    modal = document.getElementById(id);
                    span = document.getElementsByClassName("close")[0];
                    span.onclick = function () {
                        modal.style.display = "none";
                    };
                }
                modal.style.display = "block";
            }
        }
    }());
}

$.get(
    "/content/robomarket-product/en.html",
    onAjaxSuccess
);

function onAjaxSuccess(data) {
    var status = window
        .location
        .search
        .replace('?u=', '');
    purchaseModal.showModal(status);
}
