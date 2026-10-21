(() => {
    const form = document.querySelector(".cart-layout form");
    const subtotalElement = document.querySelector("#cart-subtotal");
    if (!form || !subtotalElement) return;

    const currency = new Intl.NumberFormat("en-IN", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
    let submitTimer;

    const updateTotals = () => {
        let subtotal = 0;
        form.querySelectorAll(".cart-line").forEach((line) => {
            const quantityInput = line.querySelector('input[type="number"]');
            const quantity = Math.max(0, Number.parseInt(quantityInput.value, 10) || 0);
            const lineTotal = Number(line.dataset.unitPrice) * quantity;
            line.querySelector(".line-total-value").textContent = currency.format(lineTotal);
            subtotal += lineTotal;
        });
        subtotalElement.textContent = currency.format(subtotal);
    };

    form.addEventListener("input", (event) => {
        if (event.target.matches('input[type="number"]')) updateTotals();
    });

    form.addEventListener("change", (event) => {
        if (!event.target.matches('input[type="number"]')) return;
        window.clearTimeout(submitTimer);
        submitTimer = window.setTimeout(() => form.requestSubmit(), 250);
    });
})();
