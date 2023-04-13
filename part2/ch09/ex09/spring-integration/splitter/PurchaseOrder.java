package sia5.splitter;

import lombok.Getter;

import java.util.List;

@Getter
public class PurchaseOrder {

    private BillingInfo billingInfo;
    private List<LineItem> lineItems;

    static class BillingInfo{

        private String info1;
        private String info2;
        private String info3;
        private String info4;
        private String info5;
    }

    static class LineItem{}
}
