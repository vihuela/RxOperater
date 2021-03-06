package worldgo.rxoperator.operators.filter;

import rx.Observable;
import rx.functions.Func1;
import worldgo.rxoperator.operators.BaseOperate;

/**
 * @author ricky.yao on 2016/8/4.
 */
public class Distinct extends BaseOperate<Integer> {
    @Override
    protected void invoke() {
        Observable.just(1, 1, 1, 2, 3, 4, 5, 6, 10).distinct().subscribe(subscriber);
    }
}
