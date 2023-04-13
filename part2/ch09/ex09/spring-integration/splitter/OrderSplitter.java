package sia5.splitter;

import java.util.ArrayList;
import java.util.Collection;

public class OrderSplitter {

    /**
     * POJO 특정 프레임워크 기술에 종속되지 않은 순수한 자바 객체
     */
    public Collection<Object> splitOrderIntoParts(PurchaseOrder po){
        ArrayList<Object> parts = new ArrayList<>();
        parts.add(po.getBillingInfo());
        parts.add(po.getLineItems());
        return parts;
    }
}
