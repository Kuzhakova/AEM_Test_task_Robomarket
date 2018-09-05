<%@include file="/apps/robomarket-product/global.jsp" %>
<div class="card" style="width:500px">
    <img class="card-img-top" src="${properties.fileReference}" alt="Preview image">
    <div class="card-body">
        <h1 class="card-title pricing-card-title">$${properties.price}</h1>
        <h4 class="card-title">${properties.title}</h4>
        <p class="card-text">${properties.description}</p>
        <a class="btn btn-success btn-block" data-sc-offerid="${properties.offerId}" href="https://robo.market/offer/${properties.offerId}" target="_blank">Buy</a>
    </div>
</div>
