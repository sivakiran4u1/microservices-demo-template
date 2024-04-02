const { Given, When, Then } = require('@cucumber/cucumber');
const axios = require('axios');
const assert = require('assert').strict;
const BASE_URL = process.env.machine_dns;

const products = [
    "0PUK6V6EV0", "1YMWWN1N4O", "2ZYFJ3GM2N",
    "66VCHSJNUP", "6E92ZMYYFZ", "9SIQT8TOJO",
    "L9ECAV7KIM", "LS4PSXUNUM", "OLJCESPC7Z"
];



When('A user browses products', async function () {
    console.log(BASE_URL)
    for (const product of products) {
        const response = await axios.get(`${BASE_URL}/product/${product}`);
        assert.equal(response.status, 200, `Failed to load the product: ${product}`);
    }
});

When('A user views their cart', async function () {
    const response = await axios.get(`${BASE_URL}/cart`);
    assert.equal(response.status, 200, "Failed to view the cart");
});

When('A user adds products to their cart', async function () {
    for (const product of products) {
        const response = await axios.post(`${BASE_URL}/cart`, `product_id=${product}&quantity=1`, {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
        assert.equal(response.status, 200, `Failed to add the product to the cart: ${product}`);
    }
});

When('A user accesses site assets', async function () {
    const responses = await Promise.all([
        axios.get(`${BASE_URL}/static/favicon.ico`),
        axios.get(`${BASE_URL}/static/img/products/hairdryer.jpg`)
    ]);
    responses.forEach(response => {
        assert.equal(response.status, 200, "Failed to load site assets");
    });
});

When('A user checks out with products', async function () {
    const response = await axios.post(`${BASE_URL}/cart/checkout`, 
        "email=someone%40example.com&street_address=1600+Amphitheatre+Parkway&zip_code=94043&city=Mountain+View&state=CA&country=United+States&credit_card_number=4432-8015-6152-0454&credit_card_expiration_month=1&credit_card_expiration_year=2025&credit_card_cvv=672", {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
    assert.equal(response.status, 200, `Failed to checkout: ${response.status}`);
});

async function testSession() {
    const response = await axios.get(`${BASE_URL}/`);
    assert.equal(response.status, 200, "Failed to load the homepage");
}
