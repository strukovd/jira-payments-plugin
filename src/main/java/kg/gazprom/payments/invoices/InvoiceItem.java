package kg.gazprom.payments.invoices;

import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import java.util.Date;
import kg.gazprom.payments.utils.JiraApi;


public class InvoiceItem implements IssueAction {
    public InvoiceDataModel invoiceData;
    public boolean isFirstItem;
    private String scriptBlock = "<script>" +
            "function closeAllPaymentMenu() {\n" +
            "    document.querySelectorAll(`.menu-block-list`).forEach((el)=>{el.remove();});\n" +
            "}" +

            "function changePaymentStatus(invoiceId, isPaid) {\n" +
            "    const url = `${location.origin}/rest/api/1/invoice/status?${new URLSearchParams({ id: invoiceId, isPaid })}`;\n" +
            "    // const body = {issueKey:`\"+ issueKey +\"`, subTaskKeys: subTaskKeys};\n" +
            "    const res = fetch(url, { method:`PUT`, headers: { \"Content-Type\": `application/json` }, } )\n" +
            "        .then((response)=>{\n" +
            "            if( response.ok ) AJS.flag({ type: 'info', title: 'Статус счёта к оплате успешно обновлен!', close: \"auto\" });\n" +
            "            else throw new Error();\n" +
            "        })\n" +
            "        .catch((err)=>{\n" +
            "            AJS.flag({ type: 'error', title: 'Не удалось статус счета к оплате!', body: err.message, close: \"auto\" });\n" +
            "        })\n" +
            "}" +

            "function changeInvoiceReqAmount(invoiceId, reqAmount) {\n" +
            "    const url = `${location.origin}/rest/api/1/invoice/amount?${new URLSearchParams({ id: invoiceId, reqAmount })}`;\n" +
            "    const res = fetch(url, { method:`PUT`, headers: { \"Content-Type\": `application/json` }, } )\n" +
            "        .then((response)=>{\n" +
            "            if( response.ok ) AJS.flag({ type: 'info', title: 'Необходимая сумма для оплаты успешно обновлена!', close: \"auto\" });\n" +
            "            else throw new Error();\n" +
            "        })\n" +
            "        .catch((err)=>{\n" +
            "            AJS.flag({ type: 'error', title: 'Не обновить сумму к оплате!', body: err.message, close: \"auto\" });\n" +
            "        })\n" +
            "}" +

            "function onClickPaymentMenuItem(e) {" +
            "    const menuItem = e.currentTarget;" +
            "    const actionKey = menuItem.dataset.actionKey;" +
            "    switch(actionKey) {" +
            "        case 'toPaid': {" +
            "            const invoiceId = menuItem.parentElement.parentElement.parentElement.parentElement.parentElement.querySelector(`.invoice-title`).innerText.replace(/^.*№ /, '');" +
            "            const isAgree = confirm(`Вы уверены что хотите безвозвратно сменить статус из \"Не оплачен\" в \"Оплачен\"?`);" +
            "            if(isAgree) changePaymentStatus(invoiceId, true);" +
            "            break;" +
            "        }" +
            "        case 'toUnpaid': {" +
            "            const invoiceId = menuItem.parentElement.parentElement.parentElement.parentElement.parentElement.querySelector(`.invoice-title`).innerText.replace(/^.*№ /, '');" +
            "            const isAgree = confirm(`Вы уверены что хотите безвозвратно сменить статус с \"Оплачен\" в \"Не оплачен\"?`);" +
            "            if(isAgree) changePaymentStatus(invoiceId, false);" +
            "            break;" +
            "        }" +
            "        case 'changeAmount': {" +
            "            const invoiceId = menuItem.parentElement.parentElement.parentElement.parentElement.parentElement.querySelector(`.invoice-title`).innerText.replace(/^.*№ /, '');" +
            "            const prevAmount = menuItem.parentElement.parentElement.parentElement.parentElement.parentElement.querySelector(`.service-amount-value`).innerText.replace(` сом`, ``);" +
            "            const newAmount = prompt(`Внимание, сумма счета будет безвозвратно обновлена! Предыдущая сумма ${prevAmount}, введите новое значение:`, prevAmount);" +
            "            if(newAmount) changeInvoiceReqAmount(invoiceId, newAmount);" +
            "            break;" +
            "        }" +
            "        case 'remove': {" +
            "            const invoiceId = menuItem.parentElement.parentElement.parentElement.parentElement.parentElement.querySelector(`.invoice-title`).innerText.replace(/^.*№ /, '');" +
            "            const isAgree = confirm(`Вы уверены что хотите удалить счет на оплату (№ ${invoiceId})?`);" +
            "            break;" +
            "        }" +
            "    }" +
            "}" +

