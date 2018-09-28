<%@include file="/apps/robomarket-product/global.jsp" %>
<c:set var="product" value="${sling:adaptTo(resource, 'robo.market.core.models.RobomarketProductModel')}"/>
<c:if test="${not empty product}">
    <div class="card" style="width:500px">
        <img class="card-img-top" src="${properties.fileReference}" alt="Preview image">
        <div class="card-body">
            <h3 class="card-title pricing-card-title"><fmt:formatNumber value="${product.price}" type="currency" currencySymbol=""/> RUB</h3>
            <h4 class="card-title">${product.title}</h4>
            <p class="card-text">${product.description}</p>
            <a class="btn btn-success btn-block" data-sc-offerid="${product.offerId}"
               href="https://robo.market/offer/${product.offerId}" target="_blank">Buy</a>
        </div>
    </div>
</c:if>

<script>
    $(document).ready(function() {
        purchaseModal.onLoad();
    })
</script>