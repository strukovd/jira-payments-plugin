package kg.gazprom.payments.payments;

import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import kg.gazprom.payments.invoices.InvoiceDataModel;
import kg.gazprom.payments.utils.JiraApi;

import java.util.Date;

public class PaymentItem implements IssueAction {
    public PaymentDataModel paymentData;
    public boolean isFirstItem;
    private String scriptBlock = "<script>\n" +
            "function closeAllPaymentMenu() {\n" +
            "    document.querySelectorAll(`.menu-block-list`).forEach((el)=>{el.remove();});\n" +
            "}\n" +
            "\n" +
            "function deletePayment(id, type) {\n" +
            "    const url = `${location.origin}/rest/api/1/payment?id=${id}&type=${type}`;\n" +
            "    const res = fetch(url, { method:`DELETE`, headers: { \"Content-Type\": `application/json` }, } )\n" +
            "        .then((response)=>{\n" +
            "            if( response.ok ) AJS.flag({ type: 'info', title: 'Успешно удалено!', close: \"auto\" });\n" +
            "            else throw new Error(`${response.status} ${response.statusText}`);\n" +
            "        })\n" +
            "        .catch((err)=>{\n" +
            "            AJS.flag({ type: 'error', title: 'Не удалось удалить оплату!', body: err.message, close: \"auto\" });\n" +
            "        })\n" +
            "}\n" +
            "\n" +
            "function onClickPaymentMenuItem(e) {\n" +
            "    const menuItem = e.currentTarget;\n" +
            "    const curRow = menuItem.parentElement.parentElement.parentElement.parentElement.parentElement;" +
            "    const actionKey = menuItem.dataset.actionKey;\n" +
            "    switch(actionKey) {\n" +
            "        case 'remove': {\n" +
            "            console.log(`dsasda`);" +
            "            const isBilling = curRow.querySelectorAll(`div`)[5].innerText[1] === '0';\n" +
            "            const paymentId = curRow.querySelectorAll(`div`)[0].innerText;\n" +
            "            const isAgree = confirm(`Вы уверены что хотите удалить оплату (№ ${paymentId})?`);\n" +
            "            if(isAgree) {\n" +
            "\t\t\t\tdeletePayment(paymentId, isBilling ? 'billing' : 'replenishment');\n" +
            "\t\t\t\tcurRow.remove();\n" +
            "\t\t\t}\n" +
            "\t\t\tbreak;\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "function toggleMenu(e) {\n" +
            "    const menuBtn = e.currentTarget;\n" +
            "    const menuBlock = menuBtn.querySelector(`.menu-block-list`);\n" +
            "    if(menuBlock) {\n" +
            "        // menuBlock.remove();\n" +
            "        closeAllPaymentMenu();\n" +
            "    } else {\n" +
            "        closeAllPaymentMenu();\n" +
            "        menuBtn.insertAdjacentHTML(`beforeEnd`, `<div class=\"menu-block-list\">\n" +
            "\t\t\t<ul>\n" +
            "\t\t\t\t<li data-action-key='remove'>\\uD83D\\uDDD1 Удалить</li>\n" +
            "\t\t\t</ul>\n" +
            "\t\t</div>`);\n" +
            "        menuBtn.querySelectorAll(`ul li:not(.disabled)`).forEach( (el)=>{el.addEventListener(`click`, onClickPaymentMenuItem);} );\n" +
            "        document.addEventListener(`click`, closeAllPaymentMenu, {once: true});\n" +
            "        e.stopPropagation();\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "setTimeout(()=>{\n" +
            "    document.querySelectorAll(`.payment-menu-button`).forEach(\n" +
            "        (el)=>{\n" +
            "            el.addEventListener(`click`, toggleMenu);\n" +
            "        }\n" +
            "    )\n" +
            "}, 500);\n" +
            "</script>";
    private String styleBlock = "<style>\n" +
            ".inv-item {\n" +
            "    font: normal 13px sans-serif;\n" +
            "}\n" +
            ".inv-content {\n" +
            "    padding: .5em .5em .5em 1em;\n" +
            "    margin-bottom: 0.8em;\n" +
            "    background: #f8f8f8;\n" +
            "    border-left: 4px solid #4a84ff;\n" +
            "}\n" +
            ".inv-data {\n" +
            "    display: flex;\n" +
            "}\n" +
            ".left-section {\n" +
            "    flex: auto;\n" +
            "}\n" +
            ".left-section invoice-title {\n" +
            "    padding-bottom:.3em;\n" +
            "    font:700 1.1em sans-serif;\n" +
            "}\n" +
            ".service-name-section {}\n" +
            ".wr-service-name {}\n" +
            ".service-name-value {\n" +
            "    font-weight: 700;\n" +
            "}\n" +
            ".service-amount-section {}\n" +
            ".wr-service-amount {}\n" +
            ".service-amount-value {\n" +
            "    color: darkgreen;\n" +
            "    font-weight: 700;\n" +
            "}\n" +
            "\n" +
            ".right-section {\n" +
            "    flex: auto;\n" +
            "}\n" +
            ".payment-menu-button {\n" +
            "    position: relative;\n" +
            "    padding: 0 .4em !important;\n" +
            "    margin: 0;\n" +
            "    background: #ededed;\n" +
            "    user-select: none;\n" +
            "    cursor: pointer;\n" +
            "    color: #172b4d63;\n" +
            "    margin-left: .3em;\n" +
            "    border-radius: 3px;\n" +
            "    font-weight: 700;\n" +
            "    float: right;\n" +
            "    box-shadow: 0 0 1px 0 rgba(0,0,0,.2);\n" +
            "}\n" +
            ".menu-block-list {\n" +
            "    z-index: 1000;\n" +
            "    position: absolute;\n" +
            "    right: 0;\n" +
            "    top: 2em;\n" +
            "    background: #fff;\n" +
            "    box-shadow: 0 0 3px 0 rgba(0,0,0,.1);\n" +
            "    border-radius: 3px;\n" +
            "    color: initial;\n" +
            "}\n" +
            ".menu-block-list ul {\n" +
            "    list-style: none;\n" +
            "    padding: 0;\n" +
            "    line-height: 2em;\n" +
            "    white-space: nowrap;\n" +
            "    font-weight: 400;\n" +
            "}\n" +
            ".menu-block-list ul li {\n" +
            "    padding: 0 1.2em;\n" +
            "    border-bottom: 1px solid #f5f5f5;\n" +
            "}\n" +
            ".menu-block-list ul li:not(.disabled):hover {\n" +
            "    background: #f5f5f5;\n" +
            "}\n" +
            ".menu-block-list ul li.disabled {\n" +
            "    cursor: default;\n" +
            "    filter: opacity(0.3);\n" +
            "}\n" +
            ".inner-paid-stamp { font-size:.8em; background:#7bdb7f; color:#1b603d; padding:.2em .6em; border-radius:3px; font-weight:700; }\n" +
            ".inner-unpaid-stamp { font-size:.8em; background:#ffdd77; color:#877631; padding:.2em .6em; border-radius:3px; font-weight:700; }\n" +
            ".paid-stamp {\n" +
            "    background: #7bdb7f;\n" +
            "    color: #1b603d;\n" +
            "    padding: .2em .6em;\n" +
            "    border-radius: 3px;\n" +
            "    font-weight: 700;\n" +
            "    float: right;\n" +
            "}\n" +
            ".unpaid-stamp {\n" +
            "    background: #ffdd77;\n" +
            "    color: #877631;\n" +
            "    padding: .2em .6em;\n" +
            "    border-radius: 3px;\n" +
            "    font-weight: 700;\n" +
            "    float: right;\n" +
            "}\n" +
            "\n" +
            "section.history {}\n" +
            ".payment-history {\n" +
            "    display: flex;\n" +
            "}\n" +
            ".payment-history>span {\n" +
            "    flex: auto 1 0;\n" +
            "}\n" +
            ".payment-history>span .amount {\n" +
            "    color: darkgreen;\n" +
            "    font-weight: 700;\n" +
            "}\n" +
            ".history-section .wr-title {\n" +
            "    padding:.5em 0;\n" +
            "}\n" +
            ".history-section .title {\n" +
            "    padding-bottom:.3em;\n" +
            "    font:700 1.1em sans-serif;\n" +
            "}\n" +
            "</style>";


    PaymentItem(PaymentDataModel data) {this.paymentData = data;}

    PaymentItem(PaymentDataModel data, boolean withStyles) {
        this.paymentData = data;
        this.isFirstItem = withStyles;
    }



    public String getHtml() {
        String itemBorderColor;
        if(this.paymentData.serviceId != 0)
            itemBorderColor = "#f5bf02";
        else
            itemBorderColor = "#4a84ff";

        String actionList;
        if( JiraApi.isCurrentUserInGroup("admin2") ) {
            actionList = "<span class='payment-menu-button'>⋮</span>";
        }
        else {
            actionList = "";
        }

        String itemCode = "<div style=\"display:flex;background: #f8f8f8;flex-wrap: wrap;border-left: 4px solid "+ itemBorderColor +";margin-bottom: .5em;padding: .5em .5em;\">\n" +
                    "<div style=\"display:none;\">"+ this.paymentData.id +"</div>\n" +
                    "<div style=\"flex:15% 1 0\">"+ this.paymentData.transactionId +"</div>\n" +
                    "<div style=\"flex:20% 1 0\">"+ this.paymentData.date +"</div>\n" +
                    "<div style=\"flex:5% 1 0; color:darkgreen; font-weight:700;\">"+ this.paymentData.amount +"</div>\n" +
                    "<div style=\"flex:25% 1 0\">"+ this.paymentData.sender +"</div>\n" +
                    "<div style=\"flex:auto 1 0\">["+ this.paymentData.serviceId +"] "+ this.paymentData.serviceName +"</div>\n" +
                    "<div style=\"flex:25px 1 0\">"+ actionList +"</div>\n" +
                "</div>";

        if (isFirstItem) {
            itemCode = styleBlock + scriptBlock + itemCode;
        }

        return itemCode;
    }

    public Date getTimePerformed() {
        return new Date();
    }

    public boolean isDisplayActionAllTab() {
        return true;
    }
}