            "function toggleMenu(e) {\n" +
            "    const menuBtn = e.currentTarget;\n" +
            "    const menuBlock = menuBtn.querySelector(`.menu-block-list`);\n" +
            "    if(menuBlock) {\n" +
            "        // menuBlock.remove();\n" +
            "        closeAllPaymentMenu();\n" +
            "    } else {\n" +
            "        closeAllPaymentMenu();" +
            "        menuBtn.insertAdjacentHTML(`beforeEnd`, `<div class=\"menu-block-list\"><ul><li data-action-key='toPaid'>\uD83D\uDC49 В <span class='inner-paid-stamp'>Оплачен</span></li><li data-action-key='toUnpaid'>\uD83D\uDC48 В <span class='inner-unpaid-stamp'>Не оплачен</span></li><li data-action-key='changeAmount'>\uD83D\uDCB5 Изменить сумму оплаты</li><li data-action-key='remove' class=\"disabled\">\uD83D\uDDD1 Удалить</li></ul></div>`);\n" +
            "        menuBtn.querySelectorAll(`ul li:not(.disabled)`).forEach( (el)=>{el.addEventListener(`click`, onClickPaymentMenuItem);} );" +
            "        document.addEventListener(`click`, closeAllPaymentMenu, {once: true});" +
            "        e.stopPropagation();\n" +
            "    }\n" +
            "}\n" +

            "setTimeout(()=>{\n" +
            "    document.querySelectorAll(`.menu-button`).forEach(\n" +
            "        (el)=>{\n" +
            "            el.addEventListener(`click`, toggleMenu);\n" +
            "        }\n" +
            "    )\n" +
            "}, 500);" +
            "</script>";
    private String styleBlock = "<style>" +
            ".inv-item {" +
                "font: normal 13px sans-serif;" +
            "}" +
            ".inv-content {" +
                "padding: .5em .5em .5em 1em;" +
                "margin-bottom: 0.8em;" +
                "background: #f8f8f8;" +
                "border-left: 4px solid #4a84ff;" +
            "}" +
            ".inv-data {" +
                "display: flex;" +
            "}" +
            ".left-section {" +
                "flex: auto;" +
            "}" +
            ".left-section invoice-title {" +
                "padding-bottom:.3em;" +
                "font:700 1.1em sans-serif;" +
            "}" +
            ".service-name-section {}" +
            ".wr-service-name {}" +
            ".service-name-value {" +
                "font-weight: 700;" +
            "}" +
            ".service-amount-section {}" +
            ".wr-service-amount {}" +
            ".service-amount-value {" +
                "color: darkgreen;" +
                "font-weight: 700;" +
            "}" +

            ".right-section {" +
                "flex: auto;" +
            "}" +
            ".menu-button {" +
            "    position: relative;" +
            "    background: #ededed;" +
            "    user-select: none;" +
            "    cursor: pointer;" +
            "    color: #172b4d63;" +
            "    margin-left: .3em;" +
            "    padding: .2em .4em;" +
            "    border-radius: 3px;" +
            "    font-weight: 700;" +
            "    float: right;" +
            "    box-shadow: 0 0 1px 0 rgba(0,0,0,.2);" +
            "}" +
            ".menu-block-list {" +
            "    z-index: 1000;" +
            "    position: absolute;" +
            "    right: 0;" +
            "    top: 2em;" +
            "    background: #fff;" +
            "    box-shadow: 0 0 3px 0 rgba(0,0,0,.1);" +
            "    border-radius: 3px;" +
            "    color: initial;" +
            "}" +
            ".menu-block-list ul {" +
            "    list-style: none;" +
            "    padding: 0;" +
            "    line-height: 2em;" +
            "    white-space: nowrap;" +
            "    font-weight: 400;" +
            "}" +
            ".menu-block-list ul li {" +
            "    padding: 0 1.2em;" +
            "    border-bottom: 1px solid #f5f5f5;" +
            "}" +
            ".menu-block-list ul li:not(.disabled):hover {" +
            "    background: #f5f5f5;" +
            "}" +
            ".menu-block-list ul li.disabled {" +
            "    cursor: default;" +
            "    filter: opacity(0.3);" +
            "}" +
            ".inner-paid-stamp { font-size:.8em; background:#7bdb7f; color:#1b603d; padding:.2em .6em; border-radius:3px; font-weight:700; }" +
            ".inner-unpaid-stamp { font-size:.8em; background:#ffdd77; color:#877631; padding:.2em .6em; border-radius:3px; font-weight:700; }" +
            ".paid-stamp {" +
                "background: #7bdb7f;" +
                "color: #1b603d;" +
                "padding: .2em .6em;" +
                "border-radius: 3px;" +
                "font-weight: 700;" +
                "float: right;" +
            "}" +
            ".unpaid-stamp {" +
                "background: #ffdd77;" +
                "color: #877631;" +
                "padding: .2em .6em;" +
                "border-radius: 3px;" +
                "font-weight: 700;" +
                "float: right;" +
            "}" +

            "section.history {}" +
            ".payment-history {" +
                "display: flex;" +
            "}" +
            ".payment-history>span {" +
                "flex: auto 1 0;" +
            "}" +
            ".payment-history>span .amount {" +
                "color: darkgreen;" +
                "font-weight: 700;" +
            "}" +
            ".history-section .wr-title {" +
                "padding:.5em 0;" +
            "}" +
            ".history-section .title {" +
                "padding-bottom:.3em;" +
                "font:700 1.1em sans-serif;" +
            "}" +
            "</style>";

    InvoiceItem(InvoiceDataModel data) {
        this.invoiceData = data;
    }

    InvoiceItem(InvoiceDataModel data, boolean withStyles) {
        this.invoiceData = data;
        this.isFirstItem = withStyles;
    }

    public String getHtml() {
        String historySection;
        StringBuilder preHistorySection = new StringBuilder();

        // Генерируем блок истории платежей для тек. счета
        if(this.invoiceData.history != null) {
            preHistorySection.append("<section class='history-section'>" +
                    "<div class='wr-title'>" +
                        "<span class='title'>История платежей:</span>" +
                    "</div>");

            int count = 0;
            while (this.invoiceData.history.size() > count) {

                preHistorySection.append(
                        "<div class='payment-history'>" +
                            "<span class='transId'>"+ this.invoiceData.history.get(count).transactionId +"</span>" +
                            "<span class='date'>"+ this.invoiceData.history.get(count).date +"</span>" +
                            "<span class='amount'>" +
                                "<span class='amount'>"+ this.invoiceData.history.get(count).amount +" сом</span>" +
                            "</span>" +
                            "<span class='sender'>" +
                                "<span>"+ this.invoiceData.history.get(count).sender +"</span>" +
                            "</span>" +
                        "</div>");
                count++;
            }

            preHistorySection.append("</section>");
            historySection = preHistorySection.toString();
        }
        else {
            historySection = "";
        }


        // Определим статус оплаты
        String stamp;
        if (this.invoiceData.payed) {
            stamp = "<span class='paid-stamp'>Оплачен</span>";
        }
        else {
            stamp = "<span class='unpaid-stamp'>Не оплачен</span>";
        }


        // Если пользователь состоит в группе admin2, то ему добавим вып. список функций
        String actionList;
        if( JiraApi.isCurrentUserInGroup("admin2") ) {
            actionList = "<span class='menu-button'>⋮</span>";
        }
        else {
            actionList = "";
        }

        String itemCode =
            "<article class='inv-item'>" +
                "<main class='inv-content'>" +
                    "<section class='inv-data'>" +
                        "<div class='left-section'>" +
                            "<div class='invoice-title'>Счет на оплату № "+ this.invoiceData.id +"</div>" +
                            "<div class='service-name-section'>" +
                                "<span class='wr-service-name'>Услуга ["+ this.invoiceData.serviceId +"]: " +
                                    "<span class='service-name-value'>" + this.invoiceData.serviceName +"</span>" +
                                "</span>" +
                            "</div>" +
                            "<div class='service-amount-section'>" +
                                "<span class='wr-service-amount'>К оплате: " +
                                    "<span class='service-amount-value'>" +
                                        this.invoiceData.reqAmount + " сом" +
                                    "</span>" +
                                "</span>" +
                            "</div>" +
                        "</div>" +
                        "<div class='right-section'>" +
                            actionList +
                            stamp +
                        "</div>" +
                    "</section>" +
                historySection +
                "</main>" +
            "</article>";

        if (isFirstItem) {
            itemCode = styleBlock + scriptBlock + itemCode;
        }

        return itemCode;
    }

    public Date getTimePerformed() {
        // Вернуть дату платежа
        return new Date();
    }

    public boolean isDisplayActionAllTab() {
        return true;
    }

}
